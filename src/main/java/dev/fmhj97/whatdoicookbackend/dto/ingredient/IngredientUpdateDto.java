package dev.fmhj97.whatdoicookbackend.dto.ingredient;

import jakarta.validation.constraints.Size;

public record IngredientUpdateDto(

        @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
        String name
) {
}
