package keville.server.controller.api;


import java.util.Collection;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import keville.model.event.Event;
import keville.server.dto.EventDTO;
import keville.server.exceptions.AuthorizationException;
import keville.server.security.AuthUtil;
import keville.server.services.event.EventService;

@RestController
@RequestMapping("/api/events")
public class EventController {

  private static final Logger LOG = LoggerFactory.getLogger(EventController.class);

  private EventService eventService;
  private ModelMapper mapper;

  public EventController(
      @Autowired EventService eventService,
      @Autowired ModelMapper mapper 
  ) {
    this.eventService = eventService;
    this.mapper = mapper;
  }

  @GetMapping("")
  public Collection<EventDTO> getUserEvents(
      @Autowired Authentication authentication) {

      //TODO : When RBA has { Admin , User } , call appropriate service method : { getEventss() , getUserEvents() }
      Integer principalId = AuthUtil.getPrincipalUserId(authentication);

      try {

        Collection<Event> events = eventService.getUserEvents(principalId);
        Collection<EventDTO> eventDTOs = events.stream().map( x -> mapper.map(x,EventDTO.class)).collect(Collectors.toSet());
        return eventDTOs;

      } catch (AuthorizationException ex) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }

    }

}
