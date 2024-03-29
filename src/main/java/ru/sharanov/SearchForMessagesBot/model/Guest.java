package ru.sharanov.SearchForMessagesBot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Guest {
    @EmbeddedId
    public GuestKey id;
    private Integer count;
}