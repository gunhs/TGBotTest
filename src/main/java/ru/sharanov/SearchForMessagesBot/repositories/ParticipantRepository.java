package ru.sharanov.SearchForMessagesBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sharanov.SearchForMessagesBot.model.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
}
