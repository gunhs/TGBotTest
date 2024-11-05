package ru.sharanov.JavaEventTelgeramBot.services.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sharanov.JavaEventTelgeramBot.dto.EventDTO;
import ru.sharanov.JavaEventTelgeramBot.model.Event;

@Mapper
public interface EventsMapper {
    @Mapping(ignore = true, target = "participants")
    Event toEntity(EventDTO eventDTO);

    @Mapping(target = "participantDTOList", ignore = true)
    EventDTO toDto(Event event);
}