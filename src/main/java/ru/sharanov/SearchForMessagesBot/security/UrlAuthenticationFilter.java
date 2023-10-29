package ru.sharanov.SearchForMessagesBot.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UrlAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final EventUserDetailService userDetailsService;

    public UrlAuthenticationFilter(AuthenticationManager authenticationManager, EventUserDetailService userDetailsService) {
        super(new AntPathRequestMatcher("/sign"));
        setAuthenticationManager(authenticationManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String username = request.getParameter("first_name");
        String password = request.getParameter("id");

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails != null && password.equals(userDetails.getPassword())) {
            System.out.println("Пароли совпали");
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password,
                    userDetails.getAuthorities());
            return getAuthenticationManager().authenticate(authentication);
        } else {
            throw new BadCredentialsException("Вы не являветесь участником чата");
        }
    }
}