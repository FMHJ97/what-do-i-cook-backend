package dev.fmhj97.whatdoicookbackend.dto.recipe;

import dev.fmhj97.whatdoicookbackend.entity.enums.FoodType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecipeCreateDto(

        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
        String title,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotNull(message = "FoodType is required")
        FoodType foodType,

        @Min(value = 1, message = "Servings must be at least 1")
        Integer servings,

        @Min(value = 0, message = "Prep time cannot be negative")
        Integer prepTimeMin,

        @Min(value = 0, message = "Cook time cannot be negative")
        Integer cookTimeMin
) {
}
