package keville.controller.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import keville.services.event.EventService;

@RestController
@RequestMapping("/api/event")
public class EventController {

  private static final Logger LOG = LoggerFactory.getLogger(EventController.class);

  private EventService eventService;

  public EventController(
      @Autowired EventService eventService
  ) {
    this.eventService = eventService;
  }

}
