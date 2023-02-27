package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {
    private final EventsController eventsController;

    public DefaultController(EventsController eventsController) {
        this.eventsController = eventsController;
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("events", eventsController.showEvents());
        return "index";
    }
}