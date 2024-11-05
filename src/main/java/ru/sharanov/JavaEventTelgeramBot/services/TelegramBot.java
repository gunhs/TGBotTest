package ru.sharanov.JavaEventTelgeramBot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.JavaEventTelgeramBot.Handler.ButtonHandler;
import ru.sharanov.JavaEventTelgeramBot.Handler.CommandHandler;
import ru.sharanov.JavaEventTelgeramBot.config.BotConfig;
import ru.sharanov.JavaEventTelgeramBot.config.ChatProperties;
import ru.sharanov.JavaEventTelgeramBot.dto.EventDTO;
import ru.sharanov.JavaEventTelgeramBot.dto.ParticipantBirthdaysDto;
import ru.sharanov.JavaEventTelgeramBot.model.Event;
import ru.sharanov.JavaEventTelgeramBot.model.Guest;
import ru.sharanov.JavaEventTelgeramBot.model.Participant;
import ru.sharanov.JavaEventTelgeramBot.utils.DateTypeConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final EventService eventService;
    private final ParticipantService participantService;
    private final CommandHandler commandHandler;
    private final ChatProperties chatProperties;

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
            } else if (update.getMessage().getNewChatMembers().size() > 0) {
                User user = update.getMessage().getNewChatMembers().get(0);
                helloNewChatMember(update, user);
            }
        } catch (TelegramApiException | IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void helloNewChatMember(Update update, User user) throws TelegramApiException {
        String message = " Привет, " + user.getFirstName() + "! " +
                "Добро пожаловать в чат Skillbox Java СПб!\n" +
                "Расскажи немного о себе: с какого района, чем занимаешься?\n\n" +
                "Я бот и живу в этом чате.\n" +
                "Я покажу, какие мероприятия ожидаются, а какие - уже прошли.\n\n" +
                "А ещё я умею запоминать дни рождения!\n" +
                "Напиши: мой день рождения и укажи дату\n" +
                "Например:\n" +
                "мой день рождения 1 января 2021\n" +
                "мой день рождения 2 февраля\n" +
                "мой день рождения 3.03.2023\n" +
                "Правда, я запоминаю дни рождения только тех, кто ходит на мероприятия \uD83D\uDE09\n" +
                "Чтобы видеть дни рождения, запишитесь на мероприятие в этой группе" +
                "\n\nЧтобы позвать меня напиши /events или нажми на меню снизу";
        showMessage(update.getMessage().getChatId(), message, 180000);
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
        Map<Long, Guest> guests = eventService.getGuestsByEventId(Long.parseLong(eventId)).stream()
                .collect(Collectors.toMap(k -> k.id.getParticipantID(), Function.identity()));
        event.getParticipants().forEach(p -> {
            Guest guest = guests.get(p.getUserId());
            participants.append(number.getAndIncrement())
                    .append(". ").append(p.getName()).append(" (")
                    .append(p.getNickName() == null ? "☠" : p.getNickName())
                    .append(")")
                    .append(guest == null || guest.getCount() == 0 ? "" : " +" + guest.getCount())
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
        if (eventService.checkParticipantInEvent(Long.parseLong(eventId), participantService.getParticipantIdByUserId(userId))) {
            showMessage(chatId, "Вы уже добавлены", 10000);
            return;
        }
        String eventName = eventService.getEventById(eventId).getEventName();
        participantService.addParticipant(eventId, chatId, firstName, nickName, userId);
        showMessage(chatId, firstName + "  теперь участвует в мероприятии " + eventName, 10000);
        log.info("{} присоединился к {}", firstName, eventName);
    }

    public void removeParticipant(long chatId, String name, long userId, String eventId)
            throws TelegramApiException {
        Long participantId = participantService.getParticipantIdByUserId(userId);
        if (eventService.deleteParticipantFromEvent(Long.parseLong(eventId), participantId) == 1) {
            showMessage(chatId, name + "  больше не участвует в мероприятии", 10000);
        } else {
            showMessage(chatId, "Вы не участвовали в мероприятии", 10000);
        }
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
                showMessage(chatId, firstName +
                        ", Вы уже добавлены на мероприятие " + e.getEventName(), 10000);
                continue;
            }
            participantService.addParticipant(String.valueOf(e.getId()), chatId, firstName, userName, idUser);
        }
        showMessage(chatId, firstName + "  теперь участвует во всех мероприятиях", 10000);
        log.info("{} присоединился ко всем мероприятиям", firstName);
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
        Message sentOutMessage = execute(sendVenue);
        deleteMessage(chatId, sentOutMessage.getMessageId(), 60000);
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
        String message;
        try {
            eventService.addGuest(eventId, idUser);
            message = firstName + " добавил гостя";
        } catch (RuntimeException e) {
            message = e.getMessage();
        }
        showMessage(chatId, message, 10000);
    }

    public void removeGuest(long chatId, String eventId, long idUser, String firstName)
            throws TelegramApiException {
        String message;
        try {
            eventService.removeGuest(Long.parseLong(eventId), idUser);
            message = firstName + " удалил гостя";
        } catch (RuntimeException e) {
            message = e.getMessage();
        }
        showMessage(chatId, message, 10000);
    }

    //метод чтобы узнать id чата в котором будет работать бот
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

    @Scheduled(cron = "0 00 12 * * *")
    public void congratulation() throws TelegramApiException {
        String namesakes = participantService.getNamesakes();
        if (!namesakes.isEmpty()) {
            showMessage(Long.parseLong(chatProperties.getChatId()), "Сегодня день рождения у " + namesakes +
                    "!!! \uD83C\uDF89" +
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

    public void showBirthdays(long chatId, long userId) throws TelegramApiException {
        StringBuilder participants = new StringBuilder();
        List<ParticipantBirthdaysDto> participantList = participantService.getAllParticipantsBirthdays();
        participantList.stream().filter(ParticipantBirthdaysDto::getChatMember)
                .forEach(p -> participants
                        .append(p.getName())
                        .append(" - ")
                        .append(p.getBirthday())
                        .append("\n"));
        String text = participantService.checkParticipantIsMember(userId) ? participants.toString()
                : "Вы не состоите в чате. Обратитесь к @Gunhsik";
        Message sentOutMessage = execute(SendMessage.builder().chatId(String.valueOf(chatId)).text(text)
                .disableNotification(true).build());
        deleteMessage(sentOutMessage.getChatId(), sentOutMessage.getMessageId(), 60000);
    }

    @Scheduled(cron = "0 00 17 * * *")
    public void eventNotification() throws TelegramApiException {
        List<EventDTO> events = eventService.getAllEventsDTO();
        LocalDate today = LocalDate.now();
        long chatId = Long.parseLong(chatProperties.getChatId());
        for (EventDTO e : events) {
            LocalDate event = e.getDate().toLocalDate();
            int days = Period.between(today, event).getDays();
            switch (days) {
                case 7 -> showMessage(chatId, "Через неделю будет мероприятие " + getMessage(e), 43200000);
                case 3 -> showMessage(chatId, "Через три дня будет мероприятие " + getMessage(e), 43200000);
                case 1 -> showMessage(chatId, "Завтра будет мероприятие " + getMessage(e), 43200000);
            }
        }
    }

    private String getMessage(EventDTO e) {
        StringBuilder participants = new StringBuilder(e.getEventName() + "\nПойдут:\n");
        AtomicInteger number = new AtomicInteger(1);
        eventService.getEventById(String.valueOf(e.getId())).getParticipants().forEach(p -> participants
                .append(number.getAndIncrement())
                .append(". ").append(p.getName()).append(" (")
                .append(p.getNickName() == null ? "☠" : "@" + p.getNickName())
                .append(")")
                .append("\n"));
        return participants.toString().strip();
    }

    public boolean checkEvents(String messageText) {
        return eventService.getAllEventsDTO().stream().map(e -> String.valueOf(e.getId()))
                .toList().contains(messageText);
    }
}