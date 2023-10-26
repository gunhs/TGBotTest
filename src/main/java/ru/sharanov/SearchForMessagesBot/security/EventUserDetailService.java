package ru.sharanov.SearchForMessagesBot.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;

import java.util.Optional;

@Service
public class EventUserDetailService implements UserDetailsService {

    private final ParticipantRepository participantRepository;

    public EventUserDetailService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Participant> checkParticipant = participantRepository.findParticipantByName(username);
        if (checkParticipant.isEmpty()) {
            throw new UsernameNotFoundException("user not found");
        }
        return new EventUserDetail(checkParticipant.get());
    }
}