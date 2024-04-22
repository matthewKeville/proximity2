package keville.server.services.event;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
    List<Event> qualifiedEvents = new LinkedList<Event>();

    for ( Event event : events ) {

      boolean anyMatch = event.isVirtual; //virtual events auto included
      Iterator<Region> iterator = regions.iterator();
      while ( !anyMatch && iterator.hasNext() ) { //otr, in a user region?
        Region region = iterator.next();
        if ( event.location.latitude == null || event.location.longitude == null ) {
          LOG.debug("non virtual event with missing geo location data " + event.id);
          continue;
        }
        double distance =  GeoUtils.sphericalDistance(
          new Point(event.location.latitude,event.location.longitude),
          new Point(region.latitude,region.longitude));
        if ( GeoUtils.ToMiles(distance) < region.radius ) {
          anyMatch = true;
        }
      }

      if ( anyMatch ) {
        qualifiedEvents.add(event);
      }
    }

    return qualifiedEvents;

  }

}
