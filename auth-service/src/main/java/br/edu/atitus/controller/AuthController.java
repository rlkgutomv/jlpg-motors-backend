package br.edu.atitus.controller;

import br.edu.atitus.model.UserEntity;
import br.edu.atitus.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping({"/signup", "/register"})
    public ResponseEntity<?> register(@RequestBody UserEntity user) {
        try {
            UserEntity savedUser = userService.registerUser(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping({"/signin", "/login"})
    public ResponseEntity<?> login(@RequestBody UserEntity loginRequest) {
        try {
            String loginIdentifier = loginRequest.getEmail() != null && !loginRequest.getEmail().isEmpty()
                    ? loginRequest.getEmail()
                    : loginRequest.getUsername();

            Map<String, Object> result = userService.login(loginIdentifier, loginRequest.getPassword());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
