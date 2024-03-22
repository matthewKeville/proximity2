package keville.background;

import keville.model.region.Region;
import keville.repository.RegionRepository;
import keville.services.scanner.ScannerService;
import keville.util.Iterables;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.time.Duration;

@Component
public class RegionScannerService extends SelfSchedulingBackgroundTask {

  private static Logger LOG = LoggerFactory.getLogger(RegionScannerService.class);
  private static final Duration delay = Duration.ofSeconds(60);
  private static final Duration startupDelay = Duration.ofSeconds(15);

  private RegionRepository regionRepository;
  private ScannerService scannerService;

  public RegionScannerService(
      @Autowired RegionRepository regionRepository,
      @Autowired ScannerService scannerService,
      @Autowired TaskScheduler taskScheduler
      ) {
    super(taskScheduler,delay,startupDelay,"Region Scanner BG Service");
    this.regionRepository = regionRepository;
  }

  /*
   * Determine if any regions need to be scanned, if there are
   * scan across all providers.
   */
  public void doTask() {

    // Determine regions that need to be scanned

    Collection<Region> allRegions  = Iterables.toList(regionRepository.findAll());
    List<Region> regionsToBeScanned = new ArrayList<Region>();

    LOG.info("evaluating all regions" + allRegions.size());

    for ( Region region : allRegions ) {

      LOG.info("checking if should scan region " + region.toString());
      //this should be a configurable server setting
      if ( region.lastScan.isBefore(LocalDateTime.now().minusDays(1)) ) { 
        regionsToBeScanned.add(region);
      }

    }

    if (regionsToBeScanned.size() == 0) {
      LOG.info("no regions to scan");
      return;
    }

    // Scan one region against all providers

    Region region = regionsToBeScanned.get(0);
    LOG.info(regionsToBeScanned.size() + " regions to scan");

    this.scannerService.scanRegion(region);

  }


}
