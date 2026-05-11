package dev.fmhj97.whatdoicookbackend.controller;

import dev.fmhj97.whatdoicookbackend.dto.ingredient.IngredientCreateDto;
import dev.fmhj97.whatdoicookbackend.dto.ingredient.IngredientResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.ingredient.IngredientUpdateDto;
import dev.fmhj97.whatdoicookbackend.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredient")
public class IngredientController {

    private final IngredientService ingredientService;

    /**
     * Constructor with args.
     * @param ingredientService
     */
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    /**
     * Returns a list of ingredients. If a name is provided, filters by name.
     * @param name Optional filter by ingredient name.
     * @return List of ingredients.
     */
    @GetMapping
    public ResponseEntity<List<IngredientResponseDto>> getIngredients(
            @RequestParam(required = false) String name
    ) {
        return ResponseEntity.ok(ingredientService.getIngredients(name));
    }

    /**
     * Returns a single ingredient by its ID.
     * @param id The ingredient ID.
     * @return The ingredient data.
     */
    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponseDto> getIngredientById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ingredientService.getIngredientById(id));
    }

    /**
     * Creates a new ingredient.
     * @param dto The ingredient data.
     * @return The created ingredient.
     */
    @PostMapping
    public ResponseEntity<IngredientResponseDto> addIngredient(
            @RequestBody @Valid IngredientCreateDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ingredientService.addIngredient(dto));
    }

    /**
     * Updates an existing ingredient by its ID.
     * @param id The ingredient ID.
     * @param dto The updated data.
     * @return The updated ingredient.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<IngredientResponseDto> updateIngredient(
            @PathVariable Long id,
            @RequestBody IngredientUpdateDto dto
    ) {
        return ResponseEntity.ok(ingredientService.updateIngredient(id, dto));
    }

    /**
     * Deletes an ingredient by its ID.
     * @param id the ingredient ID.
     * @return no content.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteIngredient(
            @PathVariable Long id
    ) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
