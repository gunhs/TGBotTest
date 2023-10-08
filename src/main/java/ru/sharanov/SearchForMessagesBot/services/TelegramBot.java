package ru.sharanov.SearchForMessagesBot.services;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sharanov.SearchForMessagesBot.Handler.ButtonHandler;
import ru.sharanov.SearchForMessagesBot.Handler.CommandHandler;
import ru.sharanov.SearchForMessagesBot.config.BotConfig;
import ru.sharanov.SearchForMessagesBot.config.ChatIds;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Guest;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.utils.DateTypeConverter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;
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
    private final ConfigurableEnvironment environment;
    private final String chatAdminId;
    private final ChatIds chatIds;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TelegramBot.class);

    public TelegramBot(BotConfig config, EventService eventService, ParticipantService participantService,
                       ConfigurableEnvironment environment, Environment env, @Value("${chatAdminId}") String chatAdminId, ChatIds chatIds) {
        this.config = config;
        this.eventService = eventService;
        this.participantService = participantService;
        this.environment = environment;
        this.chatIds = chatIds;
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
        System.out.println(environment.getProperty("chatAdminId"));
        execute(getMessage(chatId, "Главное меню", inlineKeyboardMarkup));
    }

    public void showFutureEventsButton(long chatId) throws TelegramApiException {
        List<EventDTO> events = eventService.getAllEventsDTO().stream()
                .filter(e -> (e.getDate().isAfter(LocalDateTime.now()))).collect(Collectors.toList());
        InlineKeyboardMarkup inlineKeyboardMarkup = ButtonHandler.showFutureEventButton(events);
        execute(getMessage(chatId, "Выберите меропиятие:", inlineKeyboardMarkup));
    }

    public void showPastEventsButton(long chatId) throws TelegramApiException {
        List<EventDTO> events = eventService.getAllEventsDTO().stream()
                .filter(e -> (!e.getDate().isAfter(LocalDateTime.now()))).collect(Collectors.toList());
        InlineKeyboardMarkup inlineKeyboardMarkup = ButtonHandler.showPastEventButton(events);
        execute(getMessage(chatId, "Выберите меропиятие:", inlineKeyboardMarkup));
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
            throws TelegramApiException, InterruptedException {
        if (eventService.getEventById(eventId).getParticipants()
                .stream().anyMatch(p -> p.getUserId() == userId)) {
            showMessage(chatId, "Вы уже добавлены");
            return;
        }
        String eventName = eventService.getEventById(eventId).getEventName();
        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setName(firstName);
        participantDTO.setNickName(nickName);
        participantDTO.setUserId(userId);
        participantService.addParticipant(participantDTO, eventId);
        showMessage(chatId, firstName + "  теперь участвует в мероприятии " + eventName);
        logger.info(firstName + " присоеденился к " + eventName);
    }

    public void removeParticipant(long chatId, String name, long idUser, String eventId)
            throws TelegramApiException, InterruptedException {
        Participant participant = participantService.getParticipantByUserId(idUser);
        if (!eventService.getEventById(eventId).getParticipants()
                .contains(participant)) {
            showMessage(chatId, "Вы не участвовали в мероприятии");
            return;
        }
        if (participant != null) {
            showMessage(chatId, name + "  больше не участвует в мероприятии");
            participantService.delParticipant(idUser, eventId);
        }
        logger.info(participant.getName() + " больше не участвует в " + eventService.getEventById(eventId).getEventName());
    }

    public void showMessage(long chatId, String textToSend) throws TelegramApiException, InterruptedException {
        Message sentOutMessage = execute(
                SendMessage.builder().chatId(String.valueOf(chatId)).text(textToSend).disableNotification(true).build());
        deleteMessage(sentOutMessage.getChatId(), sentOutMessage.getMessageId(), 10000);
    }

    public void deleteMessage(long chatId, int messageId, long time)
            throws InterruptedException, TelegramApiException {
        sleep(time);
        execute(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
    }

    public void participantJoinAll(String firstName, String userName, long chatId, long idUser)
            throws TelegramApiException, InterruptedException {
        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setName(firstName);
        participantDTO.setNickName(userName);
        participantDTO.setUserId(idUser);
        for (EventDTO e : eventService.getAllEventsDTO()) {
            if (eventService.getEventById(String.valueOf(e.getId())).getParticipants()
                    .stream().map(Participant::getUserId).toList().contains(idUser)) {
                showMessage(chatId, firstName + ", Вы уже добавлены на мероприятие " + e.getEventName());
                continue;
            }
            participantService.addParticipant(participantDTO, String.valueOf(e.getId()));
        }
        showMessage(chatId, firstName + "  теперь участвует во всех мероприятиях");
        logger.info(firstName + " присоеденился ко всем мероприятиям");
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

    public void showMap(long chatId, String eventId) throws TelegramApiException, InterruptedException {
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

    public void closeApp(long chatId) throws TelegramApiException, InterruptedException {
        Message sentOutMessage = execute(SendMessage.builder().chatId(chatId).text("Пока!").build());
        deleteMessage(chatId, sentOutMessage.getMessageId(), 0);
    }

    public void addGuest(long chatId, String eventId, long idUser, String firstName) throws TelegramApiException, InterruptedException {
        if (eventService.addGuest(Integer.parseInt(eventId), idUser)) {
            showMessage(chatId, firstName + " добавил гостя");
        } else {
            showMessage(chatId, "Слишком много гостей");
        }
    }

    public void removeGuest(long chatId, String eventId, long idUser, String firstName) throws TelegramApiException, InterruptedException {
        if (eventService.removeGuest(Integer.parseInt(eventId), idUser)) {
            showMessage(chatId, firstName + " удалил гостя");
        } else {
            showMessage(chatId, "Невозможно удалить гостя");
        }
    }

    public void checkAdmin(long chatIdMessage) {
        GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
        getChatAdministrators.setChatId(chatIdMessage);
        try {
            ArrayList<ChatMember> administrators = execute(getChatAdministrators);
            // Проверка, является ли бот администратором
            boolean isBotAdmin = false;
            for (ChatMember administrator : administrators) {
                User user = administrator.getUser();
                 if (user.getFirstName() .equals(getBotUsername())) {
                    isBotAdmin = true;
                    break;
                }
            }
            if (isBotAdmin){
                chatIds.writePropertyToFile("chatAdminId", chatAdminId);
//                environment.getPropertySources()
//                        .addLast(new MapPropertySource("myConfigSource",
//                                Collections.singletonMap("chatAdminId", chatAdminId)));
//                Properties properties = new Properties();
//                try {
//                    OutputStream output = new FileOutputStream("src/main/resources/application.properties");
//                    properties.setProperty("chatAdminId.name", "value1");
//                    properties.store(output, null);
//                } catch (IOException io) {
//                    io.printStackTrace();
//                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void testMessage(long chatId){

    }
}