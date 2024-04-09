package keville.aggregator.hosts.Eventbrite;

import keville.aggregator.exceptions.UnlikelyEventIdException;
import keville.aggregator.hosts.Eventbrite.exceptions.EventbriteAPIException;
import keville.aggregator.scraping.HarUtil;
import keville.model.event.Event;
import keville.model.event.EventStatusEnum;
import keville.model.event.EventTypeEnum;
import keville.model.event.location.Location;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import net.lightbody.bmp.core.har.Har;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class EventbriteHarProcessor {

  private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EventbriteHarProcessor.class);

  private EventbriteAPI eventbriteAPI;

  public EventbriteHarProcessor( @Autowired EventbriteAPI eventbriteAPI) {
    this.eventbriteAPI = eventbriteAPI;
  }

  public List<Event> process(Har har) {

    List<String> eventIds = extractEventIds(HarUtil.harToString(har));
    List<Event> events = new LinkedList<Event>();

    eventIds.stream().distinct().forEach( eid -> {

      try {

        events.add(createEventFrom(eid));

      } catch ( UnlikelyEventIdException | EventbriteAPIException ex ) {

        if ( ex instanceof UnlikelyEventIdException ) {

          String eidPartial = eid.substring(0,Math.min(eid.length(),20));
          LOG.debug("caught unlikely event id : " + eidPartial);

        } else {

          LOG.warn("caught EventbriteAPI exception : " + ex.getMessage());

        }
      }

    });

    return events;
  }

  private Event createEventFrom(String eventId) throws UnlikelyEventIdException, EventbriteAPIException {

    JsonObject eventJson = eventbriteAPI.getEvent(eventId);
    Location.Builder lb = Location.builder();
    Event.Builder eb = Event.builder();

    ///////////////////////////////////////////////////////////////////////////
    //Event
    ///////////////////////////////////////////////////////////////////////////

    eb.setEventId(eventId);

    if ( eventJson.has("name") ) {
      eb.setName(eventJson.getAsJsonObject("name").get("text").getAsString());
    }

    if (eventJson.has("summary")) {               // compact
      eb.setDescription(eventJson.get("summary").toString());
    } else if (eventJson.has("description")) {    // verbose (& deprecated)
      JsonElement eventDescriptionJson = eventJson.getAsJsonObject("description").get("text");
      eb.setDescription(eventDescriptionJson.toString());
    }
    
    if ( eventJson.has("start") ) {
      JsonObject eventStartJson = eventJson.getAsJsonObject("start");
      String timestring = eventStartJson.get("utc").getAsString();
      Instant start  = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(timestring));
      LocalDateTime utc = LocalDateTime.ofInstant(start,ZoneOffset.UTC);
      eb.setStart(utc);
    }

    if ( eventJson.has("end") ) {
      JsonObject eventEndJson = eventJson.getAsJsonObject("end");
      String timestring = eventEndJson.get("utc").getAsString();
      Instant end  = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(timestring));
      LocalDateTime utc = LocalDateTime.ofInstant(end,ZoneOffset.UTC);
      eb.setEnd(utc);
    }

    if (eventJson.has("organizer")) {
      JsonObject organizer = eventJson.get("organizer").getAsJsonObject();
      if ( organizer.has("name")) {
        JsonElement organizerNameElement = organizer.get("name");
        if ( !organizerNameElement.isJsonNull() ) {
          eb.setOrganizer(organizerNameElement.getAsString());
        }
      }
    }

    //intentional short circuit
    boolean virtual = eventJson.has("online_event") && eventJson.get("online_event").getAsString().equals("true");
    eb.setVirtual(virtual);

    if ( eventJson.has("url") ) {
      eb.setUrl(eventJson.get("url").getAsString());
    }

    ///////////////////////////////////////////////////////////////////////////
    //Location
    ///////////////////////////////////////////////////////////////////////////

    if ( eventJson.has("venue") && !eventJson.get("venue").isJsonNull() ) {

      JsonObject venueJson = eventJson.get("venue").getAsJsonObject();

      if ( venueJson.has("name") ) {
        lb.setName(venueJson.get("name").getAsString());
      }

      if ( venueJson.has("latitude") && venueJson.has("longitude") ) {
        Double latitude  = Double.parseDouble(venueJson.get("latitude").getAsString());
        Double longitude = Double.parseDouble(venueJson.get("longitude").getAsString());
        lb.setLatitude(latitude);
        lb.setLongitude(longitude);
      }

      if ( venueJson.has("address") ) {

        JsonObject address = venueJson.getAsJsonObject("address");

        if ( address.has("country") ) {
            lb.setCountry(address.get("country").getAsString());
        }

        if ( address.has("region") ) {
            lb.setRegion(address.get("region").getAsString());
        }

        if ( address.has("city") ) {
            lb.setLocality(address.get("city").getAsString());
        }

      }

    } else {
      LOG.warn("unable to find venue data for this event json ... ");
      LOG.warn(eventJson.toString());
    }


    eb.setLocation(lb.build());
    eb.setEventTypeEnum(EventTypeEnum.EVENTBRITE);
    eb.setStatus(EventStatusEnum.HEALTHY);

    return eb.build();

  }

  //Get event ids from HTTP Archive
  public List<String> extractEventIds(String harString) {

    List<String> eventIds = new ArrayList<String>();

      Pattern pat = Pattern.compile("(?<=eventbrite_event_id\\\\\":\\\\\").*?(?=\\\\\",\\\\\"start)"); //what an ungodly creation
      Matcher mat = pat.matcher(harString);
      while (mat.find()) {
        eventIds.add(mat.group());
      }

    return eventIds;

  }

}
