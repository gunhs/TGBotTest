package ru.sharanov.SearchForMessagesBot.controllers;


import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;
import ru.sharanov.SearchForMessagesBot.services.EventService;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

//@Validated
@RestController
public class EventsController {
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final ParticipantRepository participantRepository;

    public EventsController(EventRepository eventRepository, EventService eventService, ParticipantRepository participantRepository) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/events")
    public ModelAndView getEvents() {
        return getView();
    }

    @GetMapping("/events/{id}")
    public ModelAndView getEvent(@PathVariable("id") int id) {

        EventDTO eventDTO = eventService.getEvent(id);
        return getModelAndView("show", eventDTO);
    }

    @GetMapping("events/new")
    public ModelAndView newPerson(@ModelAttribute("event") EventDTO eventDTO) {
        return getModelAndView("new", eventDTO);
    }

    //    @RequestMapping(value = "/events", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public @ResponseBody

    @PostMapping(value = "/events")
    public ModelAndView addEvent(
            @Valid EventDTO eventDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("new");
        }
        eventService.addEvent(eventDTO);
        return getView();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/events/{id}/edit")
    public ModelAndView edit(@PathVariable("id") int id) {
        EventDTO eventDTO = eventService.getEvent(id);
        return getModelAndView("edit", eventDTO);
    }

    @RequestMapping(value = "/events/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody
    ModelAndView updateEvent(@PathVariable("id") int id, EventDTO eventDTO) {
        EventDTO newEvent = eventService.getEvent(id);
        assert newEvent != null;
        if (!(eventDTO.getEventName().isEmpty())) {
            newEvent.setEventName(eventDTO.getEventName());
        }
        if (!(eventDTO.getDate().isEmpty())) {
            newEvent.setDate(eventDTO.getDate());
        }
        if (!(eventDTO.getAddress().isEmpty())) {
            newEvent.setAddress(eventDTO.getAddress());
        }
        eventService.addEvent(newEvent);
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

    private ModelAndView getModelAndView(String view, EventDTO eventDTO) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(view);
        modelAndView.addObject("event", eventDTO);
        return modelAndView;
    }
}