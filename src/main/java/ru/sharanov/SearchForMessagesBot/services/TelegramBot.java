package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.SearchForMessagesBot.Storage.DBEvents;
import ru.sharanov.SearchForMessagesBot.config.BotConfig;
import ru.sharanov.SearchForMessagesBot.entities.Event;
import ru.sharanov.SearchForMessagesBot.entities.Participant;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;

@Service
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private final EventService eventService;
    private final ParticipantService participantService;


    public TelegramBot(BotConfig config, EventService eventService, ParticipantService participantService) {
        this.config = config;
        this.eventService = eventService;
        this.participantService = participantService;
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
                String textMessage = update.getMessage().getText();
                long chatIdMessage = update.getMessage().getChatId();
                if (textMessage.equals("/бот")) {
                    execute(sendInlineKeyBoardMessage(chatIdMessage));
                    execute(deleteMessage(chatIdMessage, update.getMessage().getMessageId(), 10000));
                } else if (textMessage.matches("[a-zA-ZА-я\\s]+, \\d{2}\\.\\d{2}\\.\\d{4}, [a-zA-ZА-я\\.\\s\\d]+")) {
                    addEvent(chatIdMessage, textMessage);
                } else if (eventService.checkWord(textMessage)) {
                    removeEvent(chatIdMessage, textMessage);
                }else if (textMessage.matches("Редактировать [А-яa-zA-Z\\s+\na-zA-ZА-я\\s]+, \\d{2}\\.\\d{2}\\.\\d{4}, [a-zA-ZА-я\\.\\s\\d]+]")){
                    editEvent(textMessage);
                }
            } else if (update.hasCallbackQuery()) {
                String messageText = update.getCallbackQuery().getData().toLowerCase(Locale.ROOT);
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                System.out.println(update.getCallbackQuery().getFrom().getUserName() + " " + messageText);
                switch (messageText) {
                    case "list events" -> getEvents(chatId);
                    case "you join to event" -> addParticipant(
                            update.getCallbackQuery().getFrom().getFirstName(),
                            update.getCallbackQuery().getFrom().getUserName(), chatId);
                    case "list participants" -> getParticipants(chatId);
                    case "you left event" -> removeParticipant(chatId,
                            update.getCallbackQuery().getFrom().getUserName(),
                            update.getCallbackQuery().getFrom().getFirstName()
                    );
                    case "add event" -> showAddEventMessage(chatId);
                }
            }
        } catch (TelegramApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void editEvent(String textMessage) {

    }

    private void addParticipant(String firstName, String nickName, long chatId) throws IOException, TelegramApiException, InterruptedException {
        if (participantService.checkNickName(nickName)) {
            showMessage(chatId, "Вы уже добавлены");
        } else {
            Participant participant = new Participant();
            participant.setName(firstName);
            participant.setNickName(nickName);
            participantService.addParticipant(participant);
            showMessage(chatId, firstName + "  теперь участвует в мероприятии");
        }
    }

    private void removeParticipant(long chatId, String userName, String name) throws IOException, TelegramApiException, InterruptedException {
        if (participantService.getParticipantByNickName(userName) != null) {
            String answer = name + "  больше не участвует в мероприятии";
            showMessage(chatId, answer);
            participantService.delParticipant(userName);
        }
    }

    private void getParticipants(long chatId) throws IOException, TelegramApiException, InterruptedException {
        List<Participant> participants = participantService.getAllParticipants();
        StringBuilder answer = new StringBuilder("Список участников ближайшего мероприятия:\n");
        for (int i = 0; i < participants.size(); i++) {
            answer.append(i + 1).append(". ").append(participants.get(i).getName()).
                    append(" (@").append(participants.get(i).getNickName()).append(")").append("\n");
        }
        if (participants.isEmpty()) {
            answer = new StringBuilder("Список участников ближайшего мероприятия пуст");
        }
        showMessage(chatId, answer.toString().trim());
    }

    private void showAddEventMessage(long chatId) throws TelegramApiException, InterruptedException {
        showMessage(chatId, """
                Чтобы добавить мероприятие введите его в формате:
                Название, дата, адресс
                например:
                Мансарда, 21.01.2023, ул. Марата 36""");
    }

    private void addEvent(long chatId, String event) throws TelegramApiException, InterruptedException {
        String answer = eventService.addEvent(event);
        showMessage(chatId, answer);
    }

    private void removeEvent(long chatId, String eventName) throws TelegramApiException, InterruptedException {
        eventService.delEvent(eventName);
        String answer = "Мероприятие удалено";
        showMessage(chatId, answer);
    }


    private void getEvents(long chatId) throws IOException, TelegramApiException, InterruptedException {

        StringBuilder answer = new StringBuilder("Список ближайших мероприятий:\n");
        int i = 1;
        for (Event e : eventService.getAllEvents()) {
            answer.append(i++).append(". ").append(e.getEventName()).append(", ")
                    .append(e.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .append(", ").append(e.getAddress()).append("\n");
        }
        if (eventService.getAllEvents().isEmpty()) {
            answer = new StringBuilder("Список ближайших мероприятий пуст");
        }
        showMessage(chatId, answer.toString().trim());
    }

    private void showMessage(long chatId, String textToSend) throws TelegramApiException, InterruptedException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        Message sentOutMessage = execute(message);
        if (textToSend.equals(message.getText())) {
            execute(deleteMessage(sentOutMessage.getChatId(), sentOutMessage.getMessageId(), 30000));
        }
    }

    private DeleteMessage deleteMessage(long chatId, int messageId, long time) throws InterruptedException {
        sleep(time);
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        return deleteMessage;
    }

    public static SendMessage sendInlineKeyBoardMessage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Добавить меня");
        inlineKeyboardButton1.setCallbackData("You join to event");
        inlineKeyboardButton2.setText("Удалить меня");
        inlineKeyboardButton2.setCallbackData("You left event");
        inlineKeyboardButton3.setText("Мероприятия");
        inlineKeyboardButton3.setCallbackData("list events");
        inlineKeyboardButton4.setText("Список участников");
        inlineKeyboardButton4.setCallbackData("list participants");
        inlineKeyboardButton5.setText(" Добавить мероприятие");
        inlineKeyboardButton5.setCallbackData("add event");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(inlineKeyboardButton3);
        keyboardButtonsRow1.add(inlineKeyboardButton4);
        keyboardButtonsRow2.add(inlineKeyboardButton5);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Меню");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
}
