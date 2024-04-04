package keville.server.services.region;

import java.util.Collection;

import keville.model.region.Region;
import keville.model.region.RegionUpdate;
import keville.server.exceptions.AuthorizationException;
import keville.server.exceptions.ResourceNotFoundException;

public interface RegionService {

  public Collection<Region> getUserRegions(Integer userId)  throws AuthorizationException;
  public Region createRegion(Region region) throws AuthorizationException;
  public Region updateRegion(RegionUpdate update) throws AuthorizationException,ResourceNotFoundException;
  public void deleteRegion(Integer id) throws AuthorizationException,ResourceNotFoundException;

}
