package keville.model.event;

import keville.model.event.location.Location;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
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
  @Column(value = "ISVIRTUAL")
  public boolean isVirtual;
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
      boolean isVirtual,
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
    this.isVirtual = isVirtual;
    this.status = status;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private EventTypeEnum eventType; 
    private String eventId;
    private String name;
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;
    private Location location;
    private String organizer;
    private String url;
    private boolean isVirtual;
    private EventStatusEnum status;

    public Builder setEventTypeEnum(EventTypeEnum eventType) {
      this.eventType = eventType;
      return this;
    } 

    public Builder setEventId(String eventId) {
      this.eventId = eventId;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder setStart(LocalDateTime start) {
      this.start = start;
      return this;
    }

    public Builder setEnd(LocalDateTime end) {
      this.end = end;
      return this;
    }

    public Builder setLocation(Location location) {
      this.location = location;
      return this;
    }

    public Builder setUrl(String url){
      this.url = url;
      return this;
    }

    public Builder setOrganizer(String organizer){
      this.organizer = organizer;
      return this;
    }

    public Builder setVirtual(boolean isVirtual) {
      this.isVirtual = isVirtual;
      return this;
    }

    public Builder setStatus(EventStatusEnum status) {
      this.status = status;
      return this;
    }

    public Event build() {
      Event event = new Event(
        eventId,
        eventType,
        name,
        description,
        start,
        end,
        location,
        organizer,
        url,
        isVirtual,
        status
      );
      return event;
    }

  }

}
