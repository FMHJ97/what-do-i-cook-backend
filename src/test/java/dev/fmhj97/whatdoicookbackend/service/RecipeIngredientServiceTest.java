package dev.fmhj97.whatdoicookbackend.service;

import dev.fmhj97.whatdoicookbackend.dto.recipeingredient.RecipeIngredientCreateDto;
import dev.fmhj97.whatdoicookbackend.dto.recipeingredient.RecipeIngredientResponseDto;
import dev.fmhj97.whatdoicookbackend.entity.Ingredient;
import dev.fmhj97.whatdoicookbackend.entity.Recipe;
import dev.fmhj97.whatdoicookbackend.entity.RecipeIngredient;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.entity.enums.FoodType;
import dev.fmhj97.whatdoicookbackend.entity.enums.Role;
import dev.fmhj97.whatdoicookbackend.entity.enums.Source;
import dev.fmhj97.whatdoicookbackend.exception.DuplicateResourceException;
import dev.fmhj97.whatdoicookbackend.exception.ForbiddenException;
import dev.fmhj97.whatdoicookbackend.exception.ResourceNotFoundException;
import dev.fmhj97.whatdoicookbackend.repository.IngredientRepository;
import dev.fmhj97.whatdoicookbackend.repository.RecipeIngredientRepository;
import dev.fmhj97.whatdoicookbackend.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeIngredientServiceTest {

    @Mock private RecipeIngredientRepository recipeIngredientRepository;
    @Mock private RecipeRepository recipeRepository;
    @Mock private IngredientRepository ingredientRepository;

    @InjectMocks private RecipeIngredientService recipeIngredientService;

    private User owner;
    private User anotherUser;
    private Recipe recipe;
    private Ingredient ingredient;
    private RecipeIngredient recipeIngredient;

    @BeforeEach
    void setUp() {
        owner = new User("francisco", "francisco@test.com", "encoded", Role.USER);
        anotherUser = new User("other", "other@test.com", "encoded", Role.USER);

        setId(owner, 1L);
        setId(anotherUser, 2L);

        recipe = new Recipe("Paella", owner, "Classic Spanish dish", FoodType.RICE, Source.USER, 4, 30, 60);
        setId(recipe, 1L);

        ingredient = new Ingredient("Rice");
        setId(ingredient, 1L);

        recipeIngredient = new RecipeIngredient(recipe, ingredient, "500", "g");
        setId(recipeIngredient, 1L);
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

    // --- addRecipeIngredient() ---

    @Test
    void addRecipeIngredient_ShouldReturnCreated_WhenOwnerRequests() {
        // Arrange
        RecipeIngredientCreateDto dto = new RecipeIngredientCreateDto(1L, "500", "g");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(recipeIngredientRepository.existsByRecipeIdAndIngredientId(1L, 1L)).thenReturn(false);
        when(recipeIngredientRepository.save(any(RecipeIngredient.class))).thenReturn(recipeIngredient);

        // Act
        RecipeIngredientResponseDto result = recipeIngredientService.addRecipeIngredient(owner, 1L, dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.recipeId()).isEqualTo(1L);
        assertThat(result.ingredientId()).isEqualTo(1L);
        verify(recipeIngredientRepository).save(any(RecipeIngredient.class));
    }

    @Test
    void addRecipeIngredient_ShouldThrowDuplicateResourceException_WhenAlreadyExists() {
        // Arrange
        RecipeIngredientCreateDto dto = new RecipeIngredientCreateDto(1L, "500", "g");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(recipeIngredientRepository.existsByRecipeIdAndIngredientId(1L, 1L)).thenReturn(true);

        // Assert + Act
        assertThatThrownBy(() -> recipeIngredientService.addRecipeIngredient(owner, 1L, dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(recipeIngredientRepository, never()).save(any());
    }

    @Test
    void addRecipeIngredient_ShouldThrowDuplicateResourceException_OnConstraintViolation() {
        // Arrange — the exists check passes but the DB unique constraint rejects a concurrent duplicate.
        RecipeIngredientCreateDto dto = new RecipeIngredientCreateDto(1L, "500", "g");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(recipeIngredientRepository.existsByRecipeIdAndIngredientId(1L, 1L)).thenReturn(false);
        doThrow(new DataIntegrityViolationException("duplicate key"))
                .when(recipeIngredientRepository).save(any(RecipeIngredient.class));

        // Assert + Act
        assertThatThrownBy(() -> recipeIngredientService.addRecipeIngredient(owner, 1L, dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void addRecipeIngredient_ShouldThrowResourceNotFoundException_WhenRecipeNotExists() {
        // Arrange
        RecipeIngredientCreateDto dto = new RecipeIngredientCreateDto(1L, "500", "g");
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert + Act
        assertThatThrownBy(() -> recipeIngredientService.addRecipeIngredient(owner, 99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void addRecipeIngredient_ShouldThrowResourceNotFoundException_WhenIngredientNotExists() {
        // Arrange
        RecipeIngredientCreateDto dto = new RecipeIngredientCreateDto(42L, "500", "g");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(ingredientRepository.findById(42L)).thenReturn(Optional.empty());

        // Assert + Act
        assertThatThrownBy(() -> recipeIngredientService.addRecipeIngredient(owner, 1L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    void addRecipeIngredient_ShouldThrowForbiddenException_WhenNotOwner() {
        // Arrange
        RecipeIngredientCreateDto dto = new RecipeIngredientCreateDto(1L, "500", "g");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));

        // Assert + Act
        assertThatThrownBy(() -> recipeIngredientService.addRecipeIngredient(anotherUser, 1L, dto))
                .isInstanceOf(ForbiddenException.class);

        verify(recipeIngredientRepository, never()).save(any());
    }

    // --- getRecipeIngredients() ---

    @Test
    void getRecipeIngredients_ShouldReturnIngredients_WhenOwnerRequests() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(recipeIngredientRepository.findByRecipeId(1L)).thenReturn(List.of(recipeIngredient));

        // Act
        List<RecipeIngredientResponseDto> result = recipeIngredientService.getRecipeIngredients(owner, 1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).ingredientId()).isEqualTo(1L);
    }

    // --- deleteRecipeIngredient() ---

    @Test
    void deleteRecipeIngredient_ShouldDelete_WhenOwnerRequests() {
        // Arrange
        when(recipeIngredientRepository.findByIdAndRecipeId(1L, 1L)).thenReturn(Optional.of(recipeIngredient));

        // Act
        recipeIngredientService.deleteRecipeIngredient(owner, 1L, 1L);

        // Verify delete was called
        verify(recipeIngredientRepository).delete(recipeIngredient);
    }

    @Test
    void deleteRecipeIngredient_ShouldThrowForbiddenException_WhenNotOwner() {
        // Arrange
        when(recipeIngredientRepository.findByIdAndRecipeId(1L, 1L)).thenReturn(Optional.of(recipeIngredient));

        // Assert + Act
        assertThatThrownBy(() -> recipeIngredientService.deleteRecipeIngredient(anotherUser, 1L, 1L))
                .isInstanceOf(ForbiddenException.class);

        verify(recipeIngredientRepository, never()).delete(any());
    }
}