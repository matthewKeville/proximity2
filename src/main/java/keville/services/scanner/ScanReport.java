package keville.services.scanner;

import keville.model.event.Event;

import java.util.List;
import java.time.Instant;

public class ScanReport {

  public Instant start;
  public Instant finish;
  public boolean success;

  //raw unvalidated events
  public List<Event> events;

  public ScanReport(Instant start, Instant finish,boolean success,List<Event> events) {
    this.start = start;
    this.finish = finish;
    this.events = events;
  }

}
