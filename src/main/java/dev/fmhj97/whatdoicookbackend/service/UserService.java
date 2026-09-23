package dev.fmhj97.whatdoicookbackend.service;

import dev.fmhj97.whatdoicookbackend.dto.user.AdminUserResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.user.ChangePasswordDto;
import dev.fmhj97.whatdoicookbackend.dto.user.DeleteAccountDto;
import dev.fmhj97.whatdoicookbackend.dto.user.UserResponseDto;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.entity.enums.Role;
import dev.fmhj97.whatdoicookbackend.exception.InvalidDataException;
import dev.fmhj97.whatdoicookbackend.exception.ResourceNotFoundException;
import dev.fmhj97.whatdoicookbackend.repository.RecipeRepository;
import dev.fmhj97.whatdoicookbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for user-related operations.
 * Handles both self-service profile actions (password change, account deletion)
 * and admin management of users.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecipeRepository recipeRepository;

    /**
     * Constructor with args.
     * @param userRepository
     * @param passwordEncoder
     * @param recipeRepository
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RecipeRepository recipeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.recipeRepository = recipeRepository;
    }

    /**
     * Returns a list of all registered users, excluding admin accounts, with their recipe counts. Admin only.
     * @return List of all non-admin users.
     */
    @Transactional(readOnly = true)
    public List<AdminUserResponseDto> getUsers() {
        Map<Long, Long> recipeCounts = recipeRepository.countRecipesGroupedByOwner().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        return userRepository.findByRoleNot(Role.ADMIN).stream()
                .map(user -> AdminUserResponseDto.from(
                        user,
                        recipeCounts.getOrDefault(user.getId(), 0L)
                ))
                .toList();
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
