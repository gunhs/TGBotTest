package ru.sharanov.SearchForMessagesBot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageId;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.SearchForMessagesBot.config.BotConfig;
import org.telegram.api.messages.*;

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
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            System.out.println(messageText);
            switch (messageText) {
                case "/start" -> startCommandReceived(chatId, update.getMessage().getFrom().getFirstName());
//                case "/Katya" -> katyaCommandReceived(chatId);
//                case "/Anya" -> anyaCommandReceived(chatId);
                case "/Настя" -> find36(chatId);
                default -> sendMessage(chatId, "Sorry, command was not recognized");
            }
        }
    }

    private void find36(long chatId) {

//        String answer = "Анечка, ты просто космос ✨" ;
        String answer = "Бот, который отвечает Насте" ;
        sendMessage(chatId, answer);
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi," +  name + ", nice to meet you";
        sendMessage(chatId, answer);
    }

    private void katyaCommandReceived(long chatId){
        String answer = "Эта строчка специально для Кати, чтобы она не выпендривалась";
        sendMessage(chatId, answer);
    }

    private void anyaCommandReceived(long chatId){
        String answer = "Анечка, ты просто космос ✨! ";
        sendMessage(chatId, answer);
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
