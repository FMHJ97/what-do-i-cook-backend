package dev.fmhj97.whatdoicookbackend.service.auth;

import dev.fmhj97.whatdoicookbackend.dto.auth.AuthResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.auth.LoginRequestDto;
import dev.fmhj97.whatdoicookbackend.dto.auth.RegisterRequestDto;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.entity.enums.Role;
import dev.fmhj97.whatdoicookbackend.exception.DuplicateResourceException;
import dev.fmhj97.whatdoicookbackend.repository.UserRepository;
import dev.fmhj97.whatdoicookbackend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor with args
     * @param userRepository
     * @param passwordEncoder
     * @param jwtService
     * @param userDetailsService
     * @param authenticationManager
     */
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Authenticates a user using their username and password.
     * If credentials are valid, generates and returns a JWT token.
     * @param dto the login request containing email and password
     * @return an AuthResponseDto containing the JWT token
     */
    public AuthResponseDto login(LoginRequestDto dto) {

        // Verify credentials — loads the user from the database and compares
        // the provided password with the stored hashed password.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        // Load the user from the database.
        User currentUser = (User) userDetailsService.loadUserByUsername(dto.username());

        // Update and save the lastLoginAt field.
        currentUser.updateLastLoginAt();
        userRepository.save(currentUser);

        // Generate and return the JWT token.
        String jwtToken = jwtService.generateJwtToken(currentUser);

        return new AuthResponseDto(jwtToken);
    }

    /**
     * Registers a new user (role = 'USER', encodes the password,
     * saves the user to the database, and returns a JWT token.
     * @param dto the registration request
     * @return an AuthResponseDto containing the JWT token
     */
    public AuthResponseDto register(RegisterRequestDto dto) {

        // Check if the email or username already exist in the database.
        if (userRepository.existsByUsername(dto.username()))
            throw new DuplicateResourceException("Username already exists: " + dto.username());

        if (userRepository.existsByEmail(dto.email()))
            throw new DuplicateResourceException("Email already exists: " + dto.email());

        // Encode the password before saving.
        String encodedPassword = passwordEncoder.encode(dto.password());

        // Create and save the new user (role = 'USER' by default).
        User newUser = new User(
                dto.username(), dto.email(), encodedPassword, Role.USER
        );

        userRepository.save(newUser);

        // Generate and return the JWT token.
        String jwtToken = jwtService.generateJwtToken(newUser);

        return new AuthResponseDto(jwtToken);

    }

}
