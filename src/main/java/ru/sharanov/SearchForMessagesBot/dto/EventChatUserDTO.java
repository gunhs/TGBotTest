package ru.sharanov.SearchForMessagesBot.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class EventChatUserDTO {
    @NotEmpty(message = "Field can't be empty")
    private String name;
    @NotEmpty(message = "Field can't be empty")
    private String password;
}
