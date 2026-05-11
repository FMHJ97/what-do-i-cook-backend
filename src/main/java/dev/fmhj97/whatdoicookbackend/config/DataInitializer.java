package dev.fmhj97.whatdoicookbackend.config;

import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.entity.enums.Role;
import dev.fmhj97.whatdoicookbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String username;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    /**
     * Constructor
     * @param userRepository
     * @param passwordEncoder
     */
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a default ADMIN user on application startup if none exists.
     * @param args incoming application arguments
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {

        // Checks if there is an existing ADMIN with the given credentials.
        if (!userRepository.existsByUsername(username) && !userRepository.existsByEmail(email)) {

            // New ADMIN user.
            User admin = new User(
                    username,
                    email,
                    passwordEncoder.encode(password),
                    Role.ADMIN
            );

            // Saves the ADMIN user.
            userRepository.save(admin);
        }
    }
}
