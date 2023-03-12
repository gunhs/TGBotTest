package ru.sharanov.SearchForMessagesBot.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTypeConverter {
    public static String localDateTimeToStringConverter(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(dateTimeFormat);
    }
}
