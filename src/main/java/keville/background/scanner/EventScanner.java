package keville.background.scanner;

import keville.model.region.Region;

public interface EventScanner  {
  ScanReport scan(Region region) ;
}
