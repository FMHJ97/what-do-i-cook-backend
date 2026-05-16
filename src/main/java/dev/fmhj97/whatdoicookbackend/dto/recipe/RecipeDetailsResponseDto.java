package dev.fmhj97.whatdoicookbackend.dto.recipe;

import dev.fmhj97.whatdoicookbackend.dto.recipeingredient.RecipeIngredientSummaryDto;
import dev.fmhj97.whatdoicookbackend.dto.recipestep.RecipeStepSummaryDto;
import dev.fmhj97.whatdoicookbackend.entity.Recipe;

import java.util.List;

public record RecipeDetailsResponseDto(
        Long id,
        String title,
        String description,
        String foodType,
        String generatedBy,
        Integer servings,
        Integer prepTimeMin,
        Integer cookTimeMin,
        List<RecipeIngredientSummaryDto> ingredients,
        List<RecipeStepSummaryDto> steps
) {

    /**
     * Converts an Recipe entity to a RecipeDetailsResponseDto.
     * @param recipe The entity to convert.
     * @return A RecipeDetailsResponseDto with the recipe's data.
     */
    public static RecipeDetailsResponseDto from(Recipe recipe) {

        List<RecipeIngredientSummaryDto> ingredients = recipe.getRecipeIngredients().stream()
                .map(RecipeIngredientSummaryDto::from)
                .toList();

        List<RecipeStepSummaryDto> steps = recipe.getRecipeSteps().stream()
                .map(RecipeStepSummaryDto::from)
                .toList();

        return new RecipeDetailsResponseDto(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getFoodType().name(),
                recipe.getGeneratedBy().name(),
                recipe.getServings(),
                recipe.getPrepTimeMin(),
                recipe.getCookTimeMin(),
                ingredients,
                steps
        );
    }

}
