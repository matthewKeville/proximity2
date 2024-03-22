package keville.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import keville.model.event.Event;
import keville.model.event.EventTypeEnum;

public interface EventRepository extends CrudRepository<Event, Integer>{

  //FIXME , actually discriminate under enum..
  @Query("SELECT * FROM EVENT WHERE event_id = :eventId")
  Optional<Event> findByEventIdAndType(String eventId,EventTypeEnum type);

  //Optional<Event> findUserEvents(String eventId,EventTypeEnum type);

}
