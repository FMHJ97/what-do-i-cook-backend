package dev.fmhj97.whatdoicookbackend.service;

import dev.fmhj97.whatdoicookbackend.dto.recipestep.RecipeStepCreateDto;
import dev.fmhj97.whatdoicookbackend.dto.recipestep.RecipeStepResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.recipestep.RecipeStepUpdateDto;
import dev.fmhj97.whatdoicookbackend.entity.Recipe;
import dev.fmhj97.whatdoicookbackend.entity.RecipeStep;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.entity.enums.FoodType;
import dev.fmhj97.whatdoicookbackend.entity.enums.Role;
import dev.fmhj97.whatdoicookbackend.entity.enums.Source;
import dev.fmhj97.whatdoicookbackend.exception.ForbiddenException;
import dev.fmhj97.whatdoicookbackend.exception.ResourceNotFoundException;
import dev.fmhj97.whatdoicookbackend.repository.RecipeRepository;
import dev.fmhj97.whatdoicookbackend.repository.RecipeStepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeStepServiceTest {

    @Mock private RecipeStepRepository recipeStepRepository;
    @Mock private RecipeRepository recipeRepository;

    @InjectMocks private RecipeStepService recipeStepService;

    private User owner;
    private User anotherUser;
    private Recipe recipe;
    private RecipeStep step;

    @BeforeEach
    void setUp() {
        owner = new User("francisco", "francisco@test.com", "encoded", Role.USER);
        anotherUser = new User("other", "other@test.com", "encoded", Role.USER);

        setId(owner, 1L);
        setId(anotherUser, 2L);

        recipe = new Recipe("Paella", owner, "Classic dish", FoodType.RICE, Source.USER, 4, 30, 60);
        setId(recipe, 1L);

        step = new RecipeStep(recipe, "Add rice to the pan");
        step.setStepNumber(1);
        setId(step, 1L);
    }

    private void setId(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // --- getRecipeSteps() ---

    @Test
    void getRecipeSteps_ShouldReturnSteps_WhenOwnerRequests() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(recipeStepRepository.findByRecipeIdOrderByStepNumberAsc(1L)).thenReturn(List.of(step));

        // Act
        List<RecipeStepResponseDto> result = recipeStepService.getRecipeSteps(owner, 1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).isEqualTo("Add rice to the pan");
    }

    @Test
    void getRecipeSteps_ShouldThrowForbiddenException_WhenNotOwner() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        // Assert + Act
        assertThatThrownBy(() -> recipeStepService.getRecipeSteps(anotherUser, 1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getRecipeSteps_ShouldThrowResourceNotFoundException_WhenRecipeNotExists() {
        // Arrange
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert + Act
        assertThatThrownBy(() -> recipeStepService.getRecipeSteps(owner, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- addRecipeStep() ---

    @Test
    void addRecipeStep_ShouldReturnCreatedStep_WhenOwnerRequests() {
        // Arrange
        RecipeStepCreateDto dto = new RecipeStepCreateDto("Boil water");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(recipeStepRepository.findMaxStepNumberByRecipeId(1L)).thenReturn(1);
        when(recipeStepRepository.save(any(RecipeStep.class))).thenReturn(step);

        // Act
        RecipeStepResponseDto result = recipeStepService.addRecipeStep(1L, owner, dto);

        // Assert
        assertThat(result).isNotNull();
        verify(recipeStepRepository).save(any(RecipeStep.class));
    }

    @Test
    void addRecipeStep_ShouldThrowForbiddenException_WhenNotOwner() {
        // Arrange
        RecipeStepCreateDto dto = new RecipeStepCreateDto("Boil water");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        // Assert + Act
        assertThatThrownBy(() -> recipeStepService.addRecipeStep(1L, anotherUser, dto))
                .isInstanceOf(ForbiddenException.class);

        verify(recipeStepRepository, never()).save(any());
    }

    @Test
    void addRecipeStep_ShouldAssignNextStepNumber_Automatically() {
        // Arrange — current max is 2, so next should be 3
        RecipeStepCreateDto dto = new RecipeStepCreateDto("Third step");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(recipeStepRepository.findMaxStepNumberByRecipeId(1L)).thenReturn(2);
        when(recipeStepRepository.save(any(RecipeStep.class))).thenAnswer(invocation -> {
            RecipeStep saved = invocation.getArgument(0);
            // Verify the step number was set to 3
            assertThat(saved.getStepNumber()).isEqualTo(3);
            return saved;
        });

        // Act
        recipeStepService.addRecipeStep(1L, owner, dto);
    }

    // --- deleteRecipeStep() ---

    @Test
    void deleteRecipeStep_ShouldDeleteAndRenumber_WhenOwnerRequests() {
        // Arrange
        RecipeStep step2 = new RecipeStep(recipe, "Second step");
        step2.setStepNumber(2);
        setId(step2, 2L);

        when(recipeStepRepository.findByIdAndRecipeId(1L, 1L)).thenReturn(Optional.of(step));
        when(recipeStepRepository.findByRecipeIdOrderByStepNumberAsc(1L)).thenReturn(List.of(step2));

        // Act
        recipeStepService.deleteRecipeStep(1L, owner, 1L);

        // Verify delete and saveAll were called
        verify(recipeStepRepository).delete(step);
        verify(recipeStepRepository).saveAll(any());
    }

    @Test
    void deleteRecipeStep_ShouldThrowForbiddenException_WhenNotOwner() {
        // Arrange
        when(recipeStepRepository.findByIdAndRecipeId(1L, 1L)).thenReturn(Optional.of(step));

        // Assert + Act
        assertThatThrownBy(() -> recipeStepService.deleteRecipeStep(1L, anotherUser, 1L))
                .isInstanceOf(ForbiddenException.class);

        verify(recipeStepRepository, never()).delete(any());
    }

    // --- updateRecipeStep() ---

    @Test
    void updateRecipeStep_ShouldReturnUpdatedStep_WhenOwnerRequests() {
        // Arrange
        RecipeStepUpdateDto dto = new RecipeStepUpdateDto("Updated description");
        when(recipeStepRepository.findByIdAndRecipeId(1L, 1L)).thenReturn(Optional.of(step));
        when(recipeStepRepository.save(any(RecipeStep.class))).thenReturn(step);

        // Act
        RecipeStepResponseDto result = recipeStepService.updateRecipeStep(owner, 1L, 1L, dto);

        // Assert
        assertThat(result).isNotNull();
        verify(recipeStepRepository).save(step);
    }

    @Test
    void updateRecipeStep_ShouldThrowForbiddenException_WhenNotOwner() {
        // Arrange
        RecipeStepUpdateDto dto = new RecipeStepUpdateDto("Updated description");
        when(recipeStepRepository.findByIdAndRecipeId(1L, 1L)).thenReturn(Optional.of(step));

        // Assert + Act
        assertThatThrownBy(() -> recipeStepService.updateRecipeStep(anotherUser, 1L, 1L, dto))
                .isInstanceOf(ForbiddenException.class);
    }
}
