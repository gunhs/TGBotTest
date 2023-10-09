package ru.sharanov.SearchForMessagesBot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Event {
    @Id
    @GenericGenerator(name = "generator", strategy = "increment")
    @GeneratedValue(generator = "generator")
    private Integer id;
    @NotEmpty(message = "Field can't be empty")
    private String eventName;
    @NotEmpty(message = "Field can't be empty")
    private String address;
    @NotNull(message = "Choose date")
    private LocalDateTime date;
    private boolean done;
    private String url;
    private Float latitude;
    private Float longitude;
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    },
            fetch = FetchType.EAGER
    )
    @JoinTable(name = "event_name",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    @ToString.Exclude
    private List<Participant> participants;

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
        participant.getEvents().add(this);
    }

    public void removeParticipant(Participant participant) {
        this.participants.remove(participant);
        participant.getEvents().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
