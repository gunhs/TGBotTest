package ru.sharanov.SearchForMessagesBot.Handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.services.EventService;
import ru.sharanov.SearchForMessagesBot.services.ParticipantService;
import ru.sharanov.SearchForMessagesBot.services.TelegramBot;

import java.io.IOException;
import java.time.LocalDateTime;

public class CommandHandler {
    private final TelegramBot telegramBot;
    private final ParticipantService participantService;
    private final EventService eventService;
    private static final Logger usersActions = LogManager.getLogger("AllActions");

    public CommandHandler(TelegramBot telegramBot, ParticipantService participantService, EventService eventService) {
        this.telegramBot = telegramBot;
        this.participantService = participantService;
        this.eventService = eventService;
    }

    public void MessageWatcherHandler(Update update) throws TelegramApiException, InterruptedException {
        String textMessage = update.getMessage().getText();
        long chatIdMessage = update.getMessage().getChatId();
        if (textMessage.equals("/events@EventJavaBot") || textMessage.equals("/events")) {
            telegramBot.showEventsButton(chatIdMessage);
            telegramBot.deleteMessage(chatIdMessage, update.getMessage().getMessageId(), 10000);
        }
    }

    public void CallBackDataHandler(Update update) throws InterruptedException, TelegramApiException, IOException {
        long idUser = update.getCallbackQuery().getFrom().getId();
        String firstName = update.getCallbackQuery().getFrom().getFirstName();
        String userName = update.getCallbackQuery().getFrom().getUserName();
        String messageText = update.getCallbackQuery().getData();
        String[] components = messageText.split("\\s", 3);
        String eventName = "";
        if (components.length == 3) {
            eventName = components[2];
        }
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        telegramBot.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId(), 10);
        usersActions.info(userName + " " + messageText);
        if (messageText.equals("join event " + eventName)) {
            telegramBot.addParticipant(firstName, userName, chatId, idUser, eventName);
        } else if (messageText.equals("left event " + eventName)) {
            telegramBot.removeParticipant(chatId, firstName, idUser, eventName);
        } else if (messageText.equals("nothing event")) {
            telegramBot.showMessage(chatId, "Определяйтесь!");
        } else if (messageText.equals("vasya event " + eventName)) {
            if (!participantService.getParticipantByUserId(idUser).getNickName().equals("Gunhsik")) {
                telegramBot.showMessage(chatId, "Ты не Вася!");
            } else {
                telegramBot.showMessage(chatId, "Привет, Вася!");
            }
        } else {
            if (eventService.getAllEvents().stream().map(EventDTO::getEventName)
                    .toList().contains(messageText)) {
                telegramBot.showEvent(chatId, messageText);
            }
        }
    }
}
