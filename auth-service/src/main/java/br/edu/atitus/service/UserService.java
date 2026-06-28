package br.edu.atitus.service;

import br.edu.atitus.model.UserEntity;
import br.edu.atitus.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserEntity registerUser(UserEntity user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username já está em uso!");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já está em uso!");
        }

        // Criptografa a senha antes de persistir no banco
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        // Busca o usuário pelo username, se não achar estoura erro genérico por segurança
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos!"));

        // Valida se a senha enviada bate com o hash criptografado no banco
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Usuário ou senha inválidos!");
        }

        // Se passar nas duas validações, gera e retorna o Token JWT
        return jwtService.generateToken(user);
    }
}