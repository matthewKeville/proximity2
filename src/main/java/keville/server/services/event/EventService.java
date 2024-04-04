package keville.server.services.event;

import java.util.Collection;

import keville.model.event.Event;
import keville.server.exceptions.AuthorizationException;

public interface EventService {

  public Collection<Event> getUserEvents(Integer userId) throws AuthorizationException;

}
