package keville.background.scanner.dev;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import keville.background.scanner.EventScanner;
import keville.background.scanner.ScanReport;
import keville.model.event.Event;
import keville.model.event.EventStatusEnum;
import keville.model.event.EventTypeEnum;
import keville.model.event.location.Location;
import keville.model.region.Region;

@Component
/* returns a list of random event data when invoked for testing purposes */
public class DevScanner implements EventScanner {

  private static Logger LOG = LoggerFactory.getLogger(DevScanner.class);
  private static String[] activities = { 
    "Party", "Concert", "Class", "Show", "Talk", "Hike"
  };

  private static String[] topics = { 
    "Pizza", "Rock", "Flower", "Yoga", "Bowling"
  };

  public ScanReport scan(Region region) {


    List<Event> events = new LinkedList<Event>();
    Random random = new Random();
    int eventCount = random.nextInt(2);

    for ( int i = 0; i < eventCount; i++ ) { 

      Event event = new Event();
      event.eventId = Instant.now().toString();
      event.eventType = EventTypeEnum.DEV;
      event.name = topics[random.nextInt(topics.length-1)] + " " + activities[random.nextInt(activities.length-1)];
      event.description = topics[random.nextInt(topics.length-1)] + " " + activities[random.nextInt(activities.length-1)];
      event.start = LocalDateTime.now();
      event.start = LocalDateTime.now().plusHours(random.nextInt(666));
      event.end = event.start.plusHours(random.nextInt(6));
      event.organizer = "dev";
      event.url = "localhost";
      event.isVirtual = random.nextBoolean();
      event.status = EventStatusEnum.HEALTHY;

      Location location = new Location("dev place",
          "United States",
          "State",
          "Town",
          "123 Fake ST",
          region.latitude  + ( Math.random() / 100.0 ),
          region.longitude + ( Math.random() / 100.0 )
      );

      event.location = location;
      events.add(event);

    }

    return new ScanReport(Instant.now(), Instant.now(),true, events);

  }
}
