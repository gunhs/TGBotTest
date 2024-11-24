//package ru.sharanov.JavaEventTelgeramBot.security;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//public class UrlAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
//    private final EventUserDetailService userDetailsService;
//    private final PasswordEncoder passwordEncoder;
//
//    public UrlAuthenticationFilter(AuthenticationManager authenticationManager,
//                                   EventUserDetailService userDetailsService, PasswordEncoder passwordEncoder) {
//        super(new AntPathRequestMatcher("/**"));
//        this.passwordEncoder = passwordEncoder;
//        setAuthenticationManager(authenticationManager);
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
//            throws AuthenticationException {
//        String username = request.getParameter("username");
//        String password = request.getParameter("id");
//        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//        if (userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
//            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password,
//                    userDetails.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            return getAuthenticationManager().authenticate(authentication);
//        } else {
//            throw new BadCredentialsException("Вы не являетесь участником чата");
//        }
//    }
//}