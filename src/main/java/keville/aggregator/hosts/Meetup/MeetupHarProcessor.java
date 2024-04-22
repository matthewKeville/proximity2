package keville.aggregator.hosts.Meetup;

import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import keville.aggregator.exceptions.SchemaParseException;
import keville.aggregator.scraping.HarUtil;
import keville.aggregator.scraping.SchemaUtil;
import keville.model.event.Event;
import keville.model.event.EventStatusEnum;
import keville.model.event.EventTypeEnum;
import keville.model.event.location.Location;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.lightbody.bmp.core.har.Har;

/*
 * Meetup.com embeds event data into the inital page. We use a regex to locate this data.
 * As we scroll new data is loaded from POST /gql2 in a different format and needs special consideration.
 */
public class MeetupHarProcessor {

  private static Logger LOG = LoggerFactory.getLogger(MeetupHarProcessor.class);

    public static List<Event> process(Har har,String targetUrl) {

      List<Event> eventsEmbedded = extractEventsFromStaticPage(HarUtil.harToString(har),targetUrl);
      List<Event> eventsAjax = extractEventsFromAjax(HarUtil.harToString(har));
      List<Event> allEvents = new LinkedList<Event>();

      LOG.debug("found " + eventsEmbedded.size() + " embedded events");
      LOG.debug("found " + eventsAjax.size()     + " ajax events");

      allEvents.addAll(eventsEmbedded);
      allEvents.addAll(eventsAjax);

      return allEvents;
    }

    // The embedded data is unescaped JsonSchema
    public static List<Event> extractEventsFromStaticPage(String harString,String targetUrl) {

      // Find inital web response (may have been redirected)
      LOG.debug("processing embedded event data");
      JsonObject response = HarUtil.findFirstResponseFromRequestUrl(harString,targetUrl,true);

      if ( response == null ) {
        LOG.debug("unable to find intial web response");
        return Collections.emptyList();
      }

      // Get response data
      String webpageData = HarUtil.getDecodedResponseText(response);
      if ( webpageData == null ) {
        LOG.debug("Initial response data is empty");
        return Collections.emptyList();
      }

      try {

        // Find the Schema Event Data by regex
        JsonArray schemaEvents = extractJsonSchemaEventArray(webpageData);
        List<Event> newEvents = new LinkedList<Event>();

        // Try to create domain Event
        for (JsonElement jo : schemaEvents) {
          JsonObject event = jo.getAsJsonObject();
          Event ev = createEventFrom(event);
          if ( ev != null ) {
            newEvents.add(ev);
          }
        }
        return newEvents;

      } catch (JsonParseException ex) {
        LOG.warn("Error parsing embedded Event data");
        return Collections.emptyList();
      }
  }  


  // Find the Schema Json data embedded in the webpage
  private static JsonArray extractJsonSchemaEventArray(String webPageData) throws JsonParseException {

    // regex101 : "(?<=www.googletagmanager.com\"/><script type=\"application/ld\+json\">).*?(?=</script>)"gm
    final String regex = "(?<=www.googletagmanager.com\\\"/><script type=\\\"application/ld\\+json\\\">).*?(?=</script>)"; 
    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    Matcher mat = pattern.matcher(webPageData);
    String eventJsonSchemaArrayString  = "";

    if (mat.find()) {
      eventJsonSchemaArrayString = mat.group(0);
      return JsonParser.parseString(eventJsonSchemaArrayString).getAsJsonArray();
    } 
    LOG.debug("no embedded event data found, maybe regex is outdated?");
    return new JsonArray();

  }



  public static List<Event> extractEventsFromAjax(String harString) {

      LOG.debug("processing ajax event data");
      final String apiUrl = "https://www.meetup.com/gql2";
      List<Event> events = new LinkedList<Event>();

      List<JsonObject> responses = HarUtil.findAllResponsesFromRequestUrl(harString,apiUrl);
      LOG.debug("found " + responses.size() + " /gql2 responses ");

      for (JsonObject resp : responses ) {

        String jsonRaw = HarUtil.getDecodedResponseText(resp);
        if ( jsonRaw == null ) {
          LOG.debug("The response content is null, skipping...");
          continue;
        }

        try {
          JsonArray edges = extractEdgesFromRawGqLJson(jsonRaw);
          for ( JsonElement edgeElement : edges ) {
             JsonObject edge = edgeElement.getAsJsonObject(); 
             events.add(createEventFromGqlEventEdge(edge));
          }
        } catch (JsonParseException e) {
          LOG.debug("unexpected data from /gql2 response");
        }

      }

      return events;
  }

  /*
   * Event data extraction from embedded JsonSchema
   */
  private static Event createEventFrom(JsonObject eventJson) {

    try {

      Event.Builder eb = SchemaUtil.createEventFromSchemaEvent(eventJson);
      eb.setEventTypeEnum(EventTypeEnum.MEETUP);
      //I am assuming this last part is the eventId
      //https://www.meetup.com/monmouth-county-golf-n-sports-fans-social-networking/events/294738939/
      String url = eventJson.get("url").getAsString(); 
      String[] splits = url.split("/");
      String id = splits[splits.length-1];

      eb.setEventId(id); 
      eb.setStatus(EventStatusEnum.HEALTHY);
      return eb.build();

    } catch ( SchemaParseException ex ) {

      LOG.error("Caught Schema Parse Exception");
      LOG.error(ex.getMessage());
      return null;

    }

  }


  /*
   * We anticipate a response from the POST to /gql2 , that contains Event Data
   */
  private static JsonArray extractEdgesFromRawGqLJson(String rawGqlJsonString) throws JsonParseException {
    JsonObject jsonData = JsonParser.parseString(rawGqlJsonString).getAsJsonObject();
    //this response has new event data : data . result . __typename == "RecommendedEventsConnection"
    if ( jsonData.get("data").getAsJsonObject().has("result") ) {
      JsonObject jsonDataResult = jsonData.get("data").getAsJsonObject().get("result").getAsJsonObject();
      if ( jsonDataResult.has("__typename") ) {
        String typeName = jsonDataResult.get("__typename").getAsString();
        if ( typeName.equals("RecommendedEventsConnection") ) {
          return jsonDataResult.get("edges").getAsJsonArray();
        }
      }
    } 
    return new JsonArray();
  }

  /* 
   * Event data extraction from gql format
   */
  private static Event createEventFromGqlEventEdge(JsonObject eventEdge) {

    Event.Builder eb = Event.builder();
    Location.Builder lb = Location.builder();

    JsonObject node = eventEdge.get("node").getAsJsonObject();

    if (node.has("id")) {
      eb.setEventId(node.get("id").getAsString());
    }

    //2023-09-27T12:00-04:00 (from gql : iso offset time)

    if (node.has("dateTime")) {
      String timestring = node.get("dateTime").getAsString();
      Instant start  = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(timestring));
      LocalDateTime utc = LocalDateTime.ofInstant(start,ZoneOffset.UTC);
      eb.setStart(utc);
    }

    if (node.has("endTime")) {
      String timestring = node.get("endTime").getAsString();
      Instant end  = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(timestring));
      LocalDateTime utc = LocalDateTime.ofInstant(end,ZoneOffset.UTC);
      eb.setEnd(utc);
    }

    if (node.has("title")) {
      eb.setName(node.get("title").getAsString());
    }

    if (node.has("description")) {
      eb.setDescription(node.get("description").getAsString());
    }

    if (node.has("venue")) {

      JsonElement venueElement = node.get("venue");

      if ( !venueElement.isJsonNull() ) {

        JsonObject venue = venueElement.getAsJsonObject();


        if ( venue.has("city") ) {
          //Spring Lake
          lb.setLocality(venue.get("city").getAsString());
        }
        if ( venue.has("state") ) {
          lb.setRegion(venue.get("state").getAsString());
          //nj
        }
        if ( venue.has("country") ) {
          lb.setCountry(venue.get("country").getAsString());
        }

        if ( venue.has("lat") && venue.has("lon")) {

          lb.setLatitude(venue.get("lat").getAsDouble());
          lb.setLongitude(venue.get("lon").getAsDouble());

        } else {

          LOG.warn(" missing geocoordinate data for event venue ");
          LOG.warn(venue.toString());

        }

        if ( venue.has("name") ) {
          lb.setName(venue.get("name").getAsString());
        }

      }
    }

    if ( node.has("eventType") ) {
      boolean virt = !node.get("eventType").getAsString().equals("PHYSICAL");
      eb.setVirtual(virt);
    }

    if ( node.has("eventUrl") ) {
      eb.setUrl(node.get("eventUrl").getAsString());
    }

    eb.setLocation(lb.build()); 
    eb.setEventTypeEnum(EventTypeEnum.MEETUP);
    eb.setStatus(EventStatusEnum.HEALTHY);

    return eb.build();
  }


}
