package keville.services.event;

import java.util.Collection;

import keville.exceptions.AuthorizationException;
import keville.model.event.Event;

public interface EventService {

  public Collection<Event> getUserEvents(Integer userId) throws AuthorizationException;

}
