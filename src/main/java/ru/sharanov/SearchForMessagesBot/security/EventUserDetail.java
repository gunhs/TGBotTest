package ru.sharanov.SearchForMessagesBot.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public class EventUserDetail implements UserDetails {

    private final EventChatUser eventChatUser;

    public EventUserDetail(EventChatUser eventChatUser) {
        this.eventChatUser = eventChatUser;
    }

    public EventChatUser getEventChatUser() {
        return eventChatUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return eventChatUser.getPassword();
    }

    @Override
    public String getUsername() {
        return eventChatUser.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
