package dev.fmhj97.whatdoicookbackend.security;

import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of Spring Security's UserDetailsService.
 * Loads user data from the database by username for authentication purposes.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor with args
     * @param userRepository
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their username from the database.
     * @param username the username identifying the user whose data is required.
     * @return the UserDetails of the found user.
     * @throws UsernameNotFoundException User not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Search in the database.
        Optional<User> user = userRepository.findByUsername(username);

        // If the user existed, return the UserDetails of the found user (User entity implements UserDetails).
        if (user.isPresent()) return user.get();

        // If user not found, throw exception.
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
