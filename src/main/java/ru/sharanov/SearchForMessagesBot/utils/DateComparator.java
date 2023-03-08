package ru.sharanov.SearchForMessagesBot.utils;

import ru.sharanov.SearchForMessagesBot.dto.EventDTO;

import java.util.Comparator;

public class DateComparator implements Comparator<EventDTO> {

    @Override
    public int compare(EventDTO o1, EventDTO o2) {
        if (o1.getDate().isAfter(o2.getDate())) {
            return 1;
        } else if (o1.getDate().equals(o2.getDate())) {
            return 0;
        } else return -1;
    }
}
