package ru.sharanov.SearchForMessagesBot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

@Data
@NoArgsConstructor
public class EventDTO {
    @NotEmpty(message = "Field can't be empty")
    private String eventName;
    @NotEmpty(message = "Field can't be empty")
    private String address;
    @NotNull(message = "Choose date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;
    private boolean done;
    private String url;
    private int id;
    private List<ParticipantDTO> participantDTOList = new ArrayList<>();
    @NotNull(message = "Field can't be empty")
    private float latitude;
    @NotNull(message = "Field can't be empty")
    private float longitude;
}