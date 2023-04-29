package ru.sharanov.SearchForMessagesBot.model;

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
    private int eventID;
    @Column(name = "participiant_id", insertable = false, updatable = false)
    private Long participantID;

    public GuestKey(int eventID, Long participantID) {
        this.eventID = eventID;
        this.participantID = participantID;
    }
}
