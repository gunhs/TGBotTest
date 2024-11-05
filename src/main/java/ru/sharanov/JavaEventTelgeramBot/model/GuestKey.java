package ru.sharanov.JavaEventTelgeramBot.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@ToString
@Getter
@Setter
@NoArgsConstructor
public class GuestKey implements Serializable {
    static final long serialVersionUID = 1L;
    @Column(name = "event_id", insertable = false, updatable = false)
    private Long eventID;
    @Column(name = "participant_id", insertable = false, updatable = false)
    private Long participantID;

    public GuestKey(Long eventID, Long participantID) {
        this.eventID = eventID;
        this.participantID = participantID;
    }
}