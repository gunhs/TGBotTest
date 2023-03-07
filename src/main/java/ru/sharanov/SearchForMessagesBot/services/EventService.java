package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;
import ru.sharanov.SearchForMessagesBot.utils.DateTypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    public EventService(EventRepository eventRepository, ParticipantRepository participantRepository) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    public void addEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setEventName(eventDTO.getEventName());
        event.setAddress(eventDTO.getAddress());
//        event.setDate(DateTypeConverter.newStringToLocalDateTimeConverter(eventDTO.getDate()));
        event.setDate(eventDTO.getDate());
        event.setDone(event.getDate().before(Date.from(Instant.now())));
        event.setUrl((eventDTO.getUrl()));
        eventRepository.save(event);
    }

    public EventDTO getEvent(int id) {
        Event event = eventRepository.findById(id).orElse(null);
        assert event != null;
        return newEventDTO(event);
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
//        if (!eventDTO.getDate().isEmpty()) {
//            event.setDate(DateTypeConverter.stringToLocalDateTimeConverter(eventDTO.getDate()));
//            event.setDone(event.getDate().isBefore(LocalDateTime.now()));
//        }
//        if (!eventDTO.getDate().equals(event.getDate())) {
//            event.setDate(eventDTO.getDate());
//            event.setDone(event.getDate(). Before(LocalDateTime.now()));
//        }
        if (!eventDTO.getUrl().isEmpty()) {
            event.setUrl(eventDTO.getUrl());
        }
        eventRepository.save(event);
    }

    private EventDTO newEventDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setEventName(event.getEventName());
        eventDTO.setAddress(event.getAddress());
//        eventDTO.setDate(DateTypeConverter.localDateTimeToStringConverter(event.getDate()));
        eventDTO.setDate(event.getDate());
        eventDTO.setDone(event.isDone());
        eventDTO.setUrl(event.getUrl());
        return eventDTO;
    }

    public List<EventDTO> getAllEventsDTO() {
//        eventRepository.findAll().forEach(e -> {
//            e.setUrl("");
//            e.setDone(e.getDate().isBefore(LocalDateTime.now()));
//            eventRepository.save(e);
//        });

        List<EventDTO> events = new ArrayList<>();
        eventRepository.findAll().forEach(event -> events.add(newEventDTO(event)));
        return events;
    }

    public EventDTO getEventDTOById(int id) {
        Event event = eventRepository.findById(id).orElse(null);
        assert event != null;
        EventDTO eventDTO = newEventDTO(event);
        event.getParticipants().forEach(p -> {
            ParticipantDTO participantDTO = new ParticipantDTO();
            participantDTO.setUserId(p.getUserId());
            participantDTO.setName(p.getName());
            participantDTO.setNickName(p.getNickName());
            participantDTO.setId(p.getId());
            eventDTO.getParticipantDTOList().add(participantDTO);
        });
        return eventDTO;
    }

    public void addParticipantInEvent(Participant participant, String eventId) {
        Event event = getEventById(eventId);
        event.addParticipant(participant);
        eventRepository.save(event);
    }

    public void deleteParticipantFromEvent(int eventId, int userId) {
        Participant participant = participantRepository.findAll().stream().filter(p -> p.getId() == userId).
                findFirst().orElse(null);
        assert participant != null;
        Objects.requireNonNull(eventRepository.findById(eventId).orElse(null)).removeParticipant(participant);
        participantRepository.save(participant);
    }

    public Event getEventById(String eventId) {
        return eventRepository.findById(Integer.valueOf(eventId)).orElse(null);
    }
}