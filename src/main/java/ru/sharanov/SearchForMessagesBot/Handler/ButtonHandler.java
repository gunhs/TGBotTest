package ru.sharanov.SearchForMessagesBot.Handler;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ButtonHandler {

    public static InlineKeyboardMarkup controlEventButton(String eventId) {
        List<List<InlineKeyboardButton>> rows = List.of(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        rows.get(0).add(addButton("\uD83D\uDC83Я пойду\uD83D\uDD7A", "join event " + eventId));
        rows.get(0).add(addButton("Я не пойду\uD83D\uDEB7", "left event " + eventId));
        rows.get(1).add(addButton("К событиям ↩️", "back"));
        rows.get(1).add(addButton("Cледующее ⏩", "future event " + eventId));
        rows.get(2).add(addButton("Показать на карте \uD83D\uDDFA", "show map " + eventId));
        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup showFutureEventButton(List<EventDTO> events) {
        List<List<InlineKeyboardButton>> rows = addEventsRows(events);
        rows.add(List.of(addButton("Записаться на всё", "join all")));
        rows.add(List.of(addButton("Назад ↩", "menu button")));
        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup showPastEventButton(List<EventDTO> events) {
        List<List<InlineKeyboardButton>> rows = addEventsRows(events);
        rows.add(List.of(addButton("Назад ↩", "menu button")));
        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup backPastEventButton() {
        List<List<InlineKeyboardButton>> rows = List.of(new ArrayList<>());
        rows.get(0).add(addButton("Назад ↩", "past menu"));
        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup closeMap(String eventId) {
        List<List<InlineKeyboardButton>> rows = List.of(new ArrayList<>());
        rows.get(0).add(addButton("Закрыть карту", "close map"));
        rows.get(0).add(addButton("Назад ↩", "past menu " + eventId));
        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup showMenuButton() {
        List<List<InlineKeyboardButton>> rows = List.of(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        rows.get(0).add(addButton("Ближайшие мероприятия", "future events"));
        rows.get(1).add(addButton("Прошедшие мероприятия", "past events"));
        rows.get(2).add(addButton("Выйти", "quit button"));
        return new InlineKeyboardMarkup(rows);
    }

    private static ArrayList<List<InlineKeyboardButton>> addEventsRows(List<EventDTO> events) {
        ArrayList<List<InlineKeyboardButton>> rows = new ArrayList<>();
        events.forEach(e -> {
            String date = e.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            keyboardButtonsRow1.add(addButton(date + "\n" + e.getEventName(), String.valueOf(e.getId())));
            rows.add(keyboardButtonsRow1);
        });
        return rows;
    }

    private static InlineKeyboardButton addButton(String text, String callBackData) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(text);
        inlineKeyboardButton.setCallbackData(callBackData);
        return inlineKeyboardButton;
    }
}