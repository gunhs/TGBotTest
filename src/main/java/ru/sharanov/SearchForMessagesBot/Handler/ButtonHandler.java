package ru.sharanov.SearchForMessagesBot.Handler;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;

import java.util.ArrayList;
import java.util.List;

public class ButtonHandler {

    public static InlineKeyboardMarkup controlEventButton(String eventId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        InlineKeyboardButton addMe = new InlineKeyboardButton();
        InlineKeyboardButton deleteMe = new InlineKeyboardButton();
        InlineKeyboardButton notChoose = new InlineKeyboardButton();
        InlineKeyboardButton nextEvent = new InlineKeyboardButton();
        addMe.setText("\uD83D\uDC83Я пойду\uD83D\uDD7A");
        addMe.setCallbackData("join event " + eventId);
        deleteMe.setText("Я не пойду\uD83D\uDEB7");
        deleteMe.setCallbackData("left event " + eventId);
        notChoose.setText("К событиям ↩️");
        notChoose.setCallbackData("back");
        nextEvent.setText("Cледующее ⏩");
        nextEvent.setCallbackData("future event " + eventId);
        keyboardButtonsRow1.add(addMe);
        keyboardButtonsRow1.add(deleteMe);
        keyboardButtonsRow2.add(notChoose);
        keyboardButtonsRow2.add(nextEvent);
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup showFutureEventButton(List<EventDTO> events) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = addEventsRows(events);
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Записаться на всё");
        inlineKeyboardButton1.setCallbackData("join all");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Назад ↩");
        inlineKeyboardButton.setCallbackData("menu button");
        keyboardButtonsRow.add(inlineKeyboardButton);
        rows.add(keyboardButtonsRow1);
        rows.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup showPastEventButton(List<EventDTO> events) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = addEventsRows(events);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Назад ↩");
        inlineKeyboardButton.setCallbackData("menu button");
        keyboardButtonsRow.add(inlineKeyboardButton);
        rows.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup backPastEventButton() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Назад ↩");
        inlineKeyboardButton.setCallbackData("past menu");
        keyboardButtonsRow.add(inlineKeyboardButton);
        rows.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    public static InlineKeyboardMarkup showMenuButton() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Ближайшие мероприятия");
        inlineKeyboardButton1.setCallbackData("future events");
        inlineKeyboardButton2.setText("Прошедшие мероприятия");
        inlineKeyboardButton2.setCallbackData("past events");
        inlineKeyboardButton3.setText("Выйти");
        inlineKeyboardButton3.setCallbackData("quit button");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        keyboardButtonsRow3.add(inlineKeyboardButton3);
        rows.add(keyboardButtonsRow1);
        rows.add(keyboardButtonsRow2);
        rows.add(keyboardButtonsRow3);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    private static ArrayList<List<InlineKeyboardButton>> addEventsRows(List<EventDTO> events) {
        ArrayList<List<InlineKeyboardButton>> rows = new ArrayList<>();
        events.forEach(e -> {
            String[] date = e.getDate().split("\\s+");
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(date[0] + "\n" + e.getEventName());
            inlineKeyboardButton1.setCallbackData(String.valueOf(e.getId()));
            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            keyboardButtonsRow1.add(inlineKeyboardButton1);
            rows.add(keyboardButtonsRow1);
        });
        return rows;
    }

}
