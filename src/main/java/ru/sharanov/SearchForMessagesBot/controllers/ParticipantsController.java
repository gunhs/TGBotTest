package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;

@RestController
public class ParticipantsController {

    private final ParticipantRepository participantRepository;
    private final EventsController eventsController;

    public ParticipantsController(ParticipantRepository participantRepository, EventsController eventsController) {
        this.participantRepository = participantRepository;
        this.eventsController = eventsController;
    }

    @DeleteMapping("/participants/{id}")
    public ModelAndView deleteEvent(@PathVariable("id") int id) {
        participantRepository.deleteById(id);
        return eventsController.getView("redirect:/events");
    }
}
