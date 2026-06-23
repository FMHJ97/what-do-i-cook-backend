package dev.fmhj97.whatdoicookbackend.dto.user;

import dev.fmhj97.whatdoicookbackend.entity.User;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLoginAt
) {

    /**
     * Converts a User entity to an UserResponseDto.
     * @param user The entity to convert.
     * @return An UserResponseDto with the user's data.
     */
    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt()
        );
    }

}
