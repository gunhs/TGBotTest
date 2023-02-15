package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.SearchForMessagesBot.Storage.DBEvents;
import ru.sharanov.SearchForMessagesBot.Storage.DBparticipant;
import ru.sharanov.SearchForMessagesBot.config.BotConfig;
import ru.sharanov.SearchForMessagesBot.entities.Participant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;

@Service
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private final DBparticipant dBparticipant = new DBparticipant();

    private final EventService eventService;
    private final ParticipantService participantService;


    public TelegramBot(BotConfig config, EventService eventService, ParticipantService participantService) throws IOException {
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
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals("/бот")) {
                    try {
                        execute(sendInlineKeyBoardMessage(update.getMessage().getChatId()));
                        sleep(10000);
                        DeleteMessage deleteMessage = new DeleteMessage();
                        deleteMessage.setChatId(update.getMessage().getChatId());
                        deleteMessage.setMessageId(update.getMessage().getMessageId());
                        execute(deleteMessage);
                    } catch (TelegramApiException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            try {
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
                }
            } catch (TelegramApiException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
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
//            dBparticipant.removeParticipants(userName);
            String answer = name + "  больше не участвует в мероприятии";
            showMessage(chatId, answer);
            participantService.delParticipant(userName);
        }
    }

    private void getParticipants(long chatId) throws IOException, TelegramApiException, InterruptedException {
//        ArrayList<String> participants = dBparticipant.getParticipants();
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

    private void getEvents(long chatId) throws IOException, TelegramApiException, InterruptedException {
        DBEvents dbEvents = new DBEvents();
        ArrayList<String> events = dbEvents.getEvents();
        StringBuilder answer = new StringBuilder("Список ближайших мероприятий:\n");
        for (int i = 0; i < events.size(); i++) {
            answer.append(i + 1).append(". ").append(events.get(i)).append("\n");
        }
        if (events.isEmpty()) {
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
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(sentOutMessage.getChatId());
            deleteMessage.setMessageId(sentOutMessage.getMessageId());
            sleep(30000);
            execute(deleteMessage);
        }
    }

    public static SendMessage sendInlineKeyBoardMessage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Добавить меня");
        inlineKeyboardButton1.setCallbackData("You join to event");
        inlineKeyboardButton2.setText("Удалить меня");
        inlineKeyboardButton2.setCallbackData("You left event");
        inlineKeyboardButton3.setText("Мероприятия");
        inlineKeyboardButton3.setCallbackData("list events");
        inlineKeyboardButton4.setText("Список участников");
        inlineKeyboardButton4.setCallbackData("list participants");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(inlineKeyboardButton3);
        keyboardButtonsRow2.add(inlineKeyboardButton4);
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
