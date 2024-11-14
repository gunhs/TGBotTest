package ru.sharanov.JavaEventTelgeramBot.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sharanov.JavaEventTelgeramBot.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    @Transactional
    @Modifying
    @Query("update Event e set e.done = true where e.date < now()")
    void updateDoneByDate();

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"participants"})
    List<Event> findAllByOrderByDateAsc();

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"participants"})
    @Query("select e from Event e where e.date < now() order by e.date")
    List<Event> findByDateBefore();

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"participants"})
    @Query("select e from Event e where e.date > now() order by e.date")
    List<Event> findByDateAfter();

    @Override
    @NotNull
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"participants"})
    Optional<Event> findById(@NotNull Integer integer);

    @Modifying
    @Query(value = "delete from event_participant where event_id=?1 and participant_id =?2", nativeQuery = true)
    int deleteParticipantFromEvent(Long eventId, Long userId);

    @Query(value = "select exists(select 1 from event_participant e " +
            "where event_id=?1 and participant_id =?2)", nativeQuery = true)
    boolean checkParticipantInEvent(Long eventId, Long userId);

    @Modifying
    @Transactional
    @Query(value = "insert into event_participant (event_id, participant_id) values(?1, ?2) ", nativeQuery = true)
    void addParticipantInEvent(Long eventId, Long userId);
}
