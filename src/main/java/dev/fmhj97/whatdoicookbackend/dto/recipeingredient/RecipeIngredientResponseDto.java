package dev.fmhj97.whatdoicookbackend.dto.recipeingredient;

import dev.fmhj97.whatdoicookbackend.entity.RecipeIngredient;

public record RecipeIngredientResponseDto(
        Long id,
        Long recipeId,
        Long ingredientId,
        String quantity,
        String unit
) {

    /**
     * Converts a RecipeIngredient entity to a RecipeIngredientResponseDto.
     * @param recipeIngredient The entity to convert.
     * @return A RecipeIngredientResponseDto with the RecipeIngredient's data.
     */
    public static RecipeIngredientResponseDto from(RecipeIngredient recipeIngredient) {
        return new RecipeIngredientResponseDto(
                recipeIngredient.getId(),
                recipeIngredient.getRecipe().getId(),
                recipeIngredient.getIngredient().getId(),
                recipeIngredient.getQuantity(),
                recipeIngredient.getUnit()
        );
    }

}
