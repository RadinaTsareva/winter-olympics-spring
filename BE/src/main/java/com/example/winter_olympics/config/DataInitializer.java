package com.example.winter_olympics.config;

import com.example.winter_olympics.entity.Role;
import com.example.winter_olympics.entity.User;
import com.example.winter_olympics.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.initial-password:}") String initialPassword
    ) {
        return args -> {

            if (!userRepository.existsByUsername("admin")) {

                if (initialPassword == null || initialPassword.isBlank()) {
                    throw new IllegalStateException(
                            "ADMIN_INITIAL_PASSWORD must be set to create the initial admin account"
                    );
                }

                User admin = new User(
                        "admin",
                        passwordEncoder.encode(initialPassword),
                        Role.ADMIN
                );

                userRepository.save(admin);

                System.out.println("Default admin user created.");
            }
        };
    }
}
