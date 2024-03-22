package keville.model.event;

import keville.model.event.location.Location;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("EVENT")
public class Event {

  @Id
  public Integer id;
  public String eventId;
  @Column(value = "SOURCE")
  public EventTypeEnum eventType;
  public String name;
  public String description;
  @Column(value = "START_TIME")
  public LocalDateTime start;
  @Column(value = "END_TIME")
  public LocalDateTime end;
  public String organizer;
  public String url;
  public boolean virtual;
  @Column(value = "LAST_UPDATE")
  @LastModifiedDate
  public LocalDateTime lastUpdate;
  public EventStatusEnum status;

  // allow Location fields to be emtpy
  @Embedded.Empty
  public Location location;

  public Event(){}

  public Event(
      String eventId, // from source location
      EventTypeEnum eventType,
      String name,
      String description,
      LocalDateTime start,
      LocalDateTime end,
      Location location,
      String organizer,
      String url,
      boolean virtual,
      EventStatusEnum status) {
    this.eventId = eventId;
    this.eventType = eventType;
    this.name = name;
    this.description = description;
    this.start = start;
    this.end = end;
    this.location = location;
    this.organizer = organizer;
    this.url = url;
    this.virtual = virtual;
    this.status = status;
  }

}
