package dev.fmhj97.whatdoicookbackend.service;

import dev.fmhj97.whatdoicookbackend.dto.ingredient.IngredientCreateDto;
import dev.fmhj97.whatdoicookbackend.dto.ingredient.IngredientResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.ingredient.IngredientUpdateDto;
import dev.fmhj97.whatdoicookbackend.entity.Ingredient;
import dev.fmhj97.whatdoicookbackend.exception.DuplicateResourceException;
import dev.fmhj97.whatdoicookbackend.exception.ResourceNotFoundException;
import dev.fmhj97.whatdoicookbackend.repository.IngredientRepository;
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
class IngredientServiceTest {

    @Mock private IngredientRepository ingredientRepository;

    @InjectMocks private IngredientService ingredientService;

    private Ingredient tomato;
    private Ingredient onion;

    @BeforeEach
    void setUp() {
        tomato = new Ingredient("Tomato");
        onion = new Ingredient("Onion");

        setId(tomato, 1L);
        setId(onion, 2L);
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

    // --- getIngredientById() ---

    @Test
    void getIngredientById_ShouldReturnIngredient_WhenExists() {
        // Arrange
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(tomato));

        // Act
        IngredientResponseDto result = ingredientService.getIngredientById(1L);

        // Assert
        assertThat(result.name()).isEqualTo("Tomato");
    }

    @Test
    void getIngredientById_ShouldThrowResourceNotFoundException_WhenNotExists() {
        // Arrange
        when(ingredientRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert + Act
        assertThatThrownBy(() -> ingredientService.getIngredientById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- getIngredients() ---

    @Test
    void getIngredients_ShouldReturnAll_WhenNoNameProvided() {
        // Arrange
        when(ingredientRepository.findAll()).thenReturn(List.of(tomato, onion));

        // Act
        List<IngredientResponseDto> result = ingredientService.getIngredients(null);

        // Assert
        assertThat(result).hasSize(2);
        verify(ingredientRepository).findAll();
        verify(ingredientRepository, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    void getIngredients_ShouldFilterByName_WhenNameProvided() {
        // Arrange
        when(ingredientRepository.findByNameContainingIgnoreCase("tom")).thenReturn(List.of(tomato));

        // Act
        List<IngredientResponseDto> result = ingredientService.getIngredients("tom");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Tomato");
        verify(ingredientRepository).findByNameContainingIgnoreCase("tom");
        verify(ingredientRepository, never()).findAll();
    }

    @Test
    void getIngredients_ShouldReturnAll_WhenBlankNameProvided() {
        // Arrange — blank string should behave like null (return all)
        when(ingredientRepository.findAll()).thenReturn(List.of(tomato, onion));

        // Act
        List<IngredientResponseDto> result = ingredientService.getIngredients("   ");

        // Assert
        assertThat(result).hasSize(2);
        verify(ingredientRepository).findAll();
    }

    // --- addIngredient() ---

    @Test
    void addIngredient_ShouldReturnCreatedIngredient_WhenValidData() {
        // Arrange
        IngredientCreateDto dto = new IngredientCreateDto("Garlic");
        when(ingredientRepository.existsByNameIgnoreCase("Garlic")).thenReturn(false);
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(new Ingredient("Garlic"));

        // Act
        IngredientResponseDto result = ingredientService.addIngredient(dto);

        // Assert
        assertThat(result.name()).isEqualTo("Garlic");
        verify(ingredientRepository).save(any(Ingredient.class));
    }

    @Test
    void addIngredient_ShouldThrowDuplicateResourceException_WhenNameExists() {
        // Arrange
        IngredientCreateDto dto = new IngredientCreateDto("Tomato");
        when(ingredientRepository.existsByNameIgnoreCase("Tomato")).thenReturn(true);

        // Assert + Act
        assertThatThrownBy(() -> ingredientService.addIngredient(dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Tomato");

        verify(ingredientRepository, never()).save(any());
    }

    // --- deleteIngredient() ---

    @Test
    void deleteIngredient_ShouldDelete_WhenExists() {
        // Arrange
        when(ingredientRepository.existsById(1L)).thenReturn(true);

        // Act
        ingredientService.deleteIngredient(1L);

        // Verify deleteById was called
        verify(ingredientRepository).deleteById(1L);
    }

    @Test
    void deleteIngredient_ShouldThrowResourceNotFoundException_WhenNotExists() {
        // Arrange
        when(ingredientRepository.existsById(99L)).thenReturn(false);

        // Assert + Act
        assertThatThrownBy(() -> ingredientService.deleteIngredient(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(ingredientRepository, never()).deleteById(any());
    }

    // --- updateIngredient() ---

    @Test
    void updateIngredient_ShouldReturnUpdatedIngredient_WhenValidData() {
        // Arrange
        IngredientUpdateDto dto = new IngredientUpdateDto("Updated Tomato");
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(tomato));
        when(ingredientRepository.findByNameIgnoreCase("Updated Tomato")).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(new Ingredient("Updated Tomato"));

        // Act
        IngredientResponseDto result = ingredientService.updateIngredient(1L, dto);

        // Assert
        assertThat(result.name()).isEqualTo("Updated Tomato");
    }

    @Test
    void updateIngredient_ShouldThrowResourceNotFoundException_WhenIngredientNotExists() {
        // Arrange
        IngredientUpdateDto dto = new IngredientUpdateDto("New Name");
        when(ingredientRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert + Act
        assertThatThrownBy(() -> ingredientService.updateIngredient(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateIngredient_ShouldThrowDuplicateResourceException_WhenNameExistsInAnotherIngredient() {
        // Arrange
        IngredientUpdateDto dto = new IngredientUpdateDto("Onion");

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(tomato));
        when(ingredientRepository.findByNameIgnoreCase("Onion")).thenReturn(Optional.of(onion));

        // Assert + Act
        assertThatThrownBy(() -> ingredientService.updateIngredient(1L, dto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Onion");
    }
}
