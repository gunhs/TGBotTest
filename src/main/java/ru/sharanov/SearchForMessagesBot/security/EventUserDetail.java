package ru.sharanov.SearchForMessagesBot.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.sharanov.SearchForMessagesBot.model.Participant;

import java.util.Collection;
import java.util.List;

public class EventUserDetail implements UserDetails {
    private final Participant participant;
    private final BCryptPasswordEncoder passwordEncoder;

    public EventUserDetail(Participant participant, BCryptPasswordEncoder passwordEncoder) {
        this.participant = participant;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
//        PasswordEncoder encoder = new BCryptPasswordEncoder();
//        return encoder.encode(String.valueOf(participant.getUserId()));
//        return String.valueOf(participant.getUserId());
        return passwordEncoder.encode(String.valueOf(participant.getUserId()));
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
