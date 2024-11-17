package ru.sharanov.JavaEventTelgeramBot.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final EventUserDetailService eventUserDetailService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.addFilterBefore(urlAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
//                .authorizeRequests()
//                .antMatchers("/**").permitAll()
//                .and().formLogin().successHandler(appAuthenticationSuccessHandler())
//                .and().csrf().disable();
        http
                .authorizeRequests()
                .antMatchers("/**").permitAll()
//                .and().formLogin().successHandler(appAuthenticationSuccessHandler())
                .and().csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(eventUserDetailService).passwordEncoder(passwordEncoder());
    }

//    @Bean
//    public UrlAuthenticationFilter urlAuthenticationFilter() throws Exception {
//        return new UrlAuthenticationFilter(authenticationManager(), eventUserDetailService, passwordEncoder);
//    }

//    @Bean
//    public AuthenticationSuccessHandler appAuthenticationSuccessHandler() {
//        return new AppAuthenticationSuccessHandler();
//    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
