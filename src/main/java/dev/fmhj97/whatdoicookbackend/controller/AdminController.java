package dev.fmhj97.whatdoicookbackend.controller;

import dev.fmhj97.whatdoicookbackend.dto.user.UserResponseDto;
import dev.fmhj97.whatdoicookbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "Endpoints for admin management")
public class AdminController {

    private final UserService userService;

    /**
     * Constructor with args.
     * @param userService
     */
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns a list of all registered users, excluding admin accounts.
     * @return A response containing the list of all non-admin users.
     */
    @GetMapping("/users")
    @Operation(summary = "Returns a list of all registered users, excluding admin accounts")
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        return ResponseEntity.ok(
                userService.getUsers()
        );
    }

    /**
     * Returns a user by the given ID.
     * @param id The ID of the user.
     * @return The user's data.
     */
    @GetMapping("/users/{id}")
    @Operation(summary = "Returns a user by the given ID")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }

    /**
     * Deletes a user by the given ID.
     * @param id The ID of the user to delete.
     * @return An empty response with status 204 (No Content).
     */
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Deletes a user by the given ID")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable Long id
    ) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
