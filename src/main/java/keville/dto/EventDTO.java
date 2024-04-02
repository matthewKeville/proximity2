package keville.dto;

import java.time.LocalDateTime;

import keville.model.event.EventStatusEnum;
import keville.model.event.EventTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDTO {

  public Integer id;
  public String eventId;
  public EventTypeEnum eventType;
  public String name;
  public String description;
  public LocalDateTime start;
  public LocalDateTime end;
  public String organizer;
  public String url;
  public boolean isVirtual;
  public LocalDateTime lastUpdate;
  public EventStatusEnum status;
  public LocationDTO location;

}
