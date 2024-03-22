package keville.services.scanner;

import keville.model.region.Region;

public interface EventScanner  {
  ScanReport scan(Region region);
}
