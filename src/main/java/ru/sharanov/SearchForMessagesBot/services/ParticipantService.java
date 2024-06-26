package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;
import ru.sharanov.SearchForMessagesBot.utils.ConvertMonth;
import ru.sharanov.SearchForMessagesBot.utils.DateTypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final String chatAdminId;

    public ParticipantService(ParticipantRepository participantRepository, EventService eventService,
                              EventRepository eventRepository, @Value("${chatAdminId}") String chatAdminId) {
        this.participantRepository = participantRepository;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.chatAdminId = chatAdminId;
    }

    public void addParticipant(String eventId, long chatId, String firstName,
                               String nickName, long userId) {
        boolean chatMember = chatId == Long.parseLong(chatAdminId);
        ParticipantDTO participantDTO = createParticipantDTO(firstName, nickName, userId, chatMember);
        Participant participant = getParticipantByUserId(userId);
        if (participant == null) {
            participant = new Participant();
            participant.setName(participantDTO.getName());
            participant.setNickName(participantDTO.getNickName());
            participant.setUserId(participantDTO.getUserId());
            participant.setChatMember(chatMember);
            participantRepository.save(participant);
        } else if (chatMember && !participant.getChatMember()) {
            participant.setChatMember(true);
            participantRepository.save(participant);
        }
        eventService.addParticipantInEvent(participant, eventId);
    }

    private ParticipantDTO createParticipantDTO(String firstName,
                                                String nickName, long userId, boolean chatMember) {
        return ParticipantDTO.builder()
                .name(firstName)
                .nickName(nickName)
                .userId(userId)
                .chatMember(chatMember)
                .build();
    }

    public void delParticipant(long idUser, String eventId) {
        Participant participant = eventService.getEventById(eventId).getParticipants()
                .stream().filter(p -> p.getUserId() == idUser).findAny().orElse(null);
        Event event = eventService.getEventById(eventId);
        event.removeParticipant(participant);
        eventRepository.save(event);
    }

    public Participant getParticipantByUserId(long userId) {
        return participantRepository.findAll().stream()
                .filter(p -> p.getUserId() == userId).findFirst().orElse(null);
    }

    public boolean addBirthdayInDB(String text, Participant participant) {
        text = text.replaceAll("мой день рождения", "").strip();
        text = text.replaceAll("\\s+", ".");
        if (!text.matches("\\d{1,2}\\.[А-яa-zA-Z0-9]{1,8}(\\.\\d{4})?")) {
            return false;
        }
        String[] components = text.split("\\.");
        int dayDigital = Integer.parseInt(components[0]);
        int monthDigital;
        int yearDigital;
        if (!(components.length == 2 || components.length == 3)) {
            return false;
        }
        yearDigital = components.length != 3 ? 1000 : Integer.parseInt(components[2]);
        if (yearDigital > LocalDate.now().getYear() ||
                ((yearDigital < LocalDate.now().getYear() - 120) && (yearDigital != 1000))) {
            return false;
        }
        String month = components[1];
        monthDigital = month.matches("[А-яa-zA-Z]+") ? ConvertMonth.convertMonthWordInDigital(month) :
                Integer.parseInt(month);
        LocalDate birthday = LocalDate.of(yearDigital,
                monthDigital, dayDigital);
        String birthdayString = DateTypeConverter.localDateToStringConverterForDB(birthday);
        participant.setBirthday(birthdayString);
        System.out.println(birthday);
        participantRepository.save(participant);
        return true;
    }

    public List<ParticipantDTO> getAllParticipants() {
        List<ParticipantDTO> result = new ArrayList<>();
        participantRepository.findAll().forEach(p -> {
            ParticipantDTO participantDTO = createParticipantDTO(p.getName(),
                    p.getNickName(), p.getUserId(), p.getChatMember());
            participantDTO.setId(p.getId());
            if (p.getBirthday() != null) {
                LocalDate birth = DateTypeConverter.stringToLocalDateConverterForDB(p.getBirthday());
                participantDTO.setBirthday(birth);
                result.add(participantDTO);
            }
        });
        return result;
    }

    public String getNamesakes() {
        List<String> participants = new ArrayList<>();
        List<Participant> users = participantRepository.findAll();
        users.stream().filter(Participant::getChatMember)
                .filter(p -> p.getBirthday() != null)
                .filter(p -> DateTypeConverter.stringToLocalDateConverterForDB(p.getBirthday()).getDayOfYear()
                        == LocalDateTime.now().getDayOfYear())
                .forEach(p -> participants.add(p.getName()));
        return !participants.isEmpty() ? String.join(",", participants) : "";
    }
}