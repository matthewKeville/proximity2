package keville.aggregator.scanner;

import lombok.ToString;

import java.util.List;

import keville.model.event.Event;

import java.time.Instant;

@ToString
public class ScanReport {

  public Instant start;
  public Instant finish;
  public boolean success;

  //raw unvalidated events
  public List<Event> events;

  public ScanReport(Instant start, Instant finish,boolean success,List<Event> events) {
    this.start = start;
    this.finish = finish;
    this.success = success;
    this.events = events;
  }

}
