package ru.sharanov.JavaEventTelgeramBot.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final EventUserDetailService eventUserDetailService;
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {

//        http.addFilterBefore(urlAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
//                .authorizeRequests()
//                .antMatchers("/**").permitAll()
//                .and().formLogin().successHandler(appAuthenticationSuccessHandler())
//                .and().csrf().disable();
//        http
//                .authorizeRequests()
//                .antMatchers("/sign").permitAll()
//                .antMatchers("/**").hasAnyRole("ADMIN", "USER")
//                .anyRequest().authenticated()
//                .and().csrf().disable();
//                .addFilterBefore(urlAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(eventUserDetailService).passwordEncoder(passwordEncoder());
//    }

//    @Bean
//    public UrlAuthenticationFilter urlAuthenticationFilter() throws Exception {
//        return new UrlAuthenticationFilter(authenticationManager(), eventUserDetailService, passwordEncoder);
//    }

//    @Bean
//    public AuthenticationSuccessHandler appAuthenticationSuccessHandler() {
//        return new AppAuthenticationSuccessHandler();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/sign").permitAll()
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(eventUserDetailService)
                .passwordEncoder(passwordEncoder());
        return authManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UrlAuthenticationFilter urlAuthenticationFilter() throws Exception {
//        return new UrlAuthenticationFilter(authenticationManager(), eventUserDetailService, passwordEncoder());
//    }
}
