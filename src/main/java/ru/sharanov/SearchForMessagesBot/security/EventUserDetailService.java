package ru.sharanov.SearchForMessagesBot.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.EventChatRepository;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;

@Service
public class EventUserDetailService implements UserDetailsService {

    private final EventChatRepository eventChatRepository;
    private final ParticipantRepository participantRepository;

    public EventUserDetailService(EventChatRepository eventChatRepository, ParticipantRepository participantRepository) {
        this.eventChatRepository = eventChatRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EventChatUser eventChatUser = eventChatRepository.getUserByName(username);
        if (eventChatUser != null) {
            return new EventUserDetail(eventChatUser);
        } else {
            throw new UsernameNotFoundException("user not found");
        }
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        Participant participant = participantRepository.findParticipantsByUserId(id);
        EventChatUser eventChatUser = eventChatRepository.getUserByIdTelegram(id);
        if (participant != null && eventChatUser == null) {
            eventChatUser = new EventChatUser();
            eventChatUser.setName(participant.getName());
            eventChatUser.setIdTelegram(id);
            eventChatRepository.save(eventChatUser);
        }
        if (eventChatUser != null) {
            return new EventUserDetail(eventChatUser);
        } else {
            throw new UsernameNotFoundException("user not found");
        }
    }
}