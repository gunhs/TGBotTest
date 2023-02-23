package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void addEvent(EventDTO eventDTO) {
        Event event = new Event();

        LocalDateTime date = dateTimeFormat.parse(eventDTO.getDate() + ":00", LocalDateTime::from);
        event.setEventName(eventDTO.getEventName());
        event.setAddress(eventDTO.getAddress());
        event.setDate(date);
        eventRepository.save(event);
    }

    public EventDTO getEvent(int id) {
        Event event = eventRepository.findById(id).orElse(null);
        EventDTO eventDTO = new EventDTO();
        eventDTO.setEventName(event.getEventName());
        eventDTO.setAddress(event.getAddress());
        String date = event.getDate().format(dateTimeFormat);
        eventDTO.setDate(date);
        return eventDTO;
    }


    public List<EventDTO> getAllEvents() {

        List<EventDTO> events = new ArrayList<>();
        eventRepository.findAll().forEach(events::add);
        return events;
    }
}
