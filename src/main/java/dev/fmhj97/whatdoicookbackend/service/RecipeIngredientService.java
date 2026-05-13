package dev.fmhj97.whatdoicookbackend.service;

import dev.fmhj97.whatdoicookbackend.dto.recipeingredient.RecipeIngredientCreateDto;
import dev.fmhj97.whatdoicookbackend.dto.recipeingredient.RecipeIngredientResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.recipeingredient.RecipeIngredientUpdateDto;
import dev.fmhj97.whatdoicookbackend.entity.Ingredient;
import dev.fmhj97.whatdoicookbackend.entity.Recipe;
import dev.fmhj97.whatdoicookbackend.entity.RecipeIngredient;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.exception.DuplicateResourceException;
import dev.fmhj97.whatdoicookbackend.exception.ForbiddenException;
import dev.fmhj97.whatdoicookbackend.exception.ResourceNotFoundException;
import dev.fmhj97.whatdoicookbackend.repository.IngredientRepository;
import dev.fmhj97.whatdoicookbackend.repository.RecipeIngredientRepository;
import dev.fmhj97.whatdoicookbackend.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeIngredientService {

    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    /**
     * Constructor.
     * @param recipeIngredientRepository
     * @param recipeRepository
     * @param ingredientRepository
     */
    public RecipeIngredientService(
            RecipeIngredientRepository recipeIngredientRepository,
            RecipeRepository recipeRepository,
            IngredientRepository ingredientRepository
    ) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Returns a list of all ingredients for the given recipe.
     * @param currentUser The current user.
     * @param recipeId The recipe ID.
     * @return List of recipe ingredients.
     */
    @Transactional(readOnly = true)
    public List<RecipeIngredientResponseDto> getRecipeIngredients(
            User currentUser, Long recipeId
    ) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        if (!recipe.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to access this RecipeIngredient");
        }

        return recipeIngredientRepository.findByRecipeId(recipeId).stream()
                .map(RecipeIngredientResponseDto::from)
                .toList();
    }

    /**
     * Returns a single recipe ingredient by its ID.
     * @param currentUser The current user.
     * @param recipeId The recipe ID.
     * @param recipeIngredientId The recipe ingredient ID.
     * @return The recipe ingredient data.
     */
    @Transactional(readOnly = true)
    public RecipeIngredientResponseDto getRecipeIngredientById(
            User currentUser, Long recipeId, Long recipeIngredientId
    ) {
        RecipeIngredient recipeIngredient = recipeIngredientRepository.findByIdAndRecipeId(recipeIngredientId, recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("RecipeIngredient not found with id: " + recipeIngredientId));

        if (!recipeIngredient.getRecipe().getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to access this RecipeIngredient");
        }

        return RecipeIngredientResponseDto.from(recipeIngredient);
    }

    /**
     * Adds a new ingredient to the given recipe.
     * @param currentUser The current user.
     * @param recipeId The recipe ID.
     * @param dto The ingredient data.
     * @return The created recipe ingredient.
     */
    @Transactional
    public RecipeIngredientResponseDto addRecipeIngredient(
            User currentUser, Long recipeId, RecipeIngredientCreateDto dto
    ) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        Ingredient ingredient = ingredientRepository.findById(dto.ingredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + dto.ingredientId()));

        if (!recipe.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to create this RecipeIngredient");
        }

        if (recipeIngredientRepository.existsByRecipeIdAndIngredientId(recipeId, ingredient.getId())) {
            throw new DuplicateResourceException("RecipeIngredient already exists with recipe ID: " + recipeId
                    + " and ingredient ID: " + ingredient.getId());
        }

        RecipeIngredient newRecipeIngredient = new RecipeIngredient(recipe, ingredient, dto.quantity(), dto.unit());

        return RecipeIngredientResponseDto.from(
                recipeIngredientRepository.save(newRecipeIngredient)
        );
    }

    /**
     * Deletes a recipe ingredient by its ID.
     * @param currentUser The current user.
     * @param recipeId The recipe ID.
     * @param recipeIngredientId The recipe ingredient ID.
     */
    @Transactional
    public void deleteRecipeIngredient(
            User currentUser, Long recipeId, Long recipeIngredientId
    ) {
        RecipeIngredient recipeIngredient = recipeIngredientRepository.findByIdAndRecipeId(recipeIngredientId, recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("RecipeIngredient not found with id: " + recipeIngredientId));

        if (!recipeIngredient.getRecipe().getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to delete this RecipeIngredient");
        }

        recipeIngredientRepository.delete(recipeIngredient);
    }

    /**
     * Updates an existing recipe ingredient by its ID.
     * @param currentUser The current user.
     * @param recipeId The recipe ID.
     * @param recipeIngredientId The recipe ingredient ID.
     * @param dto The updated data.
     * @return The updated recipe ingredient.
     */
    @Transactional
    public RecipeIngredientResponseDto updateRecipeIngredient(
            User currentUser, Long recipeId, Long recipeIngredientId, RecipeIngredientUpdateDto dto
    ) {
        RecipeIngredient recipeIngredient = recipeIngredientRepository.findByIdAndRecipeId(recipeIngredientId, recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("RecipeIngredient not found with id: " + recipeIngredientId));

        if (!recipeIngredient.getRecipe().getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to update this RecipeIngredient");
        }

        if (dto.quantity() != null && !dto.quantity().isBlank()) recipeIngredient.setQuantity(dto.quantity());

        if (dto.unit() != null && !dto.unit().isBlank()) recipeIngredient.setUnit(dto.unit());

        return RecipeIngredientResponseDto.from(
                recipeIngredientRepository.save(recipeIngredient)
        );
    }
}
