package ru.sharanov.SearchForMessagesBot.services;

import org.springframework.stereotype.Service;
import ru.sharanov.SearchForMessagesBot.dto.ParticipantDTO;
import ru.sharanov.SearchForMessagesBot.model.Event;
import ru.sharanov.SearchForMessagesBot.model.Participant;
import ru.sharanov.SearchForMessagesBot.repositories.EventRepository;
import ru.sharanov.SearchForMessagesBot.repositories.ParticipantRepository;
import ru.sharanov.SearchForMessagesBot.utils.ConvertMonth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final EventService eventService;
    private final EventRepository eventRepository;

    public ParticipantService(ParticipantRepository participantRepository, EventService eventService, EventRepository eventRepository) {
        this.participantRepository = participantRepository;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

    public void addParticipant(ParticipantDTO participantDTO, String eventId, Long chatId) {
        Participant participant = new Participant();
        if (checkUserId(participantDTO.getUserId())) {
            participant = getParticipantByUserId(participantDTO.getUserId());
        } else {
            participant.setName(participantDTO.getName());
            participant.setNickName(participantDTO.getNickName());
            participant.setUserId(participantDTO.getUserId());
            participant.setChatId(chatId);
            participantRepository.save(participant);
        }
        eventService.addParticipantInEvent(participant, eventId);
    }

    public void delParticipant(long idUser, String eventId) {
        Participant participant = eventService.getEventById(eventId).getParticipants()
                .stream().filter(p -> p.getUserId() == idUser).findAny().orElse(null);
        Event event = eventService.getEventById(eventId);
        event.removeParticipant(participant);
        eventRepository.save(event);
    }

    public boolean checkUserId(long userId) {
        return participantRepository.findAll().stream().anyMatch(p -> p.getUserId() == userId);
    }

    public Participant getParticipantByUserId(long userId) {
        return participantRepository.findAll().stream()
                .filter(p -> p.getUserId() == userId).findFirst().orElse(null);
    }

    public void addBirthdayInDB(String text, Participant participant) {
        text = text.replaceAll("мой день рождения", "").strip();
        text = text.replaceAll("\\s+", ".");
        if (!text.matches("\\d{1,2}\\.[А-яa-zA-Z0-9]{1,8}(\\.\\d{2,4})?")) {
            return;
        }
        String[] components = text.split("\\.");
        int dayDigital = Integer.parseInt(components[0]);
        int monthDigital;
        int yearDigital;
        if (!(components.length == 2 || components.length == 3)) {
            return;
        }
        yearDigital = components.length != 3 ? 1900 : Integer.parseInt(components[2]);
        String month = components[1];
        monthDigital = month.matches("[А-яa-zA-Z]+") ? ConvertMonth.convertMonthWordInDigital(month) :
                Integer.parseInt(month);
        LocalDate birthday = LocalDate.of(yearDigital,
                monthDigital, dayDigital);
        participant.setBirthday(birthday);
        participantRepository.save(participant);
    }

    public List<ParticipantDTO> getAllParticipants(String chatId) {
        List<ParticipantDTO> result = new ArrayList<>();
        participantRepository.findAll().forEach(p -> {
            ParticipantDTO participantDTO = new ParticipantDTO();
            participantDTO.setName(p.getName());
            participantDTO.setNickName(p.getNickName());
            participantDTO.setId(p.getId());
            participantDTO.setBirthday(p.getBirthday());
            participantDTO.setUserId(p.getUserId());
            if (p.getChatId() == null) {
                participantDTO.setChatId(Long.parseLong(chatId));
            } else {
                participantDTO.setChatId(p.getChatId());
            }
            if (p.getBirthday() != null) {
                result.add(participantDTO);
            }
        });
        return result;
    }

    public String getNamesakes(long chatId) {
        List<String> participants = new ArrayList<>();
        participantRepository.findAll().stream()
                .filter(p -> p.getChatId() == chatId)
                .filter(p -> p.getBirthday() != null)
                .filter(p -> p.getBirthday().getDayOfYear() == LocalDateTime.now().getDayOfYear())
                .forEach(p -> participants.add(p.getName()));
        return !participants.isEmpty() ? String.join(",", participants) : "";
    }
}