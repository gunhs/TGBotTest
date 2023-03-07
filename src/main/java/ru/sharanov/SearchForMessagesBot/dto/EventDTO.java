package ru.sharanov.SearchForMessagesBot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;
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
    private Date date;
    private boolean done;
    private String url;
    private int id;
    private List<ParticipantDTO> participantDTOList = new ArrayList<>();
}