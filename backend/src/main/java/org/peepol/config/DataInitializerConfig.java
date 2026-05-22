package org.peepol.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.peepol.domain.enums.Role;
import org.peepol.domain.enums.Status;
import org.peepol.domain.model.User;
import org.peepol.domain.repo.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializerConfig {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.system.username:admin}")
    private String systemUsername;

    @Value("${app.system.password:admin123}")
    private String systemPassword;

    @Bean
    public CommandLineRunner initData() {
        return _ -> {
            if (userRepo.findByUsername(systemUsername).isEmpty()) {
                log.info("Initializing default admin user: {}", systemUsername);
                User admin = new User();
                admin.setUsername(systemUsername);
                admin.setPassword(passwordEncoder.encode(systemPassword));
                admin.setRole(Role.ADMIN);
                admin.setStatus(Status.ENABLED);
                admin.setCreatedBy(systemUsername);
                userRepo.save(admin);
                log.info("Default admin user initialized successfully.");
            } else {
                log.info("Admin user already exists. Skipping initialization.");
            }
        };
    }
}
