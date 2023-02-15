package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.entities.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;

import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public void addParticipant(Participant participant) {
        participantRepository.save(participant);
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public void delParticipant(Participant participant) {
        participantRepository.delete(participant);
    }

    public boolean checkNickName(String nickName) {
        for (Participant p : participantRepository.findAll()) {
            if (p.getNickName().equals(nickName)) {
                return true;
            }
        }
        return false;
    }

    public Participant getParticipantByNickName(String nickName){
        for (Participant p : participantRepository.findAll()) {
            if (p.getNickName().equals(nickName)) {
                return p;
            }
        }
        return null;
    }
}
