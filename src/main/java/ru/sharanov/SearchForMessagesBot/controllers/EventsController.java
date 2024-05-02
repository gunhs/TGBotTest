package ru.sharanov.SearchForMessagesBot.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.services.EventService;
import ru.sharanov.SearchForMessagesBot.services.ModelAndViewService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventsController {

    private final EventService eventService;
    private final ModelAndViewService modelAndViewService;

    @GetMapping
    public ModelAndView getEvents() {
        return modelAndViewService.getView("index");
    }

    @GetMapping("/{id}")
    public ModelAndView getEvent(@PathVariable("id") int id) {
        EventDTO eventDTO = eventService.getEventDTO(id);
        ModelAndView modelAndView = modelAndViewService.getModelAndView("show", eventDTO);
        modelAndView.addObject("participants", eventService.getEventDTOById(eventDTO.getId()).getParticipantDTOList());
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView newEvent(@ModelAttribute("event") EventDTO event) {
        return modelAndViewService.getModelAndView("new", event);
    }

    @PostMapping
    public ModelAndView addEvent(@ModelAttribute("event") @Valid EventDTO event, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return modelAndViewService.getModelAndView("new", event);
        }
        eventService.addEvent(event);
        return modelAndViewService.getView("redirect:/events");
    }

    @GetMapping("/{id}/edit")
    public ModelAndView edit(@PathVariable("id") int id) {
        EventDTO eventDTO = eventService.getEventDTO(id);
        return modelAndViewService.getModelAndView("edit", eventDTO);
    }

    @PatchMapping("/{id}")
    public ModelAndView updateEvent(@PathVariable("id") int id,
                                    @ModelAttribute("event") @Valid EventDTO event, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return modelAndViewService.getModelAndView("edit", event);
        }
        eventService.updateEvent(event, id);
        return modelAndViewService.getView("redirect:/events");
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteEvent(@PathVariable("id") int id) {
        eventService.deleteEvent(id);
        return modelAndViewService.getView("redirect:/events");
    }

}