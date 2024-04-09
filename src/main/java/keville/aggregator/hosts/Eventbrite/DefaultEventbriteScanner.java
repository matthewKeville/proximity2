package keville.aggregator.hosts.Eventbrite;

import keville.aggregator.scanner.ScanReport;
import keville.aggregator.webdriver.ProxyWebDriver;
import keville.aggregator.webdriver.ProxyWebDriverFactory;
import keville.model.event.Event;
import keville.model.region.Region;
import keville.util.GeoUtils;

import java.time.Instant;

import java.util.stream.Collectors;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import net.lightbody.bmp.core.har.Har;

@Component
public class DefaultEventbriteScanner implements EventbriteScanner {

  private static Logger LOG = LoggerFactory.getLogger(DefaultEventbriteScanner.class);
  private ProxyWebDriverFactory proxyWebDriverFactory;
  private EventbriteHarProcessor eventbriteHarProcessor;

  public DefaultEventbriteScanner(
      @Autowired ProxyWebDriverFactory proxyWebDriverFactory,
      @Autowired EventbriteHarProcessor eventbriteHarProcessor) {
    this.proxyWebDriverFactory = proxyWebDriverFactory;
    this.eventbriteHarProcessor = eventbriteHarProcessor;
  }

  public ScanReport scan(Region region) {

    ProxyWebDriver proxyWebDriver = proxyWebDriverFactory.getInstance();

    Instant scanStart = Instant.now();
    String targetUrl = eventMapUrl(region.latitude, region.longitude, region.radius);

    LOG.info("targetting url \n" + targetUrl);
    proxyWebDriver.getDriver().get(targetUrl);

    // find the total number of result pages (broken 04/08)
    int pages = 0;
    try {
      String xPageOfKElementXPath = "/html/body/div[2]/div/div[2]/div/div/div/div[1]/div/main/div/div/section[1]/div/section/div/div/footer/div/div/ul/li[2]";
      WebElement xPageOfKElement = proxyWebDriver.getDriver().findElement(By.xpath(xPageOfKElementXPath));

      if (xPageOfKElementXPath != null) {
        String[] splits = xPageOfKElement.getText().split(" ");
        if (splits.length != 3) {
          LOG.error("expected 3 splits for xPageOfKElement string but found " + (splits.length));
          LOG.error(xPageOfKElement.getText());
        } else {
          pages = Integer.parseInt(splits[2]);
        }
      }
      LOG.info("found " + pages);
    } catch (Exception e) {
      LOG.error("unable to find the number of result pages");
      LOG.error(e.getMessage());
      LOG.error("defaulting to 1");
    }

    int pageLoadDelay_ms = 1000;/* 1 sec */
    int pagesToScrub = 1;
    if (pages != 0) {
      pagesToScrub = Math.min(10/* TODO : load max pages from settings*/, pages);
    }

    // scrub pages
    for (int i = 1; i < pagesToScrub; i++) {

      targetUrl = eventMapUrl(region.latitude, region.longitude, region.radius, i + 1);
      LOG.info(targetUrl);
      proxyWebDriver.getDriver().get(targetUrl);

      // This happens common enough that it makes sense to make a utility
      // TryWait that does not throw
      try {
        Thread.sleep(pageLoadDelay_ms);
      } catch (Exception e) {
        LOG.error("error sleeping thread");
        LOG.error(e.getMessage());
      }

      // Scroll to the bottom of the page
      JavascriptExecutor js = (JavascriptExecutor) proxyWebDriver.getDriver();
      js.executeScript("window.scrollBy(0,document.body.scrollHeight)", "");

    }

    Har har = proxyWebDriver.getProxy().getHar();
    proxyWebDriver.kill();

    Instant processStart = Instant.now();
    List<Event> events = eventbriteHarProcessor.process(har);

    events = events.stream()
        .distinct()
        .collect(Collectors.toList());

    return new ScanReport(scanStart, processStart, true, events);

  }

  static String eventMapUrl(double lat, double lon, double radius, int page) {

    GeoUtils.BBox bbox = GeoUtils.sphericalCapBbox(lat, lon, radius);

    String site = "https://www.eventbrite.com/";
    String locationPrefix = "d/united-states/belmar-new/";
    String mapPrefix = String.format("?page=%d&bbox=", page); // "?page=1&bbox=";
                                                              //
    return String.format("%s%16.14f%c2C%16.14f%c2C%16.14f%c2C%16.14f", site + locationPrefix + mapPrefix,
        bbox.a().getY(), '%', bbox.a().getX(), '%', bbox.b().getY(), '%', bbox.b().getX());

  }

  static String eventMapUrl(double lat, double lon, double radius) {
    return eventMapUrl(lat, lon, radius, 1);
  }

}
