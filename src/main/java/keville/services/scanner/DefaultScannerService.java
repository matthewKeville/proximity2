package keville.services.scanner;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import keville.model.event.Event;
import keville.model.event.EventTypeEnum;
import keville.services.event.EventService;
import keville.services.scanner.dev.DevScanner;
import keville.model.region.Region;

@Component
public class DefaultScannerService implements ScannerService {

  private static Logger LOG = LoggerFactory.getLogger(ScannerService.class);

  private Map<EventTypeEnum,EventScanner> scanners;
  private EventService eventService;

  public DefaultScannerService(@Autowired EventService eventService) {

    this.eventService = eventService;
    createScannerDelegateMap(); //perhaps this should be the responsibility of the service
                                //another candidate for autowiring
  }

  public void scanRegion(Region region) {

    for ( EventTypeEnum source : EventTypeEnum.values() ) {

      //Get EventScanner delegate 

      if ( !scanners.containsKey(source) ) {
        LOG.warn(" no EventScanner delegate for source " + source.toString() + " skipping ");
        continue;
      }
      EventScanner delegateScanner = scanners.get(source);

      //Scan raw events

      LOG.info("Starting scan for region " + region.toString() + " for provider " + source.toString()); 
      ScanReport scanReport = delegateScanner.scan(region);

      if ( !scanReport.success ) {
        LOG.warn("Scan failed for " + region.toString() + " for provider " + source.toString()); 
        continue;
      }

      //Save new events

      for ( Event event : scanReport.events ) {
        if (!eventService.eventExists(event)) {
          eventService.createEvent(event);
        }

      }

    }

  }

  private void createScannerDelegateMap() {
    this.scanners = new HashMap<EventTypeEnum,EventScanner>();
    this.scanners.put(EventTypeEnum.DEV,new DevScanner());
  }

}
