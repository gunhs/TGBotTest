package ru.sharanov.SearchForMessagesBot.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
public class Event {
    @Id
    @GenericGenerator(name = "generator", strategy = "increment")
    @GeneratedValue(generator = "generator")//аннотация генерации id
    private Integer id;

    @Column(name = "event_name")
    private String eventName;
    @Column()
    private String adress;
    @Column()
    private LocalDate date;



}
