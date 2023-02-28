package ru.sharanov.SearchForMessagesBot.Handler;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;

import java.util.ArrayList;
import java.util.List;

public class ButtonHandler {

    public static InlineKeyboardMarkup controlEventButton(String eventName) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        InlineKeyboardButton addMe = new InlineKeyboardButton();
        InlineKeyboardButton deleteMe = new InlineKeyboardButton();
        InlineKeyboardButton notChoose = new InlineKeyboardButton();
        InlineKeyboardButton yaVasya = new InlineKeyboardButton();
        addMe.setText("Добавить меня");
        addMe.setCallbackData("join event " + eventName);
        deleteMe.setText("Удалить меня");
        deleteMe.setCallbackData("left event " + eventName);
        notChoose.setText("Не определился");
        notChoose.setCallbackData("nothing event");
        yaVasya.setText("Я Вася");
        yaVasya.setCallbackData("vasya event " + eventName);
        keyboardButtonsRow1.add(addMe);
        keyboardButtonsRow1.add(deleteMe);
        keyboardButtonsRow2.add(notChoose);
        keyboardButtonsRow2.add(yaVasya);
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup showEventButton(List<EventDTO> events) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        events.forEach(e -> {
            String[] date = e.getDate().split("\\s+");
            InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText(date[0] + "\n" + e.getEventName());
            inlineKeyboardButton1.setCallbackData(e.getEventName());
            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            keyboardButtonsRow1.add(inlineKeyboardButton1);
            rows.add(keyboardButtonsRow1);
        });
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }
}
