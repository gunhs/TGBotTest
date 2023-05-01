package ru.sharanov.SearchForMessagesBot.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Guest;
import ru.sharanov.SearchForMessagesBot.model.GuestKey;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;
import ru.sharanov.SearchForMessagesBot.repositories.GuestRepository;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final GuestRepository guestRepository;

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
        if (!(eventDTO.getLatitude() == 0)) {
            event.setLatitude(eventDTO.getLatitude());
        }
        if (!(eventDTO.getLongitude() == 0)) {
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
        List<EventDTO> eventDTOList = new ArrayList<>();
        eventRepository.findAllByOrderByDateAsc().forEach(e -> eventDTOList.add(newEventDTO(e)));
        List<Event> eventList = eventRepository.findAll();
        eventList.forEach(e -> {
            if (!e.getDate().isAfter(ChronoLocalDateTime.from(LocalDateTime.now()))) {
                e.setDone(true);
            }
        });
        eventRepository.saveAll(eventList);
        return eventDTOList;
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

    public boolean addGuest(int eventId, long participantId) {
        if (getEventById(String.valueOf(eventId)).getParticipants().stream().noneMatch(p -> p.getId() == participantId)) {
            Participant participant = participantRepository.findParticipantsByUserId(participantId);
            addParticipantInEvent(participant, String.valueOf(eventId));
        }
        Guest guest = guestRepository.findAll().stream().filter(g -> g.getId().getEventID() == eventId &&
                g.getId().getParticipantID() == participantId).findFirst().orElse(null);
        if (guest == null) {
            guest = new Guest();
            guest.setId(new GuestKey(eventId, participantId));
            guest.setCount(1);
            guestRepository.save(guest);
            return true;
        } else {
            int count = guest.getCount();
            if (count < 3) {
                guest.setCount(count + 1);
                guestRepository.save(guest);
                return true;
            } else return false;
        }
    }

    public boolean removeGuest(int eventId, long participantId) {
        Guest guest = guestRepository.findAll().stream().filter(g -> g.getId().getEventID() == eventId
                && g.getId().getParticipantID() == participantId).findFirst().orElse(null);
        if (guest != null) {
            int count = guest.getCount();
            if (count > 0) {
                guest.setCount(count - 1);
                guestRepository.save(guest);
            } else return false;
            return true;
        } else return false;
    }

    public List<Guest> getGuestsByEventId(int eventId) {
        return guestRepository.findAll().stream().filter(guest -> guest.getId().getEventID() == eventId)
                .collect(Collectors.toList());
    }
}