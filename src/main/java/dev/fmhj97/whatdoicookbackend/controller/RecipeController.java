package dev.fmhj97.whatdoicookbackend.controller;

import dev.fmhj97.whatdoicookbackend.dto.recipe.RecipeCreateDto;
import dev.fmhj97.whatdoicookbackend.dto.recipe.RecipeDetailsResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.recipe.RecipeResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.recipe.RecipeUpdateDto;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.entity.enums.FoodType;
import dev.fmhj97.whatdoicookbackend.service.RecipeService;
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
@RequestMapping("/api/recipes")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Recipe", description = "Endpoints related to recipes")
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * Constructor.
     * @param recipeService
     */
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Returns a list of all recipes owned by the current user.
     * If a title and/or foodType are provided, filters by them.
     * @param title Optional filter by recipe title.
     * @param foodType Optional filter by recipe foodType.
     * @param currentUser The current user.
     * @return List of recipes.
     */
    @GetMapping
    @Operation(summary =
            "Returns a list of all recipes owned by the current user. If a title and/or foodType are provided, filters by them"
    )
    public ResponseEntity<List<RecipeResponseDto>> getMyRecipes(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) FoodType foodType,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                recipeService.getMyRecipes(currentUser, title, foodType)
        );
    }

    /**
     * Returns a random recipe owned by the current user.
     * If a foodType is provided, filters by it.
     * @param foodType Optional filter by food type.
     * @param currentUser The current user.
     * @return A random recipe with full details.
     */
    @GetMapping("/random")
    @Operation(summary = "Returns a random recipe. If a foodType is provided, filters by it")
    public ResponseEntity<RecipeDetailsResponseDto> getRandomRecipe(
            @RequestParam(required = false) FoodType foodType,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                recipeService.getRandomRecipe(currentUser, foodType)
        );
    }

    /**
     * Returns a single recipe by its ID (owned by the current user).
     * @param id The recipe ID.
     * @param currentUser The current user.
     * @return The recipe data.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Returns a single recipe by its ID")
    public ResponseEntity<RecipeResponseDto> getRecipeById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                recipeService.getRecipeById(id, currentUser)
        );
    }

    /**
     * Returns the full details of a recipe, including its ingredients and steps.
     * @param id The recipe ID.
     * @param currentUser The current user.
     * @return The recipe details.
     */
    @GetMapping("/{id}/details")
    @Operation(summary = "Returns the full details of a recipe, including its ingredients and steps")
    public ResponseEntity<RecipeDetailsResponseDto> getRecipeDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                recipeService.getRecipeDetails(id, currentUser)
        );
    }

    /**
     * Creates a new recipe for the current user.
     * @param dto The recipe data.
     * @param currentUser The current user.
     * @return The created recipe.
     */
    @PostMapping
    @Operation(summary = "Creates a new recipe for the current user")
    public ResponseEntity<RecipeResponseDto> createRecipe(
            @RequestBody @Valid RecipeCreateDto dto,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipeService.createRecipe(currentUser, dto));
    }

    /**
     * Updates an existing recipe by its ID (owned by the current user).
     * @param id The recipe ID.
     * @param dto The updated data.
     * @param currentUser The current user.
     * @return The updated recipe.
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Updates an existing recipe by its ID")
    public ResponseEntity<RecipeResponseDto> updateRecipe(
            @PathVariable Long id,
            @RequestBody @Valid RecipeUpdateDto dto,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                recipeService.updateRecipe(currentUser, id, dto)
        );
    }

    /**
     * Deletes a recipe by its ID (owned by the current user).
     * @param id The recipe ID.
     * @param currentUser The current user.
     * @return No content.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a recipe by its ID")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        recipeService.deleteRecipe(currentUser, id);
        return ResponseEntity.noContent().build();
    }
}
