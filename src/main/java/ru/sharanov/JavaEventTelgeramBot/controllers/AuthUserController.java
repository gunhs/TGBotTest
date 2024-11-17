package ru.sharanov.JavaEventTelgeramBot.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthUserController {

    private final AuthenticationManager authenticationManager;

    @GetMapping("/sign")
//    public ModelAndView newGuestFromBot(@ModelAttribute("user") EventChatUserDTO user) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("sign");
//        modelAndView.addObject("user", user);
    public ResponseEntity<String> newGuestFromBot(@RequestParam String username, @RequestParam String id) {
        System.out.println(username + " " + id);
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, id)
            );
            if (auth.isAuthenticated()) {
                return ResponseEntity.ok("Authentication successful");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Authentication failed");
        }
        return ResponseEntity.status(401).body("Authentication failed");
    }
}