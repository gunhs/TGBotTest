package ru.sharanov.SearchForMessagesBot.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UrlAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final EventUserDetailService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UrlAuthenticationFilter(AuthenticationManager authenticationManager,
                                   EventUserDetailService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        super(new AntPathRequestMatcher("/**"));
        this.passwordEncoder = passwordEncoder;
        setAuthenticationManager(authenticationManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String username = request.getParameter("first_name");
        String password = request.getParameter("id");
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password,
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return getAuthenticationManager().authenticate(authentication);
        } else {
            throw new BadCredentialsException("Вы не являветесь участником чата");
        }
    }
}