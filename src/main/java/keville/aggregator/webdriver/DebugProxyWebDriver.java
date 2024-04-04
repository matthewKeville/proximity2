package keville.aggregator.webdriver;

import java.time.Duration;
import java.time.LocalDateTime;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import keville.aggregator.scraping.HarUtil;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;

/* headful */
public class DebugProxyWebDriver implements ProxyWebDriver {

  private WebDriver webDriver;
  private BrowserMobProxy proxy;

  public DebugProxyWebDriver() {

    this.proxy = new BrowserMobProxyServer();
    proxy.start(0); /* can concurrent instances use the same port? */

    Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
    seleniumProxy.setHttpProxy("localhost:"+proxy.getPort());
    seleniumProxy.setSslProxy("localhost:"+proxy.getPort());

    ChromeOptions options = new ChromeOptions();
    options.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, PageLoadStrategy.EAGER); // wait for interactable (not loaded)
    options.setCapability(CapabilityType.PROXY, seleniumProxy);
    options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

    this.webDriver = new ChromeDriver(options);
    proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
    proxy.newHar("eventScanHar");

    webDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

  }

  public WebDriver getDriver() {
    return this.webDriver;
  }

  public BrowserMobProxy getProxy() {
    return this.proxy;
  }

  public void kill() {

    HarUtil.saveHARtoLFS(this.proxy.getHar(),"./logs/debug." + LocalDateTime.now().toString() + ".har");
    this.webDriver.quit();
    this.proxy.stop();
  }


}
