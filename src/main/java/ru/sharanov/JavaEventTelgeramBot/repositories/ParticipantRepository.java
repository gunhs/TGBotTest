package ru.sharanov.JavaEventTelgeramBot.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sharanov.JavaEventTelgeramBot.dto.ParticipantBirthdaysDto;
import ru.sharanov.JavaEventTelgeramBot.dto.ParticipantId;
import ru.sharanov.JavaEventTelgeramBot.model.Participant;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"events"})
    Participant findParticipantsByUserId(long id);

    Optional<Participant> findByNickName(String nickName);

    List<ParticipantBirthdaysDto> findByBirthdayNotNullAndChatMemberTrue();

    boolean existsByUserIdAndChatMemberTrue(Long userId);

    ParticipantId findByUserId(Long userId);
}
