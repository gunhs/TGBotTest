package ru.sharanov.SearchForMessagesBot.security;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "chat_users")
public class EventChatUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String password;
    private Long idTelegram;
}
