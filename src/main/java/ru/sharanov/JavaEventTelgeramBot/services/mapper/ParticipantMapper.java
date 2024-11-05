package ru.sharanov.JavaEventTelgeramBot.services.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sharanov.JavaEventTelgeramBot.dto.ParticipantDTO;
import ru.sharanov.JavaEventTelgeramBot.model.Participant;

@Mapper
public interface ParticipantMapper {

    @Mapping(target = "eventDTOList", ignore = true)
    @Mapping(source = "birthday", target = "birthday", dateFormat = "dd.MM.yyyy")
    ParticipantDTO toDto(Participant participant);

}