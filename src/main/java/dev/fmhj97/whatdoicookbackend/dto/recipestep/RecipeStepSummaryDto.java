package dev.fmhj97.whatdoicookbackend.dto.recipestep;

import dev.fmhj97.whatdoicookbackend.entity.RecipeStep;

public record RecipeStepSummaryDto(
        Long id,
        Integer stepNumber,
        String description
) {

    /**
     * Converts a RecipeStep entity to a RecipeStepSummaryDto.
     * @param recipeStep The entity to convert.
     * @return A RecipeStepSummaryDto with the RecipeStep's data.
     */
    public static RecipeStepSummaryDto from(RecipeStep recipeStep) {
        return new RecipeStepSummaryDto(
                recipeStep.getId(),
                recipeStep.getStepNumber(),
                recipeStep.getDescription()
        );
    }

}
