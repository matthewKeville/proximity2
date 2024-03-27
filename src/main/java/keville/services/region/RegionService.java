package keville.services.region;

import keville.exceptions.AuthorizationException;
import keville.exceptions.ResourceNotFoundException;
import keville.model.region.Region;
import java.util.Collection;

public interface RegionService {

  public Collection<Region> getUserRegions(Integer userId)  throws AuthorizationException;
  public Region createRegion(Region region) throws AuthorizationException;
  public Region updateRegion(Region region) throws AuthorizationException,ResourceNotFoundException;
  public void deleteRegion(Integer id) throws AuthorizationException,ResourceNotFoundException;

}
