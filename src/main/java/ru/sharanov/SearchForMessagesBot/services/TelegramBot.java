package ru.sharanov.SearchForMessagesBot.services;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
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
import ru.sharanov.SearchForMessagesBot.model.Guest;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.utils.DateTypeConverter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@Service
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final EventService eventService;
    private final ParticipantService participantService;
    private final CommandHandler commandHandler;
    private final String chatAdminId;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TelegramBot.class);

    public TelegramBot(BotConfig config, EventService eventService, ParticipantService participantService,
                       @Value("${chatAdminId}") String chatAdminId) {
        this.config = config;
        this.eventService = eventService;
        this.participantService = participantService;
        this.chatAdminId = chatAdminId;
        commandHandler = new CommandHandler(this, eventService);
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
                commandHandler.messageWatcherHandler(update);

            } else if (update.hasCallbackQuery()) {
                commandHandler.callBackDataHandler(update);
            }
        } catch (TelegramApiException | IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void showMenu(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = ButtonHandler.showMenuButton();
        execute(getMessage(chatId, "Главное меню", inlineKeyboardMarkup));
    }

    public void showFutureEventsButton(long chatId) throws TelegramApiException {
        List<EventDTO> events = eventService.getAllEventsDTO().stream()
                .filter(e -> (e.getDate().isAfter(LocalDateTime.now()))).collect(Collectors.toList());
        InlineKeyboardMarkup inlineKeyboardMarkup = ButtonHandler.showFutureEventButton(events);
        execute(getMessage(chatId, "Выберите мероприятие:", inlineKeyboardMarkup));
    }

    public void showPastEventsButton(long chatId) throws TelegramApiException {
        List<EventDTO> events = eventService.getAllEventsDTO().stream()
                .filter(e -> (!e.getDate().isAfter(LocalDateTime.now()))).collect(Collectors.toList());
        InlineKeyboardMarkup inlineKeyboardMarkup = ButtonHandler.showPastEventButton(events);
        execute(getMessage(chatId, "Выберите мероприятие:", inlineKeyboardMarkup));
    }

    public void showFutureEvent(long chatId, String eventId) throws TelegramApiException {
        Event event = eventService.getEventById(eventId);
        StringBuilder participants = new StringBuilder();
        AtomicInteger number = new AtomicInteger(1);
        List<Guest> guests = eventService.getGuestsByEventId(Integer.parseInt(eventId));
        event.getParticipants().forEach(p -> {
            Guest guest = guests.stream().filter(g -> Objects.equals(g.getId().getParticipantID(),
                    p.getUserId())).findFirst().orElse(null);

            int countOfGuests = guest != null ? guest.getCount() : 0;
            participants.append(number.getAndIncrement())
                    .append(". ").append(p.getName()).append(" (")
                    .append(p.getNickName() == null ? "☠" : p.getNickName())
                    .append(")")
                    .append(countOfGuests == 0 ? "" : " +" + countOfGuests)
                    .append("\n");
        });
        String info = "Что: " + event.getEventName() + "\n" +
                "Где: " + event.getAddress() + "\n" +
                "Когда: " + DateTypeConverter.localDateTimeToStringConverter(event.getDate()) + "\n" +
                (event.getUrl().isEmpty() ? "" : ("сайт: " + event.getUrl()) + "\n") +
                (event.getParticipants().isEmpty() ? "" : "\nСписок участников:\n" + participants + "\n");
        InlineKeyboardMarkup inlineKeyboardMarkup = ButtonHandler.controlEventButton(eventId);
        execute(getMessage(chatId, info, inlineKeyboardMarkup));
    }

    public void showPastEvent(long chatId, String eventId) throws TelegramApiException {
        Event event = eventService.getEventById(eventId);
        String info = "Что: " + event.getEventName() + "\n" +
                "Где: " + event.getAddress() + "\n" +
                "Когда: " + DateTypeConverter.localDateTimeToStringConverter(event.getDate());
        InlineKeyboardMarkup inlineKeyboardMarkup = ButtonHandler.backPastEventButton();
        execute(getMessage(chatId, info, inlineKeyboardMarkup));
    }

    public void addParticipant(String firstName, String nickName, long chatId, long userId, String eventId)
            throws TelegramApiException {
        if (eventService.getEventById(eventId).getParticipants()
                .stream().anyMatch(p -> p.getUserId() == userId)) {
            showMessage(chatId, "Вы уже добавлены", 10000);
            return;
        }
        String eventName = eventService.getEventById(eventId).getEventName();
        participantService.addParticipant(eventId, chatId, firstName, nickName, userId);
        showMessage(chatId, firstName + "  теперь участвует в мероприятии " + eventName, 10000);
        logger.info(firstName + " присоединился к " + eventName);
    }

    public void removeParticipant(long chatId, String name, long idUser, String eventId)
            throws TelegramApiException {
        Participant participant = participantService.getParticipantByUserId(idUser);
        if (!eventService.getEventById(eventId).getParticipants()
                .contains(participant)) {
            showMessage(chatId, "Вы не участвовали в мероприятии", 10000);
            return;
        }
        if (participant != null) {
            showMessage(chatId, name + "  больше не участвует в мероприятии", 10000);
            participantService.delParticipant(idUser, eventId);
        }
        assert participant != null;
        logger.info(participant.getName() + " больше не участвует в "
                + eventService.getEventById(eventId).getEventName());
    }

    public void showMessage(long chatId, String textToSend, long time) throws TelegramApiException {
        Message sentOutMessage = execute(
                SendMessage.builder().chatId(String.valueOf(chatId)).text(textToSend).disableNotification(true).build());
        deleteMessage(sentOutMessage.getChatId(), sentOutMessage.getMessageId(), time);
    }

    public void deleteMessage(long chatId, int messageId, long time) {
        new Thread(() -> {
            try {
                sleep(time);
                execute(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
            } catch (InterruptedException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void participantJoinAll(String firstName, String userName, long chatId, long idUser)
            throws TelegramApiException {
        for (EventDTO e : eventService.getAllEventsDTO()) {
            if (eventService.getEventById(String.valueOf(e.getId())).getParticipants()
                    .stream().map(Participant::getUserId).toList().contains(idUser)) {
                showMessage(chatId, firstName + ", Вы уже добавлены на мероприятие " + e.getEventName(), 10000);
                continue;
            }
            participantService.addParticipant(String.valueOf(e.getId()), chatId, firstName, userName, idUser);
        }
        showMessage(chatId, firstName + "  теперь участвует во всех мероприятиях", 10000);
        logger.info(firstName + " присоединился ко всем мероприятиям");
    }

    public String getNextFutureEventId(String eventId) {
        ArrayList<EventDTO> events = (ArrayList<EventDTO>) eventService.getAllEventsDTO()
                .stream().filter(e -> !e.isDone()).collect(Collectors.toList());
        return getId(events, eventId);
    }

    public String getNextPastEventId(String eventId) {
        ArrayList<EventDTO> events = (ArrayList<EventDTO>) eventService.getAllEventsDTO()
                .stream().filter(EventDTO::isDone).collect(Collectors.toList());
        return getId(events, eventId);
    }

    private String getId(ArrayList<EventDTO> events, String eventId) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId() == Integer.parseInt(eventId)) {
                i = (i + 1 == events.size()) ? 0 : i + 1;
                return String.valueOf(events.get(i).getId());
            }
        }
        return "";
    }

    public void selectEvent(long chatId, String eventId) throws TelegramApiException {
        Event event = eventService.getEventById(eventId);
        if (event.isDone()) {
            showPastEvent(chatId, eventId);
        } else {
            showFutureEvent(chatId, eventId);
        }
    }

    public void showMap(long chatId, String eventId) throws TelegramApiException {
        Event event = eventService.getEventById(eventId);
        InlineKeyboardMarkup inlineKeyboardMarkup = ButtonHandler.closeMap(eventId);
        SendVenue sendVenue = SendVenue.builder().chatId(chatId).address(event.getAddress()).title("Адрес:")
                .latitude((double) event.getLatitude()).longitude((double) event.getLongitude())
                .replyMarkup(inlineKeyboardMarkup).disableNotification(true).build();
        execute(sendVenue);
        long now = System.currentTimeMillis();
        long executeTime = now + 60000;
        if (executeTime == System.currentTimeMillis()) {
            Message sentOutMessage = execute(sendVenue);
            deleteMessage(chatId, sentOutMessage.getMessageId(), 0);
        }
    }

    public SendMessage getMessage(long chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return SendMessage.builder().chatId(chatId).text(text).replyMarkup(inlineKeyboardMarkup)
                .disableNotification(true).build();
    }

    public void closeApp(long chatId) throws TelegramApiException {
        Message sentOutMessage = execute(SendMessage.builder().chatId(chatId).text("Пока!").build());
        deleteMessage(chatId, sentOutMessage.getMessageId(), 5);
    }

    public void addGuest(long chatId, String eventId, long idUser, String firstName)
            throws TelegramApiException {
        String message = eventService.addGuest(Integer.parseInt(eventId), idUser) ? firstName + " добавил гостя" :
                "Слишком много гостей";
        showMessage(chatId, message, 10000);
    }

    public void removeGuest(long chatId, String eventId, long idUser, String firstName)
            throws TelegramApiException {
        String message = eventService.removeGuest(Integer.parseInt(eventId), idUser) ? firstName + " удалил гостя" :
                "Невозможно удалить гостя";
        showMessage(chatId, message, 10000);
    }

//    public void checkAdmin(long chatIdMessage) {
//        GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
//        getChatAdministrators.setChatId(chatIdMessage);
//        try {
//            ArrayList<ChatMember> administrators = execute(getChatAdministrators);
//            for (ChatMember administrator : administrators) {
//                User user = administrator.getUser();
//                if (user.getFirstName().equals(getBotUsername())) {
//                    System.out.println(chatIdMessage);
//                    break;
//                }
//            }
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }

    @Scheduled(cron = "0 55 16 * * *")
    public void congratulation() throws TelegramApiException {
        String namesakes = participantService.getNamesakes();
        if (!namesakes.isEmpty()) {
            showMessage(Long.parseLong(chatAdminId), "Сегодня день рождения у " + namesakes + "!!! " +
                    "Поздравляем! ", 43200000);
        }
    }

    public void addBirthday(long chatId, String text, long userId) throws TelegramApiException {
        Participant participant = participantService.getParticipantByUserId(userId);
        if (participant == null) {
            showMessage(chatId, "Вы не записывались на мероприятия", 10000);
            return;
        }
        String message = participantService.addBirthdayInDB(text.toLowerCase(), participant) ?
                participant.getName() + " внёс информацию о своём дне рождения" : "Не корректно введена дата";
        showMessage(chatId, message, 10000);
    }

    public void helloMessage(long chatIdMessage) throws TelegramApiException {
        Message sentOutMessage = execute(SendMessage.builder().chatId(chatIdMessage).text("""
                Привет! Я бот и живу в этом чате
                Я покажу, какие мероприятия ожидаюся, а какие - уже прошли.
                А ещё я умею запоминать дни рождения!
                Напишите: мой день рождения и укажите дату
                Например:
                мой день рождения 1 января 2021
                мой день рождения 2 февраля
                мой день рождения 3.03.2023
                Правда, я запоминаю дни рождения только тех, кто ходит на мероприятия 😉""").build());
        deleteMessage(chatIdMessage, sentOutMessage.getMessageId(), 10000);
    }

    public void showBirthdays(long chatId, long userId) throws TelegramApiException {
        StringBuilder participants = new StringBuilder();
        boolean chatMember = participantService.getParticipantByUserId(userId).isChatMember();
        List<ParticipantDTO> participantList = participantService.getAllParticipants();
        Collections.sort(participantList);
        participantList.stream()
                .filter(p -> p.getBirthday() != null)
                .filter(ParticipantDTO::isChatMember)
                .forEach(p -> participants.append(p.getName()).append(" - ")
                        .append(DateTypeConverter.localDateToStringConverter(p.getBirthday())).append("\n"));
        String text = chatMember ? participants.toString() : "Вы не сосотоите в чате. Обратитесь к @Gunhsik";
        Message sentOutMessage = execute(
                SendMessage.builder().chatId(String.valueOf(chatId)).text(text)
                        .disableNotification(true).build());
        deleteMessage(sentOutMessage.getChatId(), sentOutMessage.getMessageId(), 60000);
    }
}