package ru.sharanov.SearchForMessagesBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sharanov.SearchForMessagesBot.model.Participant;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
}
