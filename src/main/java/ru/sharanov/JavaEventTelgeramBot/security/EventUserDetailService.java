package ru.sharanov.JavaEventTelgeramBot.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sharanov.JavaEventTelgeramBot.model.Participant;
import ru.sharanov.JavaEventTelgeramBot.repositories.ParticipantRepository;

import java.util.Optional;

@Service
public class EventUserDetailService implements UserDetailsService {
    private final ParticipantRepository participantRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public EventUserDetailService(ParticipantRepository participantRepository, BCryptPasswordEncoder passwordEncoder) {
        this.participantRepository = participantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Participant> checkParticipant = participantRepository.findParticipantByName(username);
        if (checkParticipant.isEmpty()) {
            throw new UsernameNotFoundException("user not found");
        }
        return new EventUserDetail(checkParticipant.get(), passwordEncoder);
    }
}