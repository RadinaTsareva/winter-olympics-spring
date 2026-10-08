package com.example.winter_olympics.controller;

import com.example.winter_olympics.dto.AuthRequest;
import com.example.winter_olympics.dto.AuthResponse;
import com.example.winter_olympics.entity.Role;
import com.example.winter_olympics.entity.User;
import com.example.winter_olympics.repository.UserRepository;
import com.example.winter_olympics.exception.BadRequestException;
import com.example.winter_olympics.exception.UnauthorizedException;
import com.example.winter_olympics.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService

    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (request.getAthleteId() != null) {
            throw new BadRequestException(
                    "Athlete profiles can only be associated through a trusted administrator workflow"
            );
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.ATHLETE
        );

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                token,
                savedUser.getUsername(),
                savedUser.getRole().name()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody AuthRequest request
    ) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid username or password")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name()
        );
    }
}
