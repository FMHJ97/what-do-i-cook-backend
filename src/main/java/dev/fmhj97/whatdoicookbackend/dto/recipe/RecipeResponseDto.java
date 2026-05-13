package dev.fmhj97.whatdoicookbackend.dto.recipe;

import dev.fmhj97.whatdoicookbackend.entity.Recipe;

import java.time.LocalDateTime;

public record RecipeResponseDto(
        Long id,
        String title,
        String description,
        String foodType,
        String generatedBy,
        Long ownerId,
        Integer servings,
        Integer prepTimeMin,
        Integer cookTimeMin,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /**
     * Converts an Recipe entity to a RecipeResponseDto.
     * @param recipe The entity to convert.
     * @return A RecipeResponseDto with the recipe's data.
     */
    public static RecipeResponseDto from(Recipe recipe) {
        return new RecipeResponseDto(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getFoodType().name(),
                recipe.getGeneratedBy().name(),
                recipe.getOwner().getId(),
                recipe.getServings(),
                recipe.getPrepTimeMin(),
                recipe.getCookTimeMin(),
                recipe.getCreatedAt(),
                recipe.getUpdatedAt()
        );
    }

}
