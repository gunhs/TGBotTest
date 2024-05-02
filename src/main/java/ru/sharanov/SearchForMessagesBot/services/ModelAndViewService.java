package ru.sharanov.SearchForMessagesBot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.dto.EventDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelAndViewService {

    private final EventService eventService;

    public ModelAndView getView(String view) {
        return addObjectToModel(new ModelAndView(view), eventService.getAllEventsDTO());
    }

    public ModelAndView getModelAndView(String view, EventDTO event) {
        return addObjectToModel(new ModelAndView(view), List.of(event));
    }

    private ModelAndView addObjectToModel(ModelAndView modelAndView, List<EventDTO> events) {
        return events.size() == 1
                ? modelAndView.addObject("event", events.get(0))
                : modelAndView.addObject("events", events);
    }
}