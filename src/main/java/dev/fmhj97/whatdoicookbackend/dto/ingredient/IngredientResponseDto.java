package dev.fmhj97.whatdoicookbackend.dto.ingredient;

import dev.fmhj97.whatdoicookbackend.entity.Ingredient;

public record IngredientResponseDto(
        Long id,
        String name
) {

    /**
     * Converts an Ingredient entity to an IngredientResponseDto.
     * @param ingredient The entity to convert.
     * @return An IngredientResponseDto with the ingredient's data.
     */
    public static IngredientResponseDto from(Ingredient ingredient) {
        return new IngredientResponseDto(
                ingredient.getId(),
                ingredient.getName()
        );
    }

}
