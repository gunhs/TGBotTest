package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.dto.EventChatUserDTO;

@RestController
public class AuthUserController {

    @GetMapping("/sign")
    public ModelAndView newGuestFromBot(@ModelAttribute("user") EventChatUserDTO user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sign");
        modelAndView.addObject("user", user);
        return modelAndView;
    }
}
