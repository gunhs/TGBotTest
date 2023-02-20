package ru.sharanov.SearchForMessagesBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sharanov.SearchForMessagesBot.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
}
