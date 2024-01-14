package ru.sharanov.SearchForMessagesBot.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sharanov.SearchForMessagesBot.services.EventService;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class DefaultController {

    private final EventService eventService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("events", eventService.getAllEventsDTO());
        return "index";
    }
}