package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DefaultController {

    private final EventsController eventsController;

    public DefaultController(EventsController eventsController) {
        this.eventsController = eventsController;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("events", eventsController.showEvents());
        return "index";
    }
}