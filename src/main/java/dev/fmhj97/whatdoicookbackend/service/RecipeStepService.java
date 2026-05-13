package dev.fmhj97.whatdoicookbackend.service;

import dev.fmhj97.whatdoicookbackend.dto.recipestep.RecipeStepCreateDto;
import dev.fmhj97.whatdoicookbackend.dto.recipestep.RecipeStepResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.recipestep.RecipeStepUpdateDto;
import dev.fmhj97.whatdoicookbackend.entity.Recipe;
import dev.fmhj97.whatdoicookbackend.entity.RecipeStep;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.exception.ForbiddenException;
import dev.fmhj97.whatdoicookbackend.exception.ResourceNotFoundException;
import dev.fmhj97.whatdoicookbackend.repository.RecipeRepository;
import dev.fmhj97.whatdoicookbackend.repository.RecipeStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeStepService {

    private final RecipeStepRepository recipeStepRepository;
    private final RecipeRepository recipeRepository;

    public RecipeStepService(RecipeStepRepository recipeStepRepository, RecipeRepository recipeRepository) {
        this.recipeStepRepository = recipeStepRepository;
        this.recipeRepository = recipeRepository;
    }

    /**
     * Returns a list of all steps for the given recipe, ordered by step number.
     * @param currentUser The current user.
     * @param recipeId The recipe ID.
     * @return List of recipe steps.
     */
    @Transactional(readOnly = true)
    public List<RecipeStepResponseDto> getRecipeSteps(
            User currentUser, Long recipeId
    ) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        if (!recipe.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to access this RecipeStep");
        }

        return recipeStepRepository.findByRecipeIdOrderByStepNumberAsc(recipeId).stream()
                .map(RecipeStepResponseDto::from)
                .toList();
    }

    /**
     * Returns a single step by its ID.
     * @param currentUser The current user.
     * @param recipeId The recipe ID.
     * @param stepId The step ID.
     * @return The existing step.
     */
    @Transactional(readOnly = true)
    public RecipeStepResponseDto getRecipeStepById(
            User currentUser, Long recipeId, Long stepId
    ) {
        RecipeStep recipeStep = recipeStepRepository.findByIdAndRecipeId(stepId, recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("RecipeStep not found with id: " + stepId));

        if (!recipeStep.getRecipe().getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to access this RecipeStep");
        }

        return RecipeStepResponseDto.from(recipeStep);
    }

    /**
     * Adds a new step to the given recipe. Step number is assigned automatically.
     * @param recipeId The recipe ID.
     * @param currentUser The current user.
     * @param dto The step data.
     * @return The created step.
     */
    @Transactional
    public RecipeStepResponseDto addRecipeStep(
            Long recipeId, User currentUser, RecipeStepCreateDto dto
    ) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        if (!recipe.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to create this RecipeStep");
        }

        RecipeStep newRecipeStep = new RecipeStep(recipe, dto.description());

        // Assigns the stepNumber automatically.
        int nextStepNumber = recipeStepRepository.findMaxStepNumberByRecipeId(recipe.getId()) + 1;

        newRecipeStep.setStepNumber(nextStepNumber);

        return RecipeStepResponseDto.from(recipeStepRepository.save(newRecipeStep));
    }

    /**
     * Deletes a step by its ID and renumbers the remaining steps.
     * @param recipeId The recipe ID.
     * @param currentUser The current user.
     * @param stepId The step ID.
     */
    @Transactional
    public void deleteRecipeStep(
            Long recipeId, User currentUser, Long stepId
    ) {
        RecipeStep recipeStep = recipeStepRepository.findByIdAndRecipeId(stepId, recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("RecipeStep not found with id: " + stepId));

        if (!recipeStep.getRecipe().getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to delete this RecipeStep");
        }

        recipeStepRepository.delete(recipeStep);

        // Renumbers steps after deletion to keep order continuous.
        List<RecipeStep> recipeSteps = recipeStepRepository.findByRecipeIdOrderByStepNumberAsc(recipeId);

        for (int i = 0; i < recipeSteps.size(); i++) {
            recipeSteps.get(i).setStepNumber(i + 1);
        }

        // Saves the changes.
        recipeStepRepository.saveAll(recipeSteps);
    }

    /**
     * Updates an existing step by its ID.
     * @param currentUser The current user.
     * @param recipeId The recipe ID.
     * @param stepId The step ID.
     * @param dto The updated data.
     * @return The updated step.
     */
    @Transactional
    public RecipeStepResponseDto updateRecipeStep(
            User currentUser, Long recipeId, Long stepId, RecipeStepUpdateDto dto
    ) {
        RecipeStep recipeStep = recipeStepRepository.findByIdAndRecipeId(stepId, recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("RecipeStep not found with id: " + stepId));

        if (!recipeStep.getRecipe().getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to update this RecipeStep");
        }

        if (dto.description() != null && !dto.description().isBlank()) recipeStep.setDescription(dto.description());

        return RecipeStepResponseDto.from(recipeStepRepository.save(recipeStep));
    }
}
