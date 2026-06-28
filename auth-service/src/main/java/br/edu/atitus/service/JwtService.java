package br.edu.atitus.service;

import br.edu.atitus.model.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // Chave secreta longa obrigatória para assinar o token JWT com segurança
    private final String SECRET_KEY = "minha_chave_secreta_muito_segura_e_longa_para_o_jwt_jlpg_motors";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // Expira em 24 horas
                .signWith(getSigningKey())
                .compact();
    }
}