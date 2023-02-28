package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.services.EventService;

@RestController
public class ParticipantsController {
    private final EventsController eventsController;
    private final EventService eventService;

    public ParticipantsController(EventsController eventsController, EventService eventService) {
        this.eventsController = eventsController;
        this.eventService = eventService;
    }

    @DeleteMapping("/events/{id}/{userId}")
    public ModelAndView deleteParticipant(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        eventService.deleteParticipantFromEvent(id, userId);
        return eventsController.getView("redirect:/events/" + id);
    }
}
