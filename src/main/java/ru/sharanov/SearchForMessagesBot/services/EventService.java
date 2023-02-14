package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;

@Service
public class EventService {
    private final EventRepository eventRepository;
}
