package dev.fmhj97.whatdoicookbackend.dto.recipestep;

import jakarta.validation.constraints.Size;

public record RecipeStepUpdateDto(

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description

) {

}
