package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;
import ru.sharanov.SearchForMessagesBot.services.EventService;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
public class EventsController {
    private final EventService eventService;
    private final ParticipantRepository participantRepository;

    public EventsController(EventService eventService, ParticipantRepository participantRepository) {
        this.eventService = eventService;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/events")
    public ModelAndView getEvents() {
        return getView("index");
    }

    @GetMapping("/events/{id}")
    public ModelAndView getEvent(@PathVariable("id") int id) {
        EventDTO eventDTO = eventService.getEvent(id);
        return getModelAndView("show", eventDTO);
    }

    @GetMapping("events/new")
    public ModelAndView newPerson(@ModelAttribute("event") EventDTO event) {
        return getModelAndView("new", event);
    }

    @PostMapping(value = "/events")
    public ModelAndView addEvent(@ModelAttribute("event") @Valid EventDTO event, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getModelAndView("new", event);
        }
        eventService.addEvent(event);
        return getView("redirect:/events");
    }

    @GetMapping("/events/{id}/edit")
    public ModelAndView edit(@PathVariable("id") int id) {
        EventDTO eventDTO = eventService.getEvent(id);
        return getModelAndView("edit", eventDTO);
    }

    @PatchMapping("/events/{id}")
    public ModelAndView updateEvent(@PathVariable("id") int id,
                                    @ModelAttribute("event") @Valid EventDTO event, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getModelAndView("edit", event);
        }
        eventService.updateEvent(event, id);
        return getView("redirect:/events");
    }

    @DeleteMapping("/events/{id}")
    public ModelAndView deleteEvent(@PathVariable("id") int id) {
        eventService.deleteEvent(id);
        return getView("redirect:/events");
    }

    public ModelAndView getView(String view) {
        ModelAndView modelAndView = new ModelAndView(view);
        modelAndView.addObject("events", showEvents());
        modelAndView.addObject("participants", showParticipants());
        return modelAndView;
    }

    public ArrayList<EventDTO> showEvents() {
        return new ArrayList<>(eventService.getAllEvents());
    }

    public ArrayList<Participant> showParticipants() {
        return new ArrayList<>(participantRepository.findAll());
    }

    private ModelAndView getModelAndView(String view, EventDTO event) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(view);
        modelAndView.addObject("event", event);
        return modelAndView;
    }
}