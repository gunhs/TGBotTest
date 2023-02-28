package ru.sharanov.SearchForMessagesBot.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.SearchForMessagesBot.Handler.ButtonHandler;
import ru.sharanov.SearchForMessagesBot.Handler.CommandHandler;
import ru.sharanov.SearchForMessagesBot.config.BotConfig;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.utils.DateComparator;
import ru.sharanov.SearchForMessagesBot.utils.DateTypeConverter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

@Service
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final EventService eventService;
    private final ParticipantService participantService;
    private final CommandHandler commandHandler;


    public TelegramBot(BotConfig config, EventService eventService, ParticipantService participantService) {
        this.config = config;
        this.eventService = eventService;
        this.participantService = participantService;
        commandHandler = new CommandHandler(this, participantService, eventService);
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
                commandHandler.MessageWatcherHandler(update);
            } else if (update.hasCallbackQuery()) {
                commandHandler.CallBackDataHandler(update);
            }
        } catch (TelegramApiException | IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void showEventsButton(long chatId) throws TelegramApiException {
        List<EventDTO> events = eventService.getAllEvents();
        DateComparator comparator = new DateComparator();
        events.sort(comparator);
        InlineKeyboardMarkup inlineKeyboardMarkup = ButtonHandler.showEventButton(events);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите меропиятие:");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        execute(sendMessage);
    }

    public void showEvent(long chatId, String eventName) throws TelegramApiException {
        Event event = eventService.getEventByEventName(eventName);
        StringBuilder participants = new StringBuilder();
        AtomicInteger number = new AtomicInteger(1);
        event.getParticipants().forEach(p -> participants.append(number.getAndIncrement())
                .append(". ").append(p.getName()).append(" (@")
                .append(p.getNickName()).append(")").append("\n"));
        String info = "Что: " + event.getEventName() + "\n" +
                "Где: " + event.getAddress() + "\n" +
                "Когда: " + DateTypeConverter.localDateTimeToStringConverter(event.getDate()) + "\n" +
                "Список участников:\n" + participants;
        InlineKeyboardMarkup inlineKeyboardMarkup = ButtonHandler.controlEventButton(eventName);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(info);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        execute(sendMessage);
    }

    public void addParticipant(String firstName, String nickName, long chatId, long userId, String eventName)
            throws TelegramApiException, InterruptedException {
        if (eventService.getEventByEventName(eventName).getParticipants()
                .stream().map(Participant::getUserId).toList().contains(userId)) {
            showMessage(chatId, "Вы уже добавлены");
            return;
        }
        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setName(firstName);
        participantDTO.setNickName(nickName);
        participantDTO.setUserId(userId);
        participantService.addParticipant(participantDTO, eventName);
        showMessage(chatId, firstName + "  теперь участвует в мероприятии " + eventName);
    }

    public void removeParticipant(long chatId, String name, long idUser, String eventName)
            throws TelegramApiException, InterruptedException {
        if (participantService.getParticipantByUserId(idUser) != null) {
            String answer = name + "  больше не участвует в мероприятии";
            showMessage(chatId, answer);
            participantService.delParticipant(idUser, eventName);
        }
    }

    public void showMessage(long chatId, String textToSend) throws TelegramApiException, InterruptedException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        Message sentOutMessage = execute(message);
        if (textToSend.equals(message.getText())) {
            deleteMessage(sentOutMessage.getChatId(), sentOutMessage.getMessageId(), 30000);
        }
    }

    public void deleteMessage(long chatId, int messageId, long time)
            throws InterruptedException, TelegramApiException {
        sleep(time);
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        execute(deleteMessage);
    }
}
