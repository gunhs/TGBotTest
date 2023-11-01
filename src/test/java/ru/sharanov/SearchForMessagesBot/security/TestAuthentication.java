package ru.sharanov.SearchForMessagesBot.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import ru.sharanov.SearchForMessagesBot.model.Participant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Rollback
public class TestAuthentication {
    private final Participant participant;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private WebSecurityConfigurerAdapter webSecurityConfigurerAdapter;

    public TestAuthentication(Participant participant, BCryptPasswordEncoder passwordEncoder) {
        this.participant = participant;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    public void testAttemptAuthentication() throws Exception {
        // Создаем объект-аутентификатор
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("Василий", "408513830");
        token.setDetails(new EventUserDetail(participant, passwordEncoder));


        // Создаем контекст безопасности
        SecurityContext context = new SecurityContextImpl(new HttpServletRequest(), new HttpServletResponse());

        // Вызываем метод attemptAuthentication
        Authentication authentication = webSecurityConfigurerAdapter.getAuthenticationManager().authenticate(token, context);

        // Проверяем, что полученный объект является аутентифицированным
        assertNotNull(authentication);
        assertEquals("user", authentication.getPrincipal().toString());
    }
}