package ru.sharanov.SearchForMessagesBot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.SearchForMessagesBot.Storage.DBEvents;
import ru.sharanov.SearchForMessagesBot.Storage.DBparticipant;
import ru.sharanov.SearchForMessagesBot.config.BotConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private final DBparticipant dBparticipant = new DBparticipant();

    private InlineKeyboardMarkup replyKeyboardMarkup;

    public TelegramBot(BotConfig config) throws IOException {
        this.config = config;
//        initKeyboard();
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

            if(update.getMessage().getText().equals("Hello")){
                try {
//                    execute(sendInlineKeyBoardMessage(update.getMessage().getText().toLowerCase(Locale.ROOT), update.getMessage().getChatId()));
                    execute(sendInlineKeyBoardMessage(update.getCallbackQuery().getMessage().getText().toLowerCase(Locale.ROOT), update.getMessage().getChatId()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }


//                String messageText = update.getMessage().getText().toLowerCase(Locale.ROOT);
//                long chatId = update.getMessage().getChatId();

//                System.out.println(messageText);
//                switch (messageText) {
//                    case "/start" -> startCommandReceived(chatId, update.getMessage().getFrom().getFirstName());
//                    case "/мероприятия" -> getEvents(chatId);
//                    case "/добавь меня" -> addParticipant(update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getUserName(), chatId);
//                    case "/список" -> getParticipant(chatId);
//                    case "/удали меня" -> removeParticipant(chatId, update.getMessage().getFrom().getUserName());
////                    case "/help" -> showCommand(chatId);
//                    default -> sendMessage(chatId, "Sorry, command was not recognized");
//                }
        }
    }

//    private void showCommand(long chatId) {
//        String answer = """
//                Список комманд:
//                /Мероприятия - показать список ближайших мероприятий
//                /Добавь меня - добавить участника на ближайшее мероприятие
//                /Список - показать список участников ближайшего мероприятия
//                /Удали меня - исключить из участников ближайшего мероприятия""";
//        sendMessage(chatId, answer.trim());
//    }
//
//    private void removeParticipant(long chatId, String userName) throws IOException {
//        dBparticipant.removeParticipants(userName);
//        sendMessage(chatId, "Вы исключены из ближайшего мероприятия");
//    }
//
//    private void getParticipant(long chatId) throws IOException {
//        ArrayList<String> participants = dBparticipant.getParticipants();
//        StringBuilder answer = new StringBuilder("Список участников ближайшего мероприятия:\n");
//        for (int i = 0; i < participants.size(); i++) {
//            answer.append(i + 1).append(". ").append(participants.get(i)).append("\n");
//        }
//        if (participants.isEmpty()) {
//            answer = new StringBuilder("Список участников ближайшего мероприятия пуст");
//        }
//        sendMessage(chatId, answer.toString().trim());
//    }
//
//    private void addParticipant(String firstName, String nickName, long chatId) throws IOException {
//        if (!dBparticipant.addParticipant(firstName, nickName).isEmpty()) {
//            String answer = "Вы уже добавлены";
//            sendMessage(chatId, answer);
//        } else {
//            String answer = "Вы успешно добавлены";
//            sendMessage(chatId, answer);
//        }
//    }
//
//    private void startCommandReceived(long chatId, String name) {
//        String answer = "Hi," + name + ", nice to meet you";
//        sendMessage(chatId, answer);
//    }
//
//    private void getEvents(long chatId) throws IOException {
//        DBEvents dbEvents = new DBEvents();
//        ArrayList<String> events = dbEvents.getEvents();
//        StringBuilder answer = new StringBuilder("Список ближайших мероприятий:\n");
//        for (int i = 0; i < events.size(); i++) {
//            answer.append(i + 1).append(". ").append(events.get(i)).append("\n");
//        }
//        if (events.isEmpty()) {
//            answer = new StringBuilder("Список ближайших мероприятий пуст");
//        }
//        sendMessage(chatId, answer.toString().trim());
//    }

    private SendMessage sendInlineKeyBoardMessage(String textToSend, long chatId ) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton helpButton = new InlineKeyboardButton();
        InlineKeyboardButton addButton = new InlineKeyboardButton();
        helpButton.setText("help");
        helpButton.setCallbackData("Button \"Тык\" has been pressed");
        helpButton.setText("Добавь меня");
        helpButton.setCallbackData("/Добавь меня");
        List<InlineKeyboardButton> keyboardRowFirst = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRowSecond = new ArrayList<>();
        keyboardRowFirst.add(helpButton);
        keyboardRowSecond.add(addButton);
        List<List <InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardRowFirst);
        keyboardRows.add(keyboardRowSecond);
        inlineKeyboardMarkup.setKeyboard(keyboardRows);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }





//    private void sendMessage(long chatId, String textToSend) {
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        InlineKeyboardButton helpButton = new InlineKeyboardButton();
//        InlineKeyboardButton addButton = new InlineKeyboardButton();
//        helpButton.setText("help");
//        helpButton.setCallbackData("/help");
//        helpButton.setText("Добавь меня");
//        helpButton.setCallbackData("/Добавь меня");
//        List<InlineKeyboardButton> keyboardRowFirst = new ArrayList<>();
//        List<InlineKeyboardButton> keyboardRowSecond = new ArrayList<>();
//        keyboardRowFirst.add(helpButton);
//        keyboardRowSecond.add(addButton);
//        List<List <InlineKeyboardButton>> keyboardRows = new ArrayList<>();
//        keyboardRows.add(keyboardRowFirst);
//        keyboardRows.add(keyboardRowSecond);
//        inlineKeyboardMarkup.setKeyboard(keyboardRows);
//
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(textToSend);
//        message.setReplyMarkup(inlineKeyboardMarkup);
//
//        try {
//            execute(message);
//        } catch (TelegramApiException exception) {
//            System.out.println(exception.getMessage());
//        }
//    }

//    void initKeyboard() {
        //Создаем объект будущей клавиатуры и выставляем нужные настройки
//        replyKeyboardMarkup = new InlineKeyboardMarkup();
//        replyKeyboardMarkup.setResizeKeyboard(true); //подгоняем размер
//        replyKeyboardMarkup.setOneTimeKeyboard(false); //скрываем после использования

        //Создаем список с рядами кнопок
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        InlineKeyboardButton helpButton = new InlineKeyboardButton();
//        InlineKeyboardButton addButton = new InlineKeyboardButton();
//        helpButton.setText("help");
//        helpButton.setCallbackData("/help");
//        helpButton.setText("Добавь меня");
//        helpButton.setCallbackData("/Добавь меня");
//        List<InlineKeyboardButton> keyboardRowFirst = new ArrayList<>();
//        List<InlineKeyboardButton> keyboardRowSecond = new ArrayList<>();
//        keyboardRowFirst.add(helpButton);
//        keyboardRowSecond.add(addButton);
//        List<List <InlineKeyboardButton>> keyboardRows = new ArrayList<>();
//        keyboardRows.add(keyboardRowFirst);
//        keyboardRows.add(keyboardRowSecond);
//        replyKeyboardMarkup.setKeyboard(keyboardRows);
        //Создаем один ряд кнопок и добавляем его в список

        //Добавляем одну кнопку с текстом "help" наш ряд

//        keyboardRowSecond.add(new InlineKeyboardButton("Delete me"));

        //добавляем лист с одним рядом кнопок в главный объект
//        replyKeyboardMarkup.setKeyboard(keyboardRows);
//    }
}
