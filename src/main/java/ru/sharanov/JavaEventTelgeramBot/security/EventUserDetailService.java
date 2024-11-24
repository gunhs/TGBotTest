package ru.sharanov.JavaEventTelgeramBot.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.sharanov.JavaEventTelgeramBot.model.Participant;
import ru.sharanov.JavaEventTelgeramBot.repositories.ParticipantRepository;

@Service
@RequiredArgsConstructor
public class EventUserDetailService implements UserDetailsService {
    private final ParticipantRepository participantRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Participant checkParticipant = participantRepository.findByNickName(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(checkParticipant.getNickName())
                .password(checkParticipant.getPassword())
                .roles(checkParticipant.getRole())
                .build();
    }
}