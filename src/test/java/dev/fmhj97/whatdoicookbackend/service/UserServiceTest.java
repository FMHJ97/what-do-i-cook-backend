package dev.fmhj97.whatdoicookbackend.service;

import dev.fmhj97.whatdoicookbackend.dto.user.ChangePasswordDto;
import dev.fmhj97.whatdoicookbackend.dto.user.DeleteAccountDto;
import dev.fmhj97.whatdoicookbackend.dto.user.UserResponseDto;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.entity.enums.Role;
import dev.fmhj97.whatdoicookbackend.exception.InvalidDataException;
import dev.fmhj97.whatdoicookbackend.exception.ResourceNotFoundException;
import dev.fmhj97.whatdoicookbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User("david", "david@test.com", "encoded", Role.USER);
        user2 = new User("sara", "sara@test.com", "encoded", Role.USER);

        setId(user1, 1L);
        setId(user2, 2L);
    }

    // Helper to set private IDs via reflection (since there's no setter for ID)
    private void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // --- getUsers() ---

    @Test
    void getUsers_ShouldReturnNonAdminUsers_WhenCalled() {
        // Arrange
        when(userRepository.findByRoleNot(Role.ADMIN)).thenReturn(List.of(user1, user2));

        // Act
        List<UserResponseDto> result = userService.getUsers();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserResponseDto::username)
                .containsExactlyInAnyOrder("david", "sara");

        // Verify
        verify(userRepository).findByRoleNot(Role.ADMIN);
    }

    // --- getUserById ---

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // Act
        UserResponseDto result = userService.getUserById(1L);

        // Assert
        assertThat(result.username()).isEqualTo("david");
    }

    @Test
    void getUserById_ShouldThrowResourceNotFoundException_WhenNotExists() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- getProfileInfo() ---

    @Test
    void getProfileInfo_ShouldReturnCurrentUser_WhenCalled() {
        // Act
        UserResponseDto result = userService.getProfileInfo(user1);

        // Assert
        assertThat(result.username()).isEqualTo("david");
        assertThat(result.email()).isEqualTo("david@test.com");
    }

    // --- changeOwnPassword() ---

    @Test
    void changeOwnPassword_ShouldUpdatePassword_WhenCurrentPasswordIsCorrect() {

        ChangePasswordDto dto = new ChangePasswordDto("encoded", "newPassword");
        when(passwordEncoder.matches("encoded", user1.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncoded");

        // Act
        userService.changeOwnPassword(user1, dto);

        // Assert
        assertThat(user1.getPassword()).isEqualTo("newEncoded");

        // Verify
        verify(userRepository).save(user1);
    }

    @Test
    void changeOwnPassword_ShouldThrowInvalidDataException_WhenCurrentPasswordIsIncorrect() {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("wrongPassword", "newPassword");
        when(passwordEncoder.matches("wrongPassword", "encoded")).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> userService.changeOwnPassword(user1, dto))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Current password is incorrect");

        // Verify
        verify(userRepository, never()).save(any());
    }

    // --- deleteAccount() ---

    @Test
    void deleteAccount_ShouldDeleteUser_WhenPasswordIsCorrect() {
        // Arrange
        DeleteAccountDto dto = new DeleteAccountDto("encoded");
        when(passwordEncoder.matches("encoded", "encoded")).thenReturn(true);

        // Act
        userService.deleteAccount(user1, dto);

        // Verify
        verify(userRepository).delete(user1);
    }

    @Test
    void deleteAccount_ShouldThrowInvalidDataException_WhenPasswordIsIncorrect() {
        // Arrange
        DeleteAccountDto dto = new DeleteAccountDto("wrongPassword");
        when(passwordEncoder.matches("wrongPassword", "encoded")).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> userService.deleteAccount(user1, dto))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Password is incorrect");

        // Verify
        verify(userRepository, never()).delete(any());
    }

    // --- deleteUserById() ---

    @Test
    void deleteUserById_ShouldDeleteUser_WhenExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // Act
        userService.deleteUserById(1L);

        // Verify
        verify(userRepository).delete(user1);
    }

    @Test
    void deleteUserById_ShouldThrowResourceNotFoundException_WhenNotExists() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> userService.deleteUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        // Verify
        verify(userRepository, never()).delete(any());
    }

}
