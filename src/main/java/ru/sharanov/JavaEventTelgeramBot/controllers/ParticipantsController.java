package ru.sharanov.JavaEventTelgeramBot.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.JavaEventTelgeramBot.services.EventService;
import ru.sharanov.JavaEventTelgeramBot.services.ModelAndViewService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParticipantsController {

    private final ModelAndViewService modelAndViewService;
    private final EventService eventService;

    @DeleteMapping("/events/{id}/{userId}")
    public ModelAndView deleteParticipant(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        if (eventService.deleteParticipantFromEvent(id, userId) != 1) {
            log.error("Пользователь или мероприятие не найдено");
        }
        return modelAndViewService.getView("redirect:/events/" + id);
    }
}
