package ru.sharanov.SearchForMessagesBot.Handler;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.SearchForMessagesBot.services.EventService;
import ru.sharanov.SearchForMessagesBot.services.TelegramBot;

import java.io.IOException;

@Slf4j
public class CommandHandler {
    private final TelegramBot telegramBot;
    private final EventService eventService;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CommandHandler.class);

    public CommandHandler(TelegramBot telegramBot, EventService eventService) {
        this.telegramBot = telegramBot;
        this.eventService = eventService;
    }

    public void messageWatcherHandler(Update update) throws TelegramApiException, InterruptedException {
        String textMessage = update.getMessage().getText();
        long chatIdMessage = update.getMessage().getChatId();
        if (textMessage.equals("/events@EventJavaBot") || textMessage.equals("/events")) {
            telegramBot.showMenu(chatIdMessage);

            telegramBot.deleteMessage(chatIdMessage, update.getMessage().getMessageId(), 10);
            logger.info(update.getMessage().getFrom().getUserName() + " input: " + textMessage);
        }
    }

    public void callBackDataHandler(Update update) throws InterruptedException, TelegramApiException, IOException {
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
        logger.info(firstName + " (" + userName + ") click: " + messageText);
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
        } else {
            if (eventService.getAllEventsDTO().stream().map(e -> String.valueOf(e.getId()))
                    .toList().contains(messageText)) {
                telegramBot.selectEvent(chatId, messageText);
            }
        }
    }
}
