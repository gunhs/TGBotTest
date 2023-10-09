package ru.sharanov.SearchForMessagesBot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ParticipantDTO {
    private Integer id;
    private String name;
    private String nickName;
    private long userId;
    private long chatId;
    private List<EventDTO> eventDTOList = new ArrayList<>();
}