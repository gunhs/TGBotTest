//package ru.sharanov.JavaEventTelgeramBot.security;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import ru.sharanov.JavaEventTelgeramBot.model.Participant;
//
//import java.util.Collection;
//import java.util.Collections;
//
//
//public class EventUserDetail implements UserDetails {
//    private final Participant participant;
//    private final BCryptPasswordEncoder passwordEncoder;
//
//    public EventUserDetail(Participant participant, BCryptPasswordEncoder passwordEncoder) {
//        this.participant = participant;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.emptyList();
////        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
//    }
//
//    @Override
//    public String getPassword() {
//        String password = String.valueOf(participant.getUserId());
//        return passwordEncoder.encode(password);
//    }
//
//    @Override
//    public String getUsername() {
//        return participant.getName();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
