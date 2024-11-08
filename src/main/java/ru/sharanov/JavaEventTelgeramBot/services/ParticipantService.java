package ru.sharanov.JavaEventTelgeramBot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sharanov.JavaEventTelgeramBot.config.ChatProperties;
import ru.sharanov.JavaEventTelgeramBot.dto.ParticipantBirthdaysDto;
import ru.sharanov.JavaEventTelgeramBot.model.Participant;
import ru.sharanov.JavaEventTelgeramBot.repositories.ParticipantRepository;
import ru.sharanov.JavaEventTelgeramBot.utils.ConvertMonth;
import ru.sharanov.JavaEventTelgeramBot.utils.DateTypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final static int MAX_AGE = 120;
    private final static int DEFAULT_AGE = 1000;

    private final ParticipantRepository participantRepository;
    private final EventService eventService;
    private final ChatProperties chatProperties;

    public void addParticipant(String eventId, long chatId, String firstName,
                               String nickName, long userId) {
        boolean fromCurrentChat = chatId == Long.parseLong(chatProperties.getChatId());
        Participant participant = participantRepository.findParticipantsByUserId(userId);
        if (participant == null) {
            participant = createParticipantIfNotExist(firstName, nickName, userId, fromCurrentChat);
        } else if (fromCurrentChat && !participant.getChatMember()) {
            participant.setChatMember(true);
            participantRepository.save(participant);
        }
        eventService.addParticipantInEvent(eventId, participant);
    }

    private Participant createParticipantIfNotExist(String firstName, String nickName, long userId, boolean chatMember) {
        Participant participant = new Participant();
        participant.setName(firstName);
        participant.setNickName(nickName);
        participant.setUserId(userId);
        participant.setChatMember(chatMember);
        return participantRepository.save(participant);
    }

    public Participant getParticipantByUserId(long userId) {
        return participantRepository.findParticipantsByUserId(userId);
    }

    public Long getParticipantIdByUserId(long userId) {
        return participantRepository.findByUserId(userId).getId();
    }

    public boolean checkParticipantIsMember(long userId) {
        return participantRepository.existsByUserIdAndChatMemberTrue(userId);
    }

    public boolean addBirthdayInDB(String text, Participant participant) {
        text = text.replaceAll("мой день рождения", "").strip();
        text = text.replaceAll("\\s+", ".");
        if (!text.matches("\\d{1,2}\\.[А-яa-zA-Z0-9]{1,8}(\\.\\d{4})?")) {
            return false;
        }
        String[] components = text.split("\\.");
        if (!(components.length == 2 || components.length == 3)) {
            return false;
        }
        int dayDigital = Integer.parseInt(components[0]);
        int yearDigital = components.length != 3 ? DEFAULT_AGE : Integer.parseInt(components[2]);
        if (yearDigital > LocalDate.now().getYear() ||
                ((yearDigital < LocalDate.now().getYear() - MAX_AGE) && (yearDigital != DEFAULT_AGE))) {
            return false;
        }
        String month = components[1];
        int monthDigital = month.matches("[А-яa-zA-Z]+") ? ConvertMonth.convertMonthWordInDigital(month) :
                Integer.parseInt(month);
        LocalDate birthday = LocalDate.of(yearDigital, monthDigital, dayDigital);
        String birthdayString = DateTypeConverter.localDateToStringConverterForDB(birthday);
        participant.setBirthday(birthdayString);
        participantRepository.save(participant);
        return true;
    }

    public List<ParticipantBirthdaysDto> getAllParticipantsBirthdays() {
        return participantRepository.findByBirthdayNotNull();
    }

    public String getNamesakes() {
        List<String> participants = participantRepository.findByBirthdayNotNull()
                .stream().filter(ParticipantBirthdaysDto::getChatMember)
                .filter(p -> DateTypeConverter.stringToLocalDateConverterForDB(p.getBirthday()).getMonthValue()
                        == LocalDateTime.now().getMonthValue() &&
                        DateTypeConverter.stringToLocalDateConverterForDB(p.getBirthday()).getDayOfMonth()
                                == LocalDateTime.now().getDayOfMonth())
                .map(ParticipantBirthdaysDto::getName).collect(Collectors.toList());
        return !participants.isEmpty() ? String.join(",", participants) : "";
    }
}