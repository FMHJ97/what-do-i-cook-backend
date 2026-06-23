package dev.fmhj97.whatdoicookbackend.service;

import dev.fmhj97.whatdoicookbackend.dto.user.ChangePasswordDto;
import dev.fmhj97.whatdoicookbackend.dto.user.DeleteAccountDto;
import dev.fmhj97.whatdoicookbackend.dto.user.UserResponseDto;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.entity.enums.Role;
import dev.fmhj97.whatdoicookbackend.exception.InvalidDataException;
import dev.fmhj97.whatdoicookbackend.exception.ResourceNotFoundException;
import dev.fmhj97.whatdoicookbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsible for user-related operations.
 * Handles both self-service profile actions (password change, account deletion)
 * and admin management of users.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor with args.
     * @param userRepository
     * @param passwordEncoder
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Returns a list of all registered users, excluding admin accounts. Admin only.
     * @return List of all non-admin users.
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers() {
        return userRepository.findByRoleNot(Role.ADMIN).stream()
                .map(UserResponseDto::from)
                .toList();
    }

    /**
     * Returns a user by the given ID. Admin only.
     * @param userId The user ID.
     * @return The user data.
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return UserResponseDto.from(user);
    }

    /**
     * Returns the profile information of the currently authenticated user.
     * @param currentUser The authenticated user.
     * @return The user's own profile data.
     */
    @Transactional(readOnly = true)
    public UserResponseDto getProfileInfo(User currentUser) {
        return UserResponseDto.from(currentUser);
    }

    /**
     * Changes the password of the currently authenticated user.
     * Requires the current password for verification before updating it.
     * @param currentUser The authenticated user.
     * @param dto The current and new password.
     */
    @Transactional
    public void changeOwnPassword(
            User currentUser,
            ChangePasswordDto dto
    ) {
        if (!passwordEncoder.matches(dto.currentPassword(), currentUser.getPassword())) {
            throw new InvalidDataException("Current password is incorrect");
        }

        String encodedPassword = passwordEncoder.encode(dto.newPassword());
        currentUser.setPassword(encodedPassword);

        userRepository.save(currentUser);
    }

    /**
     * Deletes the account of the currently authenticated user.
     * Requires the current password for verification before deleting it.
     * Cascades the deletion to all owned resources (recipes -> RecipeStep + RecipeIngredient).
     * @param currentUser The authenticated user.
     * @param dto The password used to confirm the deletion.
     */
    @Transactional
    public void deleteAccount(
            User currentUser,
            DeleteAccountDto dto
    ) {
        if (!passwordEncoder.matches(dto.password(), currentUser.getPassword())) {
            throw new InvalidDataException("Password is incorrect");
        }

        userRepository.delete(currentUser);
    }

    /**
     * Deletes a user by the given ID. Admin only.
     * No password confirmation is required, since the admin is not the account owner.
     * @param userId The ID of the user to delete.
     */
    @Transactional
    public void deleteUserById(
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        userRepository.delete(user);
    }
}
