package dev.fmhj97.whatdoicookbackend.controller;

import dev.fmhj97.whatdoicookbackend.dto.recipestep.RecipeStepCreateDto;
import dev.fmhj97.whatdoicookbackend.dto.recipestep.RecipeStepResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.recipestep.RecipeStepUpdateDto;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.service.RecipeStepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes/{recipeId}/steps")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "RecipeStep", description = "Endpoints related to recipe steps")
public class RecipeStepController {

    private final RecipeStepService recipeStepService;

    public RecipeStepController(RecipeStepService recipeStepService) {
        this.recipeStepService = recipeStepService;
    }

    @GetMapping
    @Operation(summary = "Returns all steps for the given recipe, ordered by step number")
    public ResponseEntity<List<RecipeStepResponseDto>> getRecipeSteps(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                recipeStepService.getRecipeSteps(currentUser, recipeId)
        );
    }

    @GetMapping("/{stepId}")
    @Operation(summary = "Returns a single step for the given recipe")
    public ResponseEntity<RecipeStepResponseDto> getRecipeStepById(
            @PathVariable Long recipeId,
            @PathVariable Long stepId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                recipeStepService.getRecipeStepById(currentUser, recipeId, stepId)
        );
    }

    @PostMapping
    @Operation(summary = "Adds a new step to the given recipe")
    public ResponseEntity<RecipeStepResponseDto> createRecipeStep(
            @PathVariable Long recipeId,
            @RequestBody @Valid RecipeStepCreateDto dto,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipeStepService.addRecipeStep(recipeId, currentUser, dto));
    }

    @PatchMapping("/{stepId}")
    @Operation(summary = "Updates an existing step by its ID")
    public ResponseEntity<RecipeStepResponseDto> updateRecipeStep(
            @PathVariable Long stepId,
            @PathVariable Long recipeId,
            @RequestBody @Valid RecipeStepUpdateDto dto,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                recipeStepService.updateRecipeStep(currentUser, recipeId, stepId, dto)
        );
    }

    @DeleteMapping("/{stepId}")
    @Operation(summary = "Deletes a step by its ID and renumbers the remaining steps")
    public ResponseEntity<Void> deleteRecipeStep(
            @PathVariable Long recipeId,
            @PathVariable Long stepId,
            @AuthenticationPrincipal User currentUser
    ) {
        recipeStepService.deleteRecipeStep(recipeId, currentUser, stepId);
        return ResponseEntity.noContent().build();
    }
}
