package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;

import java.util.List;
import java.util.Optional;

@RestController
public class EventsController {
    private final EventRepository eventRepository;

    public EventsController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/events")
    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/events/{id}")
    public ResponseEntity getEvent(@PathVariable("id") int id) {
        Optional<Event> event = eventRepository.findById(id);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @GetMapping("events/new")
    public String newPerson(@ModelAttribute("event") Event event) {
        return "events/new";
    }

    @PostMapping("/events")
    public ResponseEntity addEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/events/{id}")
    public ResponseEntity updateEvent(@PathVariable("id") int id, @RequestBody Event event) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        Event newEvent = optionalEvent.orElse(null);
        assert newEvent != null;
        if (!(event.getEventName() == null)) {
            newEvent.setEventName(event.getEventName());
        }
        if (!(event.getDate() == null)) {
            newEvent.setDate(event.getDate());
        }
        if (!(event.getAddress() == null)) {
            newEvent.setAddress(event.getAddress());
        }
        eventRepository.save(newEvent);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity deleteEvent(@PathVariable("id") int id) {

        eventRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
