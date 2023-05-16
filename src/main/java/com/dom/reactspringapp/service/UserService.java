package com.dom.reactspringapp.service;


import com.dom.reactspringapp.entity.UserEntity;
import com.dom.reactspringapp.entity.UserRole;
import com.dom.reactspringapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public Mono<UserEntity> registerUser(UserEntity user) {
        return repository.save(
                user.toBuilder()
                        .password(encoder.encode(user.getPassword()))
                        .role(UserRole.USER)
                        .enabled(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        ).doOnSuccess(u -> {
            log.info("In registerUser - user: {} created", u);
        });
    }

    public Mono<UserEntity> getUserById(Long id) {
        return repository.findById(id);
    }

    public Mono<UserEntity> getByUsername(String username) {
        return repository.findByUsername(username);
    }
}
