package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        event.setDate(eventDTO.getDate());
        event.setDone(event.getDate().isBefore(LocalDateTime.now()));
        event.setUrl((eventDTO.getUrl()));
        event.setLatitude(eventDTO.getLatitude());
        event.setLongitude(eventDTO.getLongitude());
        eventRepository.save(event);
    }

    public EventDTO getEventDTO(int id) {
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
        if (!eventDTO.getDate().equals(event.getDate())) {
            event.setDate(eventDTO.getDate());
            event.setDone(event.getDate().isBefore(LocalDateTime.now()));
        }
        if (!eventDTO.getUrl().isEmpty()) {
            event.setUrl(eventDTO.getUrl());
        }
        if (!(eventDTO.getLatitude()==0)) {
            event.setLatitude(eventDTO.getLatitude());
        }
        if (!(eventDTO.getLongitude()==0)) {
            event.setLongitude(eventDTO.getLongitude());
        }
        eventRepository.save(event);
    }

    private EventDTO newEventDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setEventName(event.getEventName());
        eventDTO.setAddress(event.getAddress());
        eventDTO.setDate(event.getDate());
        eventDTO.setDone(event.isDone());
        eventDTO.setUrl(event.getUrl());
        eventDTO.setLatitude(event.getLatitude());
        eventDTO.setLongitude(event.getLongitude());
        return eventDTO;
    }

    public List<EventDTO> getAllEventsDTO() {
        List<EventDTO> events = new ArrayList<>();
        eventRepository.findAllByOrderByDateAsc().forEach(e->events.add(newEventDTO(e)));
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