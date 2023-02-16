package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.entities.Event;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public String addEvent(String eventInString) {
        Event event = new Event();
        String[] components = eventInString.split(",");
        String eventName = components[0].strip();
        LocalDate date = LocalDate.parse(components[1].strip(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String address = components[2].strip();
        event.setEventName(eventName);
        event.setDate(date);
        event.setAddress(address);
        for (Event e : eventRepository.findAll()) {
            if (e.getEventName().equals(eventName) && e.getDate().equals(date)) {
                return "Это мероприятие уже добавлено";
            }
        }
        eventRepository.save(event);
        return "Добавлено мероприятие" + eventName;
    }

    public void delEvent(String eventName) {
        eventRepository.delete(Objects.requireNonNull(eventRepository.findAll()
                .stream().filter(p -> p.getEventName().equals(eventName))
                .findFirst().orElse(null)));
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public boolean checkWord(String eventName) {
        return eventRepository.findAll().stream().anyMatch(p -> p.getEventName().equals(eventName));
    }
}
