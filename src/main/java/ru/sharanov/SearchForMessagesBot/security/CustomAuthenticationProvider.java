//package ru.sharanov.SearchForMessagesBot.security;
//
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//
//@Component
//public class CustomAuthenticationProvider implements AuthenticationProvider {
//
//    private final EventUserDetailService eventUserDetailService;
//    private final TelegramAuthenticationServlet telegramAuthenticationServlet;
//
//    public CustomAuthenticationProvider(EventUserDetailService eventUserDetailService, TelegramAuthenticationServlet telegramAuthenticationServlet) {
//        this.eventUserDetailService = eventUserDetailService;
//        this.telegramAuthenticationServlet = telegramAuthenticationServlet;
//    }
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String userName = telegramAuthenticationServlet.getName();
//        UserDetails userDetail = eventUserDetailService.loadUserByUsername(userName);
//        String passForCheck = telegramAuthenticationServlet.getId();
//        System.out.println("Id in servlet" + passForCheck);
//        System.out.println("Name in servlet" + userName);
//        String passInDb = userDetail.getPassword();
//        if (!passInDb.equals(passForCheck)) {
//            throw new BadCredentialsException("You don't chatMember");
//        }
//
//        return new UsernamePasswordAuthenticationToken(userDetail, passForCheck, Collections.emptyList());
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return true;
//    }
//}
