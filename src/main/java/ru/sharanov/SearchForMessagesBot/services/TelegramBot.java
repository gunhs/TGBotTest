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
import ru.sharanov.SearchForMessagesBot.config.BotConfig;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.model.Participant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
                }
            } else if (update.hasCallbackQuery()) {
                long idUser = update.getCallbackQuery().getFrom().getId();
                String firstName = update.getCallbackQuery().getFrom().getFirstName();
                String userName = update.getCallbackQuery().getFrom().getUserName();
                String messageText = update.getCallbackQuery().getData();
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                System.out.println(userName + " " + messageText);

                switch (messageText) {
                    case "list events" -> getEvents(chatId);
//                    case "you join to event" -> addParticipant(firstName, userName, chatId);
                    case "list participants" -> getParticipants(chatId);
                    case "you left event" -> removeParticipant(chatId, userName, firstName, idUser);
                    case "show events" -> execute(showEventsButton(chatId));
                    default -> addParticipant(firstName, userName, chatId, messageText, idUser);
                }
            }
        } catch (TelegramApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private SendMessage showEventsButton(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        eventService.getAllEvents().forEach(e -> {
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(e.getEventName());
            inlineKeyboardButton1.setCallbackData(e.getEventName());
            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            keyboardButtonsRow1.add(inlineKeyboardButton1);
            rows.add(keyboardButtonsRow1);
        });
        inlineKeyboardMarkup.setKeyboard(rows);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите меропиятие:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;

    }

    private void addParticipant(String firstName, String nickName, long chatId, String event, long userId)
            throws IOException, TelegramApiException, InterruptedException {
//            showMessage(chatId, "Вы уже добавлены");
        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setName(firstName);
        participantDTO.setNickName(nickName);
        participantDTO.setUserId(userId);
        participantService.addParticipant(participantDTO, event);
        showMessage(chatId, firstName + "  теперь участвует в мероприятии " + event);
    }

    private void removeParticipant(long chatId, String userName, String name, long idUser) throws IOException, TelegramApiException, InterruptedException {
        if (participantService.getParticipantByUserId(idUser) != null) {
            String answer = name + "  больше не участвует в мероприятии";
            showMessage(chatId, answer);
            participantService.delParticipant(idUser);
        }
    }

    private void getParticipants(long chatId) throws IOException, TelegramApiException, InterruptedException {
        StringBuilder answer = new StringBuilder("Список участников ближайшего мероприятия:\n");
        AtomicInteger i = new AtomicInteger(1);
        List<Participant> participants = participantService.getAllParticipants();
        if (participants.isEmpty()) {
            answer = new StringBuilder("Список участников ближайшего мероприятия пуст");
        }
        StringBuilder finalAnswer = answer;
        StringBuilder finalAnswer1 = answer;
        eventService.getAllEvents().forEach(e -> {
                    finalAnswer1.append(i.getAndIncrement()).append(". ").append(e.getEventName()).append(", ")
                            .append(e.getDate()).append(", ").append(e.getAddress()).append("\n");
                    AtomicInteger number = new AtomicInteger(1);
                    participantService.getAllParticipantByEvent(e.getEventName())
                            .forEach(p -> finalAnswer.append("\t\t\t\t").append(number.getAndIncrement())
                                    .append(". ").append(p.getName()).append(" (@")
                                    .append(p.getNickName()).append(")").append("\n"));
                }
        );
        showMessage(chatId, answer.toString().trim());
    }

    private void getEvents(long chatId) throws IOException, TelegramApiException, InterruptedException {
        StringBuilder answer = new StringBuilder("Список ближайших мероприятий:\n");
        AtomicInteger i = new AtomicInteger(1);
        if (eventService.getAllEvents().isEmpty()) {
            showMessage(chatId, "Список ближайших мероприятий пуст");
            return;
        }
        eventService.getAllEvents().forEach(e -> answer.append(i.getAndIncrement()).append(". ")
                .append(e.getEventName()).append(", ").append(e.getDate()).append(", ")
                .append(e.getAddress()).append("\n")
        );
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

    public SendMessage sendInlineKeyBoardMessage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Добавить меня");
        inlineKeyboardButton1.setCallbackData("show events");
        inlineKeyboardButton2.setText("Удалить меня");
        inlineKeyboardButton2.setCallbackData("You left event");
        inlineKeyboardButton3.setText("Мероприятия");
        inlineKeyboardButton3.setCallbackData("list events");
        inlineKeyboardButton4.setText("Список участников");
        inlineKeyboardButton4.setCallbackData("list participants");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        keyboardButtonsRow4.add(inlineKeyboardButton4);
        keyboardButtonsRow3.add(inlineKeyboardButton3);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow4);
        rowList.add(keyboardButtonsRow3);
        inlineKeyboardMarkup.setKeyboard(rowList);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Меню\nМеропиятий: " + eventService.getAllEvents().size());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
}
