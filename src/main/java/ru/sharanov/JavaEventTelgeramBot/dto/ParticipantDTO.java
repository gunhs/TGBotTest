package ru.sharanov.JavaEventTelgeramBot.dto;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ParticipantDTO implements Comparable<ParticipantDTO> {
    private Integer id;
    private String name;
    private String nickName;
    private long userId;
    private boolean chatMember;
    private LocalDate birthday;
    private List<EventDTO> eventDTOList;

    @Override
    public int compareTo(@NotNull ParticipantDTO o) {
        if (this.getBirthday().getMonthValue() > o.getBirthday().getMonthValue()) {
            return 1;
        } else if (this.getBirthday().getMonthValue() < o.getBirthday().getMonthValue()) {
            return -1;
        } else {
            return Integer.compare(this.getBirthday().getDayOfMonth(), o.getBirthday().getDayOfMonth());
        }
    }
}