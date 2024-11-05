package ru.sharanov.JavaEventTelgeramBot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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