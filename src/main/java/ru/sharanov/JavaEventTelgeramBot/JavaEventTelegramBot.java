package ru.sharanov.JavaEventTelgeramBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JavaEventTelegramBot {

    public static void main(String[] args) {
        SpringApplication.run(JavaEventTelegramBot.class, args);
    }
}