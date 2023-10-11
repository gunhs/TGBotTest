package ru.sharanov.SearchForMessagesBot.Sheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.SearchForMessagesBot.services.TelegramBot;

import java.time.LocalDateTime;
import java.time.Period;

import static java.lang.Thread.sleep;

@Service
public class Birthday {

    private final TelegramBot telegramBot;

    public Birthday(TelegramBot telegramBot) throws TelegramApiException, InterruptedException {
        checkCurrentTime();
        this.telegramBot = telegramBot;
    }

    public void checkCurrentTime() throws TelegramApiException, InterruptedException {
        long hour = LocalDateTime.now().getHour();
        long minute = LocalDateTime.now().getMinute();
        if (hour < 12) {
            long delay = ((12 - hour)*3600000) + (60 - minute)*60000;
            new Thread(()->{
                try {
                    sleep(delay);
                    checkBirthday();
                } catch (InterruptedException | TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }else {
            telegramBot.congratulation();

        }
    }

    @Scheduled(fixedRateString = "PT1M")
    public void checkBirthday() throws TelegramApiException, InterruptedException {
        LocalDateTime today = LocalDateTime.now();

        if (today.getHour() == 0 && today.getMinute() == 1) {
            telegramBot.congratulation();
        }
    }
}
