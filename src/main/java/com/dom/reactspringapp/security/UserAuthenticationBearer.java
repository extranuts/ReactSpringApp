package com.dom.reactspringapp.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;

public class UserAuthenticationBearer {
    public static Mono<Authentication> create(JWTHandler.VerificationResult verification) {
        Claims claims = verification.claims;
        String subject = claims.getSubject();

        String role = claims.get("role", String.class);
        String username = claims.get("username", String.class);

        List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority(role));

        Long principalId = Long.parseLong(subject);

        CustomPrincipal principal = new CustomPrincipal(principalId, username);

        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(principal, null, authority));
    }
}
