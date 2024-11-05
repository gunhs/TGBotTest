package ru.sharanov.JavaEventTelgeramBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sharanov.JavaEventTelgeramBot.model.Guest;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findById_EventIDAndId_ParticipantID(Long eventID, Long participantID);

    List<Guest> findById_EventID(Long eventID);
}
