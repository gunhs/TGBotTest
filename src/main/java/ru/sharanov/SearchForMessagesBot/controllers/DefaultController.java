package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;

import java.util.ArrayList;

@Controller
public class DefaultController {
    private final EventRepository eventRepository;

    public DefaultController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @RequestMapping("/")
    public String index(Model model){
        Iterable<Event> eventIterable =  eventRepository.findAll();
        ArrayList<Event> events = new ArrayList<>();
        for (Event e : eventIterable){
            events.add(e);
        }
        model.addAttribute("events", events);
        return "index";
    }
}