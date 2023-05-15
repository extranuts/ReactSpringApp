package com.dom.reactspringapp.security;

import com.dom.reactspringapp.entity.UserEntity;
import com.dom.reactspringapp.exception.AuthException;
import com.dom.reactspringapp.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private String expirationInSeconds;
    @Value("${jwt.issuer}")
    private String issuer;

    private TokenDetails generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>() {{
            put("role", user.getId());
        }};
        return generateToken(claims, user.getId().toString());
    }

    private TokenDetails generateToken(Map<String, Object> claims, String subject) {
        long expirationTimeInMillis = Long.parseLong(expirationInSeconds) * 1000L;
        Date exprirationDate = new Date(new Date().getTime() + expirationTimeInMillis);

        return generateToken(exprirationDate, claims, subject);
    }

    private TokenDetails generateToken(Date experationDate, Map<String, Object> claims, String subject) {
        Date createdDate = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setId(UUID.randomUUID().toString())
                .setExpiration(experationDate)
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();
        return TokenDetails.builder()
                .token(token)
                .issuedAt(createdDate)
                .expiresAt(experationDate)
                .build();
    }

    public Mono<TokenDetails> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    if (!user.isEnabled()) {
                        return Mono.error(new AuthException("Account disabled", "USER_ACCOUNT_DISABLED"));
                    }
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new AuthException("Invalid password", "INVALID_PASSWORD"));
                    }
                    return Mono.just(generateToken(user).builder()
                            .userId(user.getId())
                            .build()
                    );
                })
                .switchIfEmpty(Mono.error(new AuthException("Invalid username", "INVALID_USERNAME")));
    }
}
