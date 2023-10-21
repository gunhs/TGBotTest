package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.dto.EventChatUserDTO;

import javax.validation.Valid;

@RestController
public class AuthUserController {
    @GetMapping("/sign")
    public ModelAndView getAuth() {
        return new ModelAndView("sign");
    }

    @PostMapping(value = "/sign")
    public ModelAndView checkUser(@ModelAttribute("user") @Valid EventChatUserDTO user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("sign");
        } else {
            return new ModelAndView("redirect:/events");
        }
    }
}
