package ru.sharanov.JavaEventTelgeramBot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class JavaEventTelegramBot {

    public static void main(String[] args) {
        log.info("Время запуска {}", LocalDateTime.now());
        SpringApplication.run(JavaEventTelegramBot.class, args);
    }
}