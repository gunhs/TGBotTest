package ru.sharanov.SearchForMessagesBot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.SearchForMessagesBot.Storage.DBEvents;
import ru.sharanov.SearchForMessagesBot.Storage.DBparticipant;
import ru.sharanov.SearchForMessagesBot.config.BotConfig;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();
                System.out.println(messageText);
                switch (messageText) {
                    case "/start" -> startCommandReceived(chatId, update.getMessage().getFrom().getFirstName());
//                case "/Katya" -> katyaCommandReceived(chatId);
//                case "/Anya" -> anyaCommandReceived(chatId);
//                case "/Настя" -> find36(chatId);
                    case "/Мероприятия" -> getEvents(chatId);
                    case "/Добавить меня" -> addParticipant(chatId, update.getMessage().getFrom().getFirstName());
                    case "/Список" -> getParticipant(chatId);
                    default -> sendMessage(chatId, "Sorry, command was not recognized");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getParticipant(long chatId) throws IOException {
        DBparticipant dBparticipant = new DBparticipant();
        ArrayList<String> participants = dBparticipant.getParticipants();
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < participants.size(); i++) {
            answer.append(i + 1).append(". ").append(participants.get(i)).append("\n");
        }
        sendMessage(chatId, answer.toString().trim());
    }

    private void addParticipant(long chatId, String firstName) throws IOException {
        DBparticipant dBparticipant= new DBparticipant();
        dBparticipant.addParticipant(firstName);
    }

    private void find36(long chatId) {
        String answer = "Бот, который отвечает Насте";
        sendMessage(chatId, answer);
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi," + name + ", nice to meet you";
        sendMessage(chatId, answer);
    }

    private void katyaCommandReceived(long chatId) {
        String answer = "Эта строчка специально для Кати, чтобы она не выпендривалась";
        sendMessage(chatId, answer);
    }

    private void anyaCommandReceived(long chatId) {
        String answer = "Анечка, ты просто космос ✨! ";
        sendMessage(chatId, answer);
    }

    private void getEvents(long chatId) throws IOException {
        DBEvents dbEvents = new DBEvents();
        ArrayList<String> events = dbEvents.getEvents();
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < events.size(); i++) {
            answer.append(i + 1).append(". ").append(events.get(i)).append("\n");
        }
        sendMessage(chatId, answer.toString().trim());
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
