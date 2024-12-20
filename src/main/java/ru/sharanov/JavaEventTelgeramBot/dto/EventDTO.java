package ru.sharanov.JavaEventTelgeramBot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class EventDTO {

    private static final String NOT_EMPTY_FIELD_MESSAGE = "Field can't be empty";
    private static final String CHOOSE_DATE_MESSAGE = "Choose date";

    @NotEmpty(message = NOT_EMPTY_FIELD_MESSAGE)
    private String eventName;

    @NotEmpty(message = NOT_EMPTY_FIELD_MESSAGE)
    private String address;

    @NotNull(message = CHOOSE_DATE_MESSAGE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;

    private boolean done;

    private String url;

    private int id;

    private List<ParticipantDTO> participantDTOList = new ArrayList<>();

    @NotNull(message = NOT_EMPTY_FIELD_MESSAGE)
    private Float latitude;

    @NotNull(message = NOT_EMPTY_FIELD_MESSAGE)
    private Float longitude;

}