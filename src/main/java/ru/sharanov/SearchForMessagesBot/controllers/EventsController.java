package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RestController
public class EventsController {
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    public EventsController(EventRepository eventRepository, ParticipantRepository participantRepository) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/events")
    public ModelAndView getEvents() {
        return getView();
    }

    @GetMapping("/events/{id}")
    public ModelAndView getEvent(@PathVariable("id") int id) {
        Event event = eventRepository.findById(id).orElse(null);
        return getModelAndView("show", event);
    }

    @GetMapping("events/new")
    public ModelAndView newPerson(@ModelAttribute("event") Event event) {
        return getModelAndView("new", event);
    }

    //    @RequestMapping(value = "/events", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public @ResponseBody
    @PostMapping(value = "/events")
    public ModelAndView addEvent(@RequestParam("date")
                                     @Pattern(regexp = "^\\d{4}-([0][1-9]|[1][0-2])-([0-2][0-9]|[3][0-1])\\s([0-1][0-9]|[2][0-4]):[0-5][0-9]:[0-5][0-9]$") String dateString,
                                 String eventName, String address) {
        Event event = new Event();
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime date = dateTimeFormat.parse(dateString + ":00", LocalDateTime::from);
        event.setEventName(eventName);
        event.setDate(date);
        event.setAddress(address);
        eventRepository.save(event);
        return getView();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/events/{id}/edit")
    public ModelAndView edit(@PathVariable("id") int id) {
        Event event = eventRepository.findById(id).orElse(null);
        return getModelAndView("edit", event);
    }

    @RequestMapping(value = "/events/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody
    ModelAndView updateEvent(@PathVariable("id") int id, @Valid Event event) {
        Event newEvent = eventRepository.findById(id).orElse(null);
        assert newEvent != null;
        if (!(event.getEventName().isEmpty())) {
            newEvent.setEventName(event.getEventName());
        }
        if (!(event.getDate() == null)) {
            newEvent.setDate(event.getDate());
        }
        if (!(event.getAddress().isEmpty())) {
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

    public ModelAndView getView() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("events", showEvents());
        modelAndView.addObject("participants", showParticipants());
        return modelAndView;
    }

    public ArrayList<Event> showEvents() {
        Iterable<Event> eventIterable = eventRepository.findAll();
        ArrayList<Event> events = new ArrayList<>();
        eventIterable.forEach(events::add);
        return events;
    }

    public ArrayList<Participant> showParticipants() {
        Iterable<Participant> participantIterable = participantRepository.findAll();
        ArrayList<Participant> participants = new ArrayList<>();
        participantIterable.forEach(participants::add);
        return participants;
    }

    private ModelAndView getModelAndView(String view, Event event) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(view);
        modelAndView.addObject("event", event);
        return modelAndView;
    }
}