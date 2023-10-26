package ru.sharanov.SearchForMessagesBot.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.repositories.EventChatRepository;

@Service
public class EventUserDetailService implements UserDetailsService {

    private final EventChatRepository eventChatRepository;

    public EventUserDetailService(EventChatRepository eventChatRepository) {
        this.eventChatRepository = eventChatRepository;
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
}