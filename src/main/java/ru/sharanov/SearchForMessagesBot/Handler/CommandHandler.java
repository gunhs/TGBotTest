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
        String eventId = "";
        if (components.length == 3) {
            eventId = components[2];
        }
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        telegramBot.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId(), 10);
        usersActions.info(userName + " " + messageText);
        if (messageText.equals("join event " + eventId)) {
            telegramBot.addParticipant(firstName, userName, chatId, idUser, eventId);
        } else if (messageText.equals("left event " + eventId)) {
            telegramBot.removeParticipant(chatId, firstName, idUser, eventId);
        } else if (messageText.equals("back")) {
            telegramBot.showEventsButton(chatId);
        } else if (messageText.equals("next event " + eventId)) {
            String nextEventName= telegramBot.getNextEventName(eventId);
            telegramBot.showEvent(chatId, nextEventName);
        } else if (messageText.equals("join all")) {
            telegramBot.participantJoinAll(firstName, userName, chatId, idUser);
        } else {
            if (eventService.getAllEventsDTO().stream().map(e-> String.valueOf(e.getId()))
                    .toList().contains(messageText)) {
                telegramBot.showEvent(chatId, messageText);
            }
        }
    }
}
