package ru.sharanov.JavaEventTelgeramBot.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.sharanov.JavaEventTelgeramBot.services.ModelAndViewService;

@RestController
@RequiredArgsConstructor
public class AuthUserController {

    private final AuthenticationManager authenticationManager;
    private final ModelAndViewService modelAndViewService;

    @GetMapping("/sign")
//    public ModelAndView newGuestFromBot(@ModelAttribute("user") EventChatUserDTO user) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("sign");
//        modelAndView.addObject("user", user);
    public ModelAndView newGuestFromBot(@RequestParam String username, @RequestParam String id) {
        try {
            Authentication auth =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, id));
            if (auth.isAuthenticated()) {
                return modelAndViewService.getView("redirect:/events");
//                return ResponseEntity.ok("Authentication successful");
            }
        } catch (AuthenticationException e) {
            return null;
        }
        return null;
    }
}