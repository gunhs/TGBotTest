package ru.sharanov.SearchForMessagesBot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sharanov.SearchForMessagesBot.security.EventChatUser;

@Repository
public interface EventChatRepository extends JpaRepository<EventChatUser, Integer> {

    EventChatUser getUserByName(String username);
    EventChatUser getUserByIdTelegram(Long userId);

}
