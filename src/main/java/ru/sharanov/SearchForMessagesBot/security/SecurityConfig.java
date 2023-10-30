package ru.sharanov.SearchForMessagesBot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final EventUserDetailService eventUserDetailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public SecurityConfig(EventUserDetailService eventUserDetailService, BCryptPasswordEncoder passwordEncoder) {
        this.eventUserDetailService = eventUserDetailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(urlAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/sign").permitAll()
                .and().httpBasic()
                .and().csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(eventUserDetailService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public UrlAuthenticationFilter urlAuthenticationFilter() throws Exception {
        return new UrlAuthenticationFilter(authenticationManager(), eventUserDetailService);
    }
}
