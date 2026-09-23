package dev.fmhj97.whatdoicookbackend.dto.user;

import dev.fmhj97.whatdoicookbackend.entity.User;

import java.time.LocalDateTime;

/**
 * Response DTO for admin user management. Extends the information exposed by
 * {@link UserResponseDto} with the number of recipes owned by the user.
 */
public record AdminUserResponseDto(
        Long id,
        String username,
        String email,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLoginAt,
        long recipeCount
) {

    /**
     * Converts a User entity and its recipe count to an AdminUserResponseDto.
     * @param user The entity to convert.
     * @param recipeCount The number of recipes owned by the user.
     * @return An AdminUserResponseDto with the user's data and recipe count.
     */
    public static AdminUserResponseDto from(User user, long recipeCount) {
        return new AdminUserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt(),
                recipeCount
        );
    }

}