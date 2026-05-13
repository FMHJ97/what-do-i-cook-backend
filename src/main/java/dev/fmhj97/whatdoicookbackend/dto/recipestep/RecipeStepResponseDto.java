package dev.fmhj97.whatdoicookbackend.dto.recipestep;

import dev.fmhj97.whatdoicookbackend.entity.RecipeStep;

public record RecipeStepResponseDto(
        Long id,
        Long recipeId,
        Integer stepNumber,
        String description
) {

    /**
     * Converts a RecipeStep entity to a RecipeStepResponseDto.
     * @param recipeStep The entity to convert.
     * @return A RecipeStepResponseDto with the RecipeStep's data.
     */
    public static RecipeStepResponseDto from(RecipeStep recipeStep) {
        return new RecipeStepResponseDto(
                recipeStep.getId(),
                recipeStep.getRecipe().getId(),
                recipeStep.getStepNumber(),
                recipeStep.getDescription()
        );
    }

}
