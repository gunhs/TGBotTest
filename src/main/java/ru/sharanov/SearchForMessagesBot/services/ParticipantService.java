package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EventService eventService;
    private final EventRepository eventRepository;

    public ParticipantService(ParticipantRepository participantRepository, EventService eventService, EventRepository eventRepository) {
        this.participantRepository = participantRepository;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    public void addParticipant(ParticipantDTO participantDTO, String eventId) {
        Participant participant = new Participant();
        if (checkUserId(participantDTO.getUserId())) {
            participant = getParticipantByUserId(participantDTO.getUserId());
        } else {
            participant.setName(participantDTO.getName());
            participant.setNickName(participantDTO.getNickName());
            participant.setUserId(participantDTO.getUserId());
            participantRepository.save(participant);
        }
        eventService.addParticipantInEvent(participant, eventId);
    }

    public void delParticipant(long idUser, String eventId) {
        Participant participant = eventService.getEventById(eventId).getParticipants()
                .stream().filter(p -> p.getUserId() == idUser).findAny().orElse(null);
        Event event = eventService.getEventById(eventId);
        event.removeParticipant(participant);
        eventRepository.save(event);
    }

    public boolean checkUserId(long userId) {
        return participantRepository.findAll().stream().anyMatch(p -> p.getUserId() == userId);
    }

    public Participant getParticipantByUserId(long userId) {
        return participantRepository.findAll().stream()
                .filter(p -> p.getUserId() == userId).findFirst().orElse(null);
    }
}