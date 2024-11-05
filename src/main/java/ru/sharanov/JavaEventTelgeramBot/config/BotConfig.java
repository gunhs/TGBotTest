package ru.sharanov.JavaEventTelgeramBot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class BotConfig {
    @Value("${telegram.botName}")
    private String botName;
    @Value("${telegram.token}")
    private String token;
}
