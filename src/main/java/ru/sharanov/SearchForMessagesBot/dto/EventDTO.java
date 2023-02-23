package ru.sharanov.SearchForMessagesBot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class EventDTO {
    private String eventName;
    private String address;
    //    @Pattern(regexp = "^\\d{4}-([0][1-9]|[1][0-2])-([0-2][0-9]|[3][0-1])" +
//            "\\s([0-1][0-9]|[2][0-4]):[0-5][0-9]:[0-5][0-9]$",
//            message = "Wrong format. hhhh-mm-dd hh:mm:ss")
    @Pattern(regexp = "^([0-2][0-9]|[3][0-1])\\.([0][1-9]|[1][0-2])\\.\\d{4}" +
            "\\s([0-1][0-9]|[2][0-4]):[0-5][0-9]:[0-5][0-9]$",
            message = "Wrong format. hhhh-mm-dd hh:mm:ss")
    private String date;
}
