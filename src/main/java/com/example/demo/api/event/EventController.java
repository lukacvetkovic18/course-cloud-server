package com.example.demo.api.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getAllEvents() { return eventService.getAllEvents(); }

    @GetMapping(path = "/{id}")
    public Optional<Event> getEventById(@PathVariable("id") long id) {
        return eventService.getEventById(id);
    }

    @DeleteMapping
    public void deleteAllEvents() {
        eventService.deleteAllEvents();
    }

    @DeleteMapping(path = "/{id}")
    public void deleteEvent(@PathVariable("id") long id) {
        eventService.deleteEvent(id);
    }
}
