package ru.sharanov.SearchForMessagesBot.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.sharanov.SearchForMessagesBot.model.Participant;

import java.util.Collection;
import java.util.List;

public class EventUserDetail implements UserDetails {

    private final Participant participant;

    public EventUserDetail(Participant participant) {
        this.participant = participant;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return String.valueOf(participant.getUserId());
    }

    @Override
    public String getUsername() {
        return participant.getName();
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
