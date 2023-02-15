package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.entities.Event;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void addEvent(Event event){
        eventRepository.save(event);
    }

    public void delEvent(Event event){
        eventRepository.delete(event);
    }

    public List<Event> getAllEvents(){
        return eventRepository.findAll();
    }
}
