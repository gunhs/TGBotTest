package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void addEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setEventName(eventDTO.getEventName());
        event.setAddress(eventDTO.getAddress());
        event.setDate(stringToLocalDateTimeConverter(eventDTO.getDate()));
        eventRepository.save(event);
    }

    public EventDTO getEvent(int id) {
        Event event = eventRepository.findById(id).orElse(null);
        assert event != null;
        return newEventDTO(event);
    }

    public List<EventDTO> getAllEvents() {
        List<EventDTO> events = new ArrayList<>();
        eventRepository.findAll().forEach(event -> events.add(newEventDTO(event)));
        return events;
    }

    public void deleteEvent(int id) {

        eventRepository.deleteById(id);
    }

    public void updateEvent(EventDTO eventDTO, int id) {
        Event event = eventRepository.findById(id).orElse(null);
        assert event != null;
        if (!eventDTO.getEventName().isEmpty()) {
            event.setEventName(eventDTO.getEventName());
        }
        if (!eventDTO.getAddress().isEmpty()) {
            event.setAddress(eventDTO.getAddress());
        }
        if (!eventDTO.getDate().isEmpty()) {
            event.setDate(stringToLocalDateTimeConverter(eventDTO.getDate()));
        }
        eventRepository.save(event);
    }

    private EventDTO newEventDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setEventName(event.getEventName());
        eventDTO.setAddress(event.getAddress());
        eventDTO.setDate(localDateTimeToStringConverter(event.getDate()));
        return eventDTO;
    }

    private String localDateTimeToStringConverter(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(dateTimeFormat);
    }

    private LocalDateTime stringToLocalDateTimeConverter(String dateTime) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return dateTimeFormat.parse(dateTime + ":00", LocalDateTime::from);
    }

    public void addParticipantInEvent(Participant participant, String eventName) {
        Event event = getEventByEventName(eventName);
        event.addParticipant(participant);
        eventRepository.save(event);
    }

    public EventDTO getEventDTOById(int id) {
        Event event = eventRepository.findById(id).orElse(null);
        EventDTO eventDTO = new EventDTO();
        assert event != null;
        eventDTO.setId(event.getId());
        eventDTO.setEventName(event.getEventName());
        eventDTO.setAddress(event.getAddress());
        eventDTO.setDate(localDateTimeToStringConverter(event.getDate()));
        event.getParticipants().forEach(p->{
            ParticipantDTO participantDTO = new ParticipantDTO();
            participantDTO.setUserId(p.getUserId());
            participantDTO.setName(p.getName());
            participantDTO.setNickName(p.getNickName());
            participantDTO.setId(p.getId());
            eventDTO.getParticipantDTOList().add(participantDTO);
        });
        return eventDTO;
    }

    public Event getEventByEventName(String eventName){
        return eventRepository.findEventByEventName(eventName);
    }
    public Event getEventById(int id){
        return eventRepository.findById(id).orElse(null);
    }

    private Event getEventByEventDTO(EventDTO eventDTO) {
        return eventRepository.findById(eventDTO.getId()).orElse(null);
    }
}