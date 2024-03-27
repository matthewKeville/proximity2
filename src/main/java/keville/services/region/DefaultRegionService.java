package keville.services.region;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import keville.exceptions.AuthorizationException;
import keville.exceptions.ResourceNotFoundException;
import keville.model.region.Region;
import keville.repository.RegionRepository;
import keville.util.Iterables;
import keville.util.auth.AuthUtil;

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
      throw new AuthorizationException("principal " + principalId + " can't access " + userId + " events");
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
  public Region updateRegion(Region regionUpdate) throws AuthorizationException,ResourceNotFoundException {

    //region exists?
    Optional<Region> regionOpt = regionRepository.findById(regionUpdate.id);
    if ( regionOpt.isEmpty() ) {
      throw new ResourceNotFoundException("region " + regionUpdate.id + " not found");
    }
    Region region = regionOpt.get();

    //owned by principal?
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer principalId = AuthUtil.getPrincipalUserId(authentication);
    if (region.owner != principalId) {
      throw new AuthorizationException("principal " + principalId + " can't update " + region.owner + "'s region");
    }

    //update region TODO : bussiness logic
    return regionRepository.save(regionUpdate);

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
