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
    public ModelAndView newEvent(@ModelAttribute("user") EventChatUserDTO user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sign");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping(value = "/sign")
    public ModelAndView checkUser(@ModelAttribute("user") @Valid EventChatUserDTO user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("sign");
            modelAndView.addObject("user", user);
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("redirect:/events");
            modelAndView.addObject("user", user);
            return modelAndView;
        }
    }
}
