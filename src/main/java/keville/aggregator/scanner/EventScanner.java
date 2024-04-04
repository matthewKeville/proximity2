package keville.aggregator.scanner;

import keville.model.region.Region;

public interface EventScanner  {
  ScanReport scan(Region region) ;
}
