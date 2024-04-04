package keville.server.services.region;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import keville.model.region.Region;
import keville.model.region.RegionUpdate;
import keville.repository.RegionRepository;
import keville.server.exceptions.AuthorizationException;
import keville.server.exceptions.ResourceNotFoundException;
import keville.server.security.AuthUtil;
import keville.util.Iterables;

@Component
public class DefaultRegionService implements RegionService {

  private RegionRepository regionRepository;
  private static Logger LOG = LoggerFactory.getLogger(DefaultRegionService.class);

  public DefaultRegionService(@Autowired RegionRepository regionRepository) {
    this.regionRepository = regionRepository;
  }

  @Override
  public Collection<Region> getUserRegions(Integer userId) throws AuthorizationException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer principalId = AuthUtil.getPrincipalUserId(authentication);

    if (!userId.equals(principalId)) {
      throw new AuthorizationException("principal " + principalId + " can't access " + userId + " regions");
    }

    return Iterables.toList(regionRepository.findUserRegions(userId));
  } 

  @Override
  public Region createRegion(Region region) throws AuthorizationException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer principalId = AuthUtil.getPrincipalUserId(authentication);

    if (region.owner != null && !region.owner.equals(principalId)) {
      throw new AuthorizationException("principal " + principalId + " can't create a region owned by " + region.owner);
    }

    //TODO : Perform validation
    LOG.warn("validation skipped for createRegion, please implement");

    return regionRepository.save(region);

  } 

  @Override
  public Region updateRegion(RegionUpdate update) throws AuthorizationException,ResourceNotFoundException {

    //region exists?
    Optional<Region> regionOpt = regionRepository.findById(update.id);
    if ( regionOpt.isEmpty() ) {
      throw new ResourceNotFoundException("region " + update.id + " not found");
    }
    Region region = regionOpt.get();

    //owned by principal?
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer principalId = AuthUtil.getPrincipalUserId(authentication);
    if (region.owner != principalId) {
      throw new AuthorizationException("principal " + principalId + " can't update " + region.owner + "'s region");
    }

    //apply update to region
    //TODO : streamline the partial updating of resources (perhaps a custom ModelMapper cfg)
    region.name = update.name;
    region.radius = update.radius;
    region.latitude = update.latitude;
    region.longitude = update.longitude;
    region.isDisabled = update.isDisabled;
    region.forceScan = update.forceScan;

    //TODO : Apply bussiness logic (no negative radius, radius limit) ...

    return regionRepository.save(region);

  } 

  @Override
  public void deleteRegion(Integer id) throws AuthorizationException,ResourceNotFoundException {

    //region exists?
    Optional<Region> regionOpt = regionRepository.findById(id);
    if ( regionOpt.isEmpty() ) {
      throw new ResourceNotFoundException("region " + id + " not found");
    }
    Region region = regionOpt.get();

    //owned by principal?
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer principalId = AuthUtil.getPrincipalUserId(authentication);
    if (!region.owner.equals(principalId)) {
      throw new AuthorizationException("principal " + principalId + " can't delete " + region.owner + "'s region");
    }

    //update region TODO : bussiness logic
    regionRepository.deleteById(id);

  } 
}
