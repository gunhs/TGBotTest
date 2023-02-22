package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.model.Participant;
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

    public void delParticipant(String nickName) {
        Participant participant = participantRepository.findAll().stream().filter(p -> p.getNickName().equals(nickName))
                .findFirst().orElse(null);
        assert participant != null;
        participantRepository.delete(participant);
    }

    public boolean checkNickName(String nickName) {
        return participantRepository.findAll().stream().anyMatch(p -> p.getNickName().equals(nickName));

    }

    public Participant getParticipantByNickName(String nickName) {
        return participantRepository.findAll().stream()
                .filter(p -> p.getNickName().equals(nickName)).findFirst().orElse(null);
    }
}
