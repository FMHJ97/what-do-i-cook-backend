package dev.fmhj97.whatdoicookbackend.dto.recipeingredient;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecipeIngredientCreateDto(

        @NotNull(message = "Ingredient ID is required")
        Long ingredientId,

        @Size(max = 10, message = "Quantity cannot exceed 10 characters")
        String quantity,

        @Size(max = 15, message = "Unit cannot exceed 15 characters")
        String unit
) {
}
