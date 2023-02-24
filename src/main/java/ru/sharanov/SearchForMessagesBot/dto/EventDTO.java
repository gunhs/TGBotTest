package ru.sharanov.SearchForMessagesBot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class EventDTO {
    @NotEmpty(message = "Field can't be empty")
    private String eventName;
    @NotEmpty(message = "Field can't be empty")
    private String address;
    @Pattern(regexp = "^([0-2][0-9]|[3][0-1])\\.([0][1-9]|[1][0-2])\\.\\d{4}" +
            "\\s([0-1][0-9]|[2][0-4]):[0-5][0-9]$",
            message = "Wrong format.dd.mm.yyyy hh:mm")
    private String date;
    private int id;
}
