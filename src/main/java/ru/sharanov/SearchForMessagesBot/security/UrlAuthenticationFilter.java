package ru.sharanov.SearchForMessagesBot.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

//public class UrlAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
//
//    public UrlAuthenticationFilter(AuthenticationManager authenticationManager) {
//        super(new AntPathRequestMatcher("/login", "GET")); // Замените "/login" на путь, по которому будет производиться аутентификация
//        setAuthenticationManager(authenticationManager);
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
//            throws AuthenticationException, IOException, ServletException {

//        String userName = request.getParameter("name");
//        UserDetails userDetail = eventUserDetailService.loadUserByUsername(userName);
//        String passForCheck = request.getParameter("id");
//        System.out.println("Id in servlet" + passForCheck);
//        System.out.println("Name in servlet" + userName);
//        String passInDb = userDetail.getPassword();
//        if (!passInDb.equals(passForCheck)) {
//            throw new BadCredentialsException("You don't chatMember");
//        }
//
//        return new UsernamePasswordAuthenticationToken(userDetail, passForCheck, Collections.emptyList());
//    }
//}