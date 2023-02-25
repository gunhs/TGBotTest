package ru.sharanov.SearchForMessagesBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sharanov.SearchForMessagesBot.model.Event;

import javax.validation.constraints.NotEmpty;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    Event findEventByEventName(@NotEmpty(message = "Field can't be empty") String eventName);
}
