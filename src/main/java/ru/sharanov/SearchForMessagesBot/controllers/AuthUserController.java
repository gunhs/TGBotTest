package ru.sharanov.SearchForMessagesBot.controllers;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.SearchForMessagesBot.dto.EventChatUserDTO;

import javax.validation.Valid;

@RestController
public class AuthUserController {
//    @GetMapping("/sign")
//    public ModelAndView newGuest(@ModelAttribute("user") EventChatUserDTO user) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("sign");
//        modelAndView.addObject("user", user);
//        return modelAndView;
//    }

    @PostMapping(value = "/sign")
    public ModelAndView checkUser(@ModelAttribute("user") @Valid EventChatUserDTO user, BindingResult bindingResult) {
        System.out.println("Бот прислал POST запрос");
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

    @GetMapping("/sign")
    public ModelAndView newGuestFromBot(@ModelAttribute("user") EventChatUserDTO user, @RequestParam("id") Long userId) {
        System.out.println("Бот прислал GET запрос");
        if (userId != null) {
            System.out.println(userId);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sign");
        modelAndView.addObject("user", user);
        return modelAndView;
    }


}
