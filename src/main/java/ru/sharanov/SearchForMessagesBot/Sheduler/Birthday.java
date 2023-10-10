package ru.sharanov.SearchForMessagesBot.Sheduler;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class Birthday {

    private final ParticipantRepository participantRepository;

    public Birthday(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public String checkBirthday() {
        LocalDateTime today = LocalDateTime.now();
        List<String> participants = new ArrayList<>();
        if (today.getHour() == 0 && today.getMinute() == 1) {
            participantRepository.findAll().stream()
                    .filter(p -> p.getBirthday().getDayOfYear() == today.getDayOfYear())
                    .forEach(p -> participants.add(p.getName()));
        }
        if (!participants.isEmpty()) {
            return String.join(",", participants);
        } else {
            return "";
        }
    }
}
