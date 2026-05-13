package dev.fmhj97.whatdoicookbackend.controller;

import dev.fmhj97.whatdoicookbackend.dto.recipeingredient.RecipeIngredientCreateDto;
import dev.fmhj97.whatdoicookbackend.dto.recipeingredient.RecipeIngredientResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.recipeingredient.RecipeIngredientUpdateDto;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.service.RecipeIngredientService;
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
@RequestMapping("/api/recipes/{recipeId}/ingredients")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "RecipeIngredient", description = "Endpoints related to recipe's ingredients")
public class RecipeIngredientController {

    private final RecipeIngredientService recipeIngredientService;

    /**
     * Constructor.
     * @param recipeIngredientService
     */
    public RecipeIngredientController(RecipeIngredientService recipeIngredientService) {
        this.recipeIngredientService = recipeIngredientService;
    }

    @GetMapping
    @Operation(summary = "Returns all ingredients for the given recipe")
    public ResponseEntity<List<RecipeIngredientResponseDto>> getRecipeIngredients(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                recipeIngredientService.getRecipeIngredients(currentUser, recipeId)
        );
    }

    @GetMapping("/{recipeIngredientId}")
    @Operation(summary = "Returns a single recipe ingredient by its ID")
    public ResponseEntity<RecipeIngredientResponseDto> getRecipeIngredientById(
            @PathVariable Long recipeId,
            @PathVariable Long recipeIngredientId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                recipeIngredientService.getRecipeIngredientById(currentUser, recipeId, recipeIngredientId)
        );
    }

    @PostMapping
    @Operation(summary = "Adds a new ingredient to the given recipe")
    public ResponseEntity<RecipeIngredientResponseDto> createRecipeIngredient(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid RecipeIngredientCreateDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipeIngredientService.addRecipeIngredient(currentUser, recipeId, dto));
    }

    @PatchMapping("/{recipeIngredientId}")
    @Operation(summary = "Updates an existing recipe ingredient by its ID")
    public ResponseEntity<RecipeIngredientResponseDto> updateRecipeIngredient(
            @PathVariable Long recipeId,
            @PathVariable Long recipeIngredientId,
            @RequestBody @Valid RecipeIngredientUpdateDto dto,
            @AuthenticationPrincipal User currentUser
    ) {
        return  ResponseEntity.ok(
                recipeIngredientService.updateRecipeIngredient(currentUser, recipeId, recipeIngredientId, dto)
        );
    }

    @DeleteMapping("/{recipeIngredientId}")
    @Operation(summary = "Deletes a recipe ingredient by its ID")
    public ResponseEntity<Void> deleteRecipeIngredient(
            @PathVariable Long recipeId,
            @PathVariable Long recipeIngredientId,
            @AuthenticationPrincipal User currentUser
    ) {
        recipeIngredientService.deleteRecipeIngredient(currentUser, recipeId, recipeIngredientId);
        return ResponseEntity.noContent().build();
    }
}
