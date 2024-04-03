package keville.background.scanner;

import keville.background.SelfSchedulingBackgroundTask;
import keville.background.scanner.Meetup.DefaultMeetupScanner;
import keville.background.scanner.dev.DevScanner;
import keville.model.event.EventTypeEnum;
import keville.model.region.Region;
import keville.repository.EventRepository;
import keville.repository.RegionRepository;
import keville.util.Iterables;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.time.Duration;

@Component
public class RegionScannerService extends SelfSchedulingBackgroundTask {

  private static Logger LOG = LoggerFactory.getLogger(RegionScannerService.class);
  private static final Duration delay = Duration.ofSeconds(60);
  private static final Duration startupDelay = Duration.ofSeconds(15);

  private Map<EventTypeEnum,EventScanner> scanners;
  private RegionRepository regionRepository;
  private EventRepository eventRepository;;

  public RegionScannerService(
      @Autowired RegionRepository regionRepository,
      @Autowired EventRepository eventRepository,
      @Autowired TaskScheduler taskScheduler,
      @Autowired DefaultMeetupScanner meetupScanner
      ) {

    super(taskScheduler,startupDelay,delay,"Region Scanner BG Service");
    this.regionRepository = regionRepository;
    this.eventRepository = eventRepository;

    //this probably belongs in a seperate class (injected into this one)
    this.scanners = new HashMap<EventTypeEnum,EventScanner>();
    this.scanners.put(EventTypeEnum.DEV,new DevScanner());
    this.scanners.put(EventTypeEnum.MEETUP,meetupScanner);
  }

  /*
   * Determine if any regions need to be scanned, if there are
   * scan across all providers.
   */
  public void doTask() {

    // Determine regions that need to be scanned

    Collection<Region> allRegions  = Iterables.toList(regionRepository.findAll());
    List<Region> regionsToBeScanned = new ArrayList<Region>();

    LOG.info("checking scan candidancy for " + allRegions.size() + " regions");

    for ( Region region : allRegions ) {
      if ( shouldScan(region) ) {
        regionsToBeScanned.add(region);
      }
    }

    if (regionsToBeScanned.size() == 0) {
      return;
    }

    // Scan one region against all providers
    Region region = regionsToBeScanned.get(0);
    scanRegion(region);

  }

  private boolean shouldScan(Region region) {

    if ( region.forceScan ) {
      return true;
    }

    if ( region.lastScan.isAfter(LocalDateTime.now().minusDays(1)) ) { 
      return false;
    }
    return !region.isDisabled;

  }

  private void scanRegion(Region region) {

    for ( EventTypeEnum source : EventTypeEnum.values() ) {

      //Get EventScanner delegate 
      EventScanner delegateScanner = scanners.get(source);

      //Scan raw events
      LOG.info("Starting scan for region " + region.toString() + " for provider " + source.toString()); 
      ScanReport scanReport = delegateScanner.scan(region);

      if ( !scanReport.success ) {
        LOG.warn("Scan failed for " + region.toString() + " for provider " + source.toString()); 
        continue;
      } else {
        LOG.info("Scan success for " + region.toString() + " for provider " + source.toString()); 
      }

      //Update DB
 
      scanReport.events
        .stream()
        .filter(  event -> (eventRepository.findByEventIdAndType(event.eventId,source).isEmpty()))
        .forEach( event -> eventRepository.save(event)
      );

      region.lastScan = LocalDateTime.now();
      region.forceScan = false;
      regionRepository.save(region);

    }

  }

}
