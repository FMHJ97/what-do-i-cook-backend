package dev.fmhj97.whatdoicookbackend.dto.recipeingredient;

import dev.fmhj97.whatdoicookbackend.entity.RecipeIngredient;

public record RecipeIngredientSummaryDto(
        Long id,
        Long ingredientId,
        String ingredientName,
        String quantity,
        String unit
) {

    /**
     * Converts a RecipeIngredient entity to a RecipeIngredientSummaryDto.
     * @param recipeIngredient The entity to convert.
     * @return A RecipeIngredientSummaryDto with the RecipeIngredient's data.
     */
    public static RecipeIngredientSummaryDto from(RecipeIngredient recipeIngredient) {
        return new RecipeIngredientSummaryDto(
                recipeIngredient.getId(),
                recipeIngredient.getIngredient().getId(),
                recipeIngredient.getIngredient().getName(),
                recipeIngredient.getQuantity(),
                recipeIngredient.getUnit()
        );
    }

}
