package ru.sharanov.JavaEventTelgeramBot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder bCryptPasswordEncoder, UserDetailsService eventUserDetailService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(eventUserDetailService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
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
