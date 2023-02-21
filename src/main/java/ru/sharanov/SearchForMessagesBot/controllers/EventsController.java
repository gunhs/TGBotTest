package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;

import java.util.ArrayList;
import java.util.Optional;

@RestController
public class EventsController {
    private final EventRepository eventRepository;

    public EventsController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/events")
    public ModelAndView getEvents() {
        return getView();
    }

    @GetMapping("/events/{id}")
    public ModelAndView getEvent(@PathVariable("id") int id) {
        Optional<Event> event = eventRepository.findById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("show");
        modelAndView.addObject("event", event.orElse(null));
        return modelAndView;
    }

    @GetMapping("events/new")
    public ModelAndView newPerson(@ModelAttribute("event") Event event) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("new");
        modelAndView.addObject("event", event);
        return modelAndView;
    }

    @RequestMapping(value = "/events", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody ModelAndView addEvent(Event event) {
        eventRepository.save(event);
        return getView();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/events/{id}/edit")
    public ModelAndView edit(@PathVariable("id") int id) {
        Optional<Event> event = eventRepository.findById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit");
            modelAndView.addObject("event", event.orElse(null));
            return modelAndView;
    }

    @RequestMapping(value = "/events/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody ModelAndView updateEvent(@PathVariable("id") int id, Event event) {
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
        return getView();
    }

    @DeleteMapping("/events/{id}")
    public ModelAndView deleteEvent(@PathVariable("id") int id) {
        eventRepository.deleteById(id);
        return getView();
    }

    private ModelAndView getView(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        Iterable<Event> eventIterable =  eventRepository.findAll();
        ArrayList<Event> events = new ArrayList<>();
        eventIterable.forEach(events::add);
        modelAndView.addObject("events", events);
        return modelAndView;
    }

    private ModelAndView getModelAndView(String view, Event event ){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(view);
        modelAndView.addObject("event", event);
        return modelAndView;
    }

}
