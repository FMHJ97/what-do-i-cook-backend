package dev.fmhj97.whatdoicookbackend.service.auth;

import dev.fmhj97.whatdoicookbackend.dto.auth.AuthResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.auth.LoginRequestDto;
import dev.fmhj97.whatdoicookbackend.dto.auth.RegisterRequestDto;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.entity.enums.Role;
import dev.fmhj97.whatdoicookbackend.exception.DuplicateResourceException;
import dev.fmhj97.whatdoicookbackend.repository.UserRepository;
import dev.fmhj97.whatdoicookbackend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// Enables Mockito annotations in this test class
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // @Mock creates a fake (mock) implementation of each dependency
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private UserDetailsService userDetailsService;
    @Mock private AuthenticationManager authenticationManager;

    // @InjectMocks creates the real AuthService and injects the mocks above
    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequestDto registerDto;
    private LoginRequestDto loginDto;

    // Runs before each test — sets up common test data
    @BeforeEach
    void setUp() {
        testUser = new User("francisco", "francisco@test.com", "encodedPassword", Role.USER);

        registerDto = new RegisterRequestDto("francisco", "francisco@test.com", "password123");
        loginDto = new LoginRequestDto("francisco", "password123");
    }

    // --- register() ---

    @Test
    void register_ShouldReturnToken_WhenValidData() {
        // Arrange — define what the mocks return when called
        when(userRepository.existsByUsername("francisco")).thenReturn(false);
        when(userRepository.existsByEmail("francisco@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateJwtToken(any(User.class))).thenReturn("jwt-token");

        // Act — call the method we are testing
        AuthResponseDto result = authService.register(registerDto);

        // Assert — verify the result is what we expect
        assertThat(result.token()).isEqualTo("jwt-token");

        // Verify — check that specific methods were called
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateJwtToken(any(User.class));
    }

    @Test
    void register_ShouldThrowDuplicateResourceException_WhenUsernameExists() {
        // Arrange
        when(userRepository.existsByUsername("francisco")).thenReturn(true);

        // Assert + Act — expect this exception to be thrown when calling register()
        assertThatThrownBy(() -> authService.register(registerDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("francisco");

        // Verify that save() was never called
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_ShouldThrowDuplicateResourceException_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByUsername("francisco")).thenReturn(false);
        when(userRepository.existsByEmail("francisco@test.com")).thenReturn(true);

        // Assert + Act
        assertThatThrownBy(() -> authService.register(registerDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("francisco@test.com");

        verify(userRepository, never()).save(any());
    }

    // --- login() ---

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() {
        // Arrange
        when(userDetailsService.loadUserByUsername("francisco")).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateJwtToken(testUser)).thenReturn("jwt-token");

        // Act
        AuthResponseDto result = authService.login(loginDto);

        // Assert
        assertThat(result.token()).isEqualTo("jwt-token");

        // Verify authenticate() was called with correct credentials
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("francisco", "password123")
        );
        verify(userRepository).save(testUser);
    }

    @Test
    void login_ShouldThrowBadCredentialsException_WhenInvalidCredentials() {
        // Arrange — simulate authentication failure
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        // Assert + Act
        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(BadCredentialsException.class);

        // Verify token was never generated
        verify(jwtService, never()).generateJwtToken(any());
    }

    @Test
    void login_ShouldUpdateLastLoginAt_WhenValidCredentials() {
        // Arrange
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateJwtToken(any())).thenReturn("jwt-token");

        // Act
        authService.login(loginDto);

        // Verify user was saved (lastLoginAt updated)
        verify(userRepository).save(testUser);
    }
}
