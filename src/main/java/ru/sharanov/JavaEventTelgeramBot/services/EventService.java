package ru.sharanov.JavaEventTelgeramBot.services;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sharanov.JavaEventTelgeramBot.dto.EventDTO;
import ru.sharanov.JavaEventTelgeramBot.dto.ParticipantDTO;
import ru.sharanov.JavaEventTelgeramBot.model.Event;
import ru.sharanov.JavaEventTelgeramBot.model.Guest;
import ru.sharanov.JavaEventTelgeramBot.model.GuestKey;
import ru.sharanov.JavaEventTelgeramBot.model.Participant;
import ru.sharanov.JavaEventTelgeramBot.repositories.EventRepository;
import ru.sharanov.JavaEventTelgeramBot.repositories.GuestRepository;
import ru.sharanov.JavaEventTelgeramBot.repositories.ParticipantRepository;
import ru.sharanov.JavaEventTelgeramBot.services.mapper.EventsMapper;
import ru.sharanov.JavaEventTelgeramBot.services.mapper.ParticipantMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final GuestRepository guestRepository;
    private final ParticipantMapper participantMapper = Mappers.getMapper(ParticipantMapper.class);
    private final EventsMapper eventsMapper = Mappers.getMapper(EventsMapper.class);

    public void addEvent(EventDTO eventDTO) {
        eventDTO.setDone(eventDTO.getDate().isBefore(LocalDateTime.now()));
        eventRepository.save(eventsMapper.toEntity(eventDTO));
    }

    public Event getEventById(int id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event with id" + id + " not found"));
    }

    public Event getEventById(String eventId) {
        return getEventById(Integer.parseInt(eventId));
    }

    public EventDTO getEventDTO(int id) {
        Event event = getEventById(id);
        return eventsMapper.toDto(event);
    }

    public void deleteEvent(int id) {
        eventRepository.deleteById(id);
    }

    public void updateEvent(EventDTO eventDTO, int id) {
        Event event = getEventById(id);
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

    public List<EventDTO> getAllEventsDTO() {
        return eventRepository.findAllByOrderByDateAsc().stream()
                .map(eventsMapper::toDto).collect(Collectors.toList());
    }

    public List<EventDTO> getAllEventsDtoBefore() {
        return eventRepository.findByDateBefore().stream()
                .map(eventsMapper::toDto).collect(Collectors.toList());
    }

    public List<EventDTO> getAllEventsDtoAfter() {
        return eventRepository.findByDateAfter().stream()
                .map(eventsMapper::toDto).collect(Collectors.toList());
    }

    public EventDTO getEventDTOById(int id) {
        Event event = getEventById(id);
        EventDTO eventDTO = eventsMapper.toDto(event);
        List<ParticipantDTO> participantDtoList = event.getParticipants().stream().map(participantMapper::toDto).toList();
        eventDTO.setParticipantDTOList(participantDtoList);
        return eventDTO;
    }

    public void addParticipantInEvent(String eventId, Participant participant) {
        eventRepository.addParticipantInEvent(Long.parseLong(eventId), participant.getId());
    }

    @Transactional
    public int deleteParticipantFromEvent(Long eventId, Long userId) {
        return eventRepository.deleteParticipantFromEvent(eventId, userId);
    }

    public void addGuest(String eventId, long userId) {
        addParticipantInEventIfNotExist(eventId, userId);
        long eventIdDigital = Long.parseLong(eventId);
        Guest guest = getGuestsByEventIdAndUserId(eventIdDigital, userId).orElse(new Guest());
        guest.setId(guest.getId() == null ? new GuestKey(eventIdDigital, userId) : guest.getId());
        guest.setCount(guest.getCount() == null ? 1 : guest.getCount() + 1);
        if (guest.getCount() > 3) {
            throw new RuntimeException("Слишком много гостей");
        }
        guestRepository.save(guest);
    }

    private void addParticipantInEventIfNotExist(String eventId, long userId) {
        if (!checkParticipantInEvent(Long.parseLong(eventId), participantRepository.findByUserId(userId).getId())) {
            Participant participant = participantRepository.findParticipantsByUserId(userId);
            addParticipantInEvent(eventId, participant);
        }
    }

    public void removeGuest(long eventId, long participantId) throws RuntimeException {
        Guest guest = getGuestsByEventIdAndUserId(eventId, participantId)
                .orElseThrow(() -> new RuntimeException("Вы не записаны"));
        guest.setCount(guest.getCount() - 1);
        if (guest.getCount() < 0) {
            throw new RuntimeException("Невозможно удалить гостя");
        }
        guestRepository.save(guest);
    }

    public List<Guest> getGuestsByEventId(Long eventId) {
        return guestRepository.findById_EventID(eventId);
    }

    public Optional<Guest> getGuestsByEventIdAndUserId(Long eventId, Long userId) {
        return guestRepository.findById_EventIDAndId_ParticipantID(eventId, userId);
    }

    @Scheduled(cron = "0 59 23 * * *")
    public void checkStatusEvent() {
        eventRepository.updateDoneByDate();
    }

    public boolean checkParticipantInEvent(Long eventId, Long userId) {
        return eventRepository.checkParticipantInEvent(eventId, userId);
    }
}