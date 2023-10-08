package ru.sharanov.SearchForMessagesBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SearchForMessagesBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchForMessagesBotApplication.class, args);
    }
}