package keville.services.event;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import keville.model.event.Event;
import keville.repository.EventRepository;
import keville.util.Iterables;

@Component
public class DefaultEventService implements EventService {

  private EventRepository eventRepository;

  public DefaultEventService(@Autowired EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  @Override
  public boolean eventExists(Event event) {
    return eventRepository.findByEventIdAndType(event.eventId,event.eventType).isPresent();
  }

  @Override
  public boolean createEvent(Event event) {

    //TODO perform some validation

    eventRepository.save(event);
    return true;
  } 

  /*
  @Override
  public Collection<Event> getUserEvents(Integer userId) {
    return Iterables.toList(eventRepository.findAll());
    return null;
  };
  */
}
