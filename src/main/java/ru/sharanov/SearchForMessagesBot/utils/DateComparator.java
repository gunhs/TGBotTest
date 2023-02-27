package ru.sharanov.SearchForMessagesBot.utils;

import ru.sharanov.SearchForMessagesBot.dto.EventDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;

import java.util.Comparator;

public class DateComparator implements Comparator<EventDTO> {


    @Override
    public int compare(EventDTO o1, EventDTO o2) {
        if (DateTypeConverter.stringToLocalDateTimeConverter(o1.getDate())
                .isAfter(DateTypeConverter.stringToLocalDateTimeConverter(o2.getDate()))) {
            return 1;
        } else if (DateTypeConverter.stringToLocalDateTimeConverter(o1.getDate())
                .equals(DateTypeConverter.stringToLocalDateTimeConverter(o2.getDate()))) {
            return 0;
        } else return -1;
    }
}
