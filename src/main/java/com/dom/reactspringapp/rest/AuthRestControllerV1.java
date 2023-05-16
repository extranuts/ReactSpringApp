package com.dom.reactspringapp.rest;

import com.dom.reactspringapp.dto.AuthRequestDTO;
import com.dom.reactspringapp.dto.AuthResponseDTO;
import com.dom.reactspringapp.dto.UserDTO;
import com.dom.reactspringapp.entity.UserEntity;
import com.dom.reactspringapp.mapper.UserMapper;
import com.dom.reactspringapp.security.CustomPrincipal;
import com.dom.reactspringapp.security.SecurityService;
import com.dom.reactspringapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestControllerV1 {

    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public Mono<UserDTO> register(@RequestBody UserDTO dto) {
        UserEntity entity = userMapper.map(dto);
        return userService.registerUser(entity)
                .map(userMapper::map);
    }

    @PostMapping("/login")
    public Mono<AuthResponseDTO> login(@RequestBody AuthRequestDTO dto) {
        return securityService.authenticate(dto.getUsername(), dto.getPassword())
                .flatMap(tokenDetails -> Mono.just(
                        AuthResponseDTO.builder()
                                .userId(tokenDetails.getUserId())
                                .token(tokenDetails.getToken())
                                .issuedAt(tokenDetails.getIssuedAt())
                                .expiresAt(tokenDetails.getExpiresAt())
                                .build()
                ));
    }

    @GetMapping("/info")
    public Mono<UserDTO> getUserInfo(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return userService.getUserById(principal.getId())
                .map(userMapper::map);
    }

}
