package ru.sharanov.JavaEventTelgeramBot.Handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.JavaEventTelgeramBot.services.TelegramBot;

@Slf4j
@Service
public class CommandHandler {
    private final TelegramBot telegramBot;

    public CommandHandler(@Lazy TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void messageWatcherHandler(Update update) throws TelegramApiException {
        String textMessage = update.getMessage().getText();
        long chatIdMessage = update.getMessage().getChatId();
        if (textMessage.equals("/start")) {
            telegramBot.showMessage(chatIdMessage,
                    "Нажмите кнопку \"Меню\"\nPress \"Menu\" Button", 10000);
        }
        if (textMessage.equals("/events@EventJavaBot") || textMessage.equals("/events")) {
            telegramBot.showMenu(chatIdMessage);
            telegramBot.deleteMessage(chatIdMessage, update.getMessage().getMessageId(), 10);
            log.info("{} input: {}", update.getMessage().getFrom().getUserName(), textMessage);
        }
        if (textMessage.toLowerCase().matches("мой день рождения\\s+\\d.+")) {
            telegramBot.addBirthday(chatIdMessage, textMessage, update.getMessage().getFrom().getId());
        }
    }

    public void callBackDataHandler(Update update) throws Exception {
        long idUser = update.getCallbackQuery().getFrom().getId();
        String firstName = update.getCallbackQuery().getFrom().getFirstName();
        String userName = update.getCallbackQuery().getFrom().getUserName();
        String messageText = update.getCallbackQuery().getData();
        String[] components = messageText.split("\\s", 3);
        String eventId = components.length == 3 ? components[2] : "";
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        telegramBot.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId(), 10);
        log.info("{} ({}) click: {}", firstName, userName, messageText);
        if (messageText.equals("join event " + eventId)) {
            telegramBot.addParticipant(firstName, userName, chatId, idUser, eventId);
        } else if (messageText.equals("left event " + eventId)) {
            telegramBot.removeParticipant(chatId, firstName, idUser, eventId);
        } else if (messageText.equals("back")) {
            telegramBot.showFutureEventsButton(chatId);
        } else if (messageText.equals("future event " + eventId)) {
            String nextEventId = telegramBot.getNextFutureEventId(eventId);
            telegramBot.showFutureEvent(chatId, nextEventId);
        } else if (messageText.equals("join all")) {
            telegramBot.participantJoinAll(firstName, userName, chatId, idUser);
        } else if (messageText.equals("past event " + eventId)) {
            String nextEventId = telegramBot.getNextPastEventId(eventId);
            telegramBot.showPastEvent(chatId, nextEventId);
        } else if (messageText.equals("show map " + eventId)) {
            telegramBot.showMap(chatId, eventId);
        } else if (messageText.equals("future events")) {
            telegramBot.showFutureEventsButton(chatId);
        } else if (messageText.equals("past events")) {
            telegramBot.showPastEventsButton(chatId);
        } else if (messageText.equals("menu button")) {
            telegramBot.showMenu(chatId);
        } else if (messageText.equals("past menu")) {
            telegramBot.showPastEventsButton(chatId);
        } else if (messageText.equals("quit button")) {
            telegramBot.closeApp(chatId);
        } else if (messageText.equals("close map")) {
            telegramBot.closeApp(chatId);
        } else if (messageText.equals("past menu " + eventId)) {
            telegramBot.showFutureEvent(chatId, eventId);
        } else if (messageText.equals("add guest " + eventId)) {
            telegramBot.addGuest(chatId, eventId, idUser, firstName);
        } else if (messageText.equals("remove guest " + eventId)) {
            telegramBot.removeGuest(chatId, eventId, idUser, firstName);
        } else if (messageText.equals("birthdays")) {
            telegramBot.showBirthdays(chatId, idUser);
        } else if (telegramBot.checkEvents(messageText)) {
            telegramBot.selectEvent(chatId, messageText);
        }
    }
}
