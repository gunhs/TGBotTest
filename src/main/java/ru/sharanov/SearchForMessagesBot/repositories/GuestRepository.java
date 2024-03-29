package ru.sharanov.SearchForMessagesBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sharanov.SearchForMessagesBot.model.Guest;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Integer> {
}
