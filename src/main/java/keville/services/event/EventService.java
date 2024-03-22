package keville.services.event;

import java.util.Collection;

import keville.model.event.Event;

public interface EventService {

  //given an Event external to the DB, determine if there is a match in the DB
  public boolean eventExists(Event event);

  //given an Event external to the DB, create a DB event and groom the event
  //if necessary
  public boolean createEvent(Event event);

  //public Collection<Event> getUserEvents(Integer userId);

}
