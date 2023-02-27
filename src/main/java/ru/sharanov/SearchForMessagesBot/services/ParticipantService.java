package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;

import java.util.List;

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

    public void addParticipant(ParticipantDTO participantDTO, String eventName) {
        Participant participant = new Participant();
        participant.setName(participantDTO.getName());
        participant.setNickName(participantDTO.getNickName());
        participant.setUserId(participantDTO.getUserId());
        if (!checkUserId(participantDTO.getUserId())) {
            participantRepository.save(participant);
        } else {
            participant = getParticipantByUserId(participantDTO.getUserId());
        }
        eventService.addParticipantInEvent(participant, eventName);
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public void delParticipant(long idUser, String eventName) {

        Participant participant = eventService.getEventByEventName(eventName).getParticipants()
                .stream().filter(p -> p.getUserId() == idUser).findAny().orElse(null);
        System.out.println("Я получил участника " + participant.getName());
        Event event= eventService.getEventByEventName(eventName);
        event.removeParticipant(participant);
        eventRepository.save(event);
    }

    public boolean checkUserId(long userId) {
//        if (participantRepository.findAll().isEmpty()) {
//            return false;
//        }
        return participantRepository.findAll().stream().anyMatch(p -> p.getUserId() == userId);
    }

    public Participant getParticipantByUserId(long userId) {
        return participantRepository.findAll().
                stream().filter(p -> p.getUserId() == userId).findFirst().orElse(null);
    }

//    public List<ParticipantDTO> getAllParticipantByEvent(String eventName) {
//        List<ParticipantDTO> participants = new ArrayList<>();
//        System.out.println(eventService.getEventDTOByEventName(eventName).getId());
//        eventService.getEventDTOByEventName(eventName).getParticipantDTOList().forEach(p -> {
//            ParticipantDTO participantDTO = new ParticipantDTO();
//            participantDTO.setId(p.getId());
//            participantDTO.setName(p.getName());
//            participantDTO.setNickName(p.getNickName());
//            participantDTO.setUserId(p.getUserId());
//            participants.add(participantDTO);
//        });
//        return participants;
//    }
}