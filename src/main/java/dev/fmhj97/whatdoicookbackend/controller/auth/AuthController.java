package dev.fmhj97.whatdoicookbackend.controller.auth;

import dev.fmhj97.whatdoicookbackend.dto.auth.AuthResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.auth.LoginRequestDto;
import dev.fmhj97.whatdoicookbackend.dto.auth.RegisterRequestDto;
import dev.fmhj97.whatdoicookbackend.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Endpoints related to authentication")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor with args.
     * @param authService
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticates a user and returns a JWT token if credentials are valid.
     * @param dto the login request containing username and password.
     * @return a response containing the generated JWT token.
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticates a user and returns a JWT token")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    /**
     * Registers a new user and returns a JWT token after successful registration.
     * @param dto the registration request containing user data.
     * @return a response containing the generated JWT token.
     */
    @PostMapping("/register")
    @Operation(summary = "Registers a new user and returns a JWT token")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterRequestDto dto) {
        return ResponseEntity.ok(authService.register(dto));
    }
}
