package ru.sharanov.JavaEventTelgeramBot.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@Table(name = "guests")
@NoArgsConstructor
public class Guest {
    @EmbeddedId
    public GuestKey id;
    private Integer count;
}