package ru.sharanov.SearchForMessagesBot.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ofPattern;

public class DateTypeConverter {
    public static String localDateTimeToStringConverter(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFormat = ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(dateTimeFormat);
    }

    public static String localDateToStringConverter(LocalDate dateTime) {
        DateTimeFormatter dateTimeFormat = dateTime.getYear() == 1900 ?
                ofPattern("dd MMMM", new Locale("ru")) :
                ofPattern("dd MMMM yyyy", new Locale("ru"));
        return dateTime.format(dateTimeFormat);
    }
}
