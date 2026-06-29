package br.edu.atitus.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class GreetingController {

    private final String message;

    public GreetingController(@Value("${greeting.message:Bem-vindo ao JLPG Motors}") String message) {
        this.message = message;
    }

    @GetMapping("/greeting")
    public Map<String, String> greeting() {
        return Map.of("message", message);
    }
}
