package dev.fmhj97.whatdoicookbackend.controller;

import dev.fmhj97.whatdoicookbackend.dto.user.ChangePasswordDto;
import dev.fmhj97.whatdoicookbackend.dto.user.DeleteAccountDto;
import dev.fmhj97.whatdoicookbackend.dto.user.UserResponseDto;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profile", description = "Endpoints for the authenticated user to manage their own account")
public class ProfileController {

    private final UserService userService;

    /**
     * Constructor with args.
     * @param userService
     */
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns the profile information of the currently authenticated user.
     * @param currentUser The authenticated user.
     * @return The user's profile data.
     */
    @GetMapping
    @Operation(summary = "Returns the profile information of the currently authenticated user")
    public ResponseEntity<UserResponseDto> getProfileInfo(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                userService.getProfileInfo(currentUser)
        );
    }

    /**
     * Changes the password of the currently authenticated user.
     * @param currentUser The authenticated user.
     * @param dto The current and new password.
     * @return An empty response with status 204 (No Content).
     */
    @PatchMapping("/password")
    @Operation(summary = "Changes the password of the currently authenticated user")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ChangePasswordDto dto
    ) {
        userService.changeOwnPassword(currentUser, dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes the account of the currently authenticated user, after verifying their password.
     * @param currentUser The authenticated user.
     * @param dto The password used to confirm the deletion.
     * @return An empty response with status 204 (No Content).
     */
    @DeleteMapping
    @Operation(summary = "Deletes the account of the currently authenticated user")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody DeleteAccountDto dto
    ) {
        userService.deleteAccount(currentUser, dto);
        return ResponseEntity.noContent().build();
    }
}
