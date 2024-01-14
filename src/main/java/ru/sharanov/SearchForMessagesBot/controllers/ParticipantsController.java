package ru.sharanov.SearchForMessagesBot.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.services.EventService;
import ru.sharanov.SearchForMessagesBot.services.ModelAndViewService;

@RestController
@RequiredArgsConstructor
public class ParticipantsController {

    private final ModelAndViewService modelAndViewService;
    private final EventService eventService;

    @DeleteMapping("/events/{id}/{userId}")
    public ModelAndView deleteParticipant(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        eventService.deleteParticipantFromEvent(id, userId);
        return modelAndViewService.getView("redirect:/events/" + id);
    }

}
