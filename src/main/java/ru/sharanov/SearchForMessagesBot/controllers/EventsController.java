package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.services.EventService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class EventsController {
    private final EventService eventService;

    public EventsController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ModelAndView getEvents() {
        return getView("index");
    }

    @GetMapping("/events/{id}")
    public ModelAndView getEvent(@PathVariable("id") int id) {
        EventDTO eventDTO = eventService.getEventDTO(id);
        ModelAndView modelAndView = getModelAndView("show", eventDTO);
        modelAndView.addObject("participants", showParticipants(eventDTO.getId()));
        return modelAndView;
    }

    @GetMapping("events/new")
    public ModelAndView newEvent(@ModelAttribute("event") EventDTO event) {
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
        EventDTO eventDTO = eventService.getEventDTO(id);
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
        return modelAndView;
    }

    public ArrayList<EventDTO> showEvents() {
        List<EventDTO> eventDTOS = eventService.getAllEventsDTO();
        return new ArrayList<>(eventDTOS);
    }

    public ArrayList<ParticipantDTO> showParticipants(int id) {
        return new ArrayList<>(eventService.getEventDTOById(id).getParticipantDTOList());
    }

    private ModelAndView getModelAndView(String view, EventDTO event) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(view);
        modelAndView.addObject("event", event);
        return modelAndView;
    }
}