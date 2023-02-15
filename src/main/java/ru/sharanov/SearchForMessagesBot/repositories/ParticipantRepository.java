package ru.sharanov.SearchForMessagesBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sharanov.SearchForMessagesBot.entities.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
}
