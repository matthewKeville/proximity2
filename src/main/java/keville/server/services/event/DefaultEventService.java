package keville.server.services.event;

import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import keville.model.event.Event;
import keville.model.region.Region;
import keville.repository.EventRepository;
import keville.repository.RegionRepository;
import keville.server.exceptions.AuthorizationException;
import keville.server.security.AuthUtil;
import keville.util.GeoUtils;
import keville.util.Iterables;

@Component
public class DefaultEventService implements EventService {

  private static Logger LOG = LoggerFactory.getLogger(DefaultEventService.class);
  private RegionRepository regionRepository;
  private EventRepository eventRepository;

  public DefaultEventService(
      @Autowired EventRepository eventRepository,
      @Autowired RegionRepository regionRepository) 
  {
    this.eventRepository = eventRepository;
    this.regionRepository = regionRepository;
  }

  @Override
  public Collection<Event> getUserEvents(Integer userId) throws AuthorizationException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Integer principalId = AuthUtil.getPrincipalUserId(authentication);

    if (!userId.equals(principalId)) {
      throw new AuthorizationException("principal " + principalId + " can't access " + userId + " events");
    }

    // for all regions belonging to a user, return events that fall within atleast one region

    //TODO : move query up to database
    Collection<Region> regions = Iterables.toList(regionRepository.findUserRegions(userId));
    Collection<Event> events = Iterables.toList(eventRepository.findAll());

    events = events.stream()
      .filter( e -> e.location.latitude != null && e.location.longitude != null )
      .filter( e -> ( 
      regions.stream().anyMatch( reg -> (  
        e.isVirtual || GeoUtils.sphericalDistance(
          new Point(e.location.latitude,e.location.longitude),
          new Point(reg.latitude,reg.longitude)
        ) < reg.radius
        )
      )
    )).collect(Collectors.toList());


    return events;

  }

}
