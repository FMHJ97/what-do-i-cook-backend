package dev.fmhj97.whatdoicookbackend.service;

import dev.fmhj97.whatdoicookbackend.dto.ingredient.IngredientCreateDto;
import dev.fmhj97.whatdoicookbackend.dto.ingredient.IngredientResponseDto;
import dev.fmhj97.whatdoicookbackend.dto.ingredient.IngredientUpdateDto;
import dev.fmhj97.whatdoicookbackend.entity.Ingredient;
import dev.fmhj97.whatdoicookbackend.exception.DuplicateResourceException;
import dev.fmhj97.whatdoicookbackend.exception.ResourceNotFoundException;
import dev.fmhj97.whatdoicookbackend.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    /**
     * Constructor with args.
     * @param ingredientRepository
     */
    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Returns an ingredient by the given ID.
     * @param id The ingredient ID.
     * @return The ingredient data.
     */
    @Transactional(readOnly = true)
    public IngredientResponseDto getIngredientById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));

        return IngredientResponseDto.from(ingredient);
    }

    /**
     * Returns an ingredient by the given name.
     * @param name The ingredient name.
     * @return The ingredient data.
     */
    @Transactional(readOnly = true)
    public IngredientResponseDto getIngredientByName(String name) {
        Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with name: " + name));

        return IngredientResponseDto.from(ingredient);
    }

    /**
     * Returns a list of all ingredients. If a name is provided, filters by name.
     * @param name Optional filter by ingredient name.
     * @return List of ingredients.
     */
    @Transactional(readOnly = true)
    public List<IngredientResponseDto> getIngredients(String name) {
        if (name != null && !name.isBlank()) {
            return ingredientRepository.findByNameContainingIgnoreCase(name).stream()
                    .map(IngredientResponseDto::from)
                    .toList();
        } else {
            return ingredientRepository.findAll().stream()
                    .map(IngredientResponseDto::from)
                    .toList();
        }
    }

    /**
     * Deletes an ingredient by the given ID.
     * @param id The ingredient ID.
     */
    @Transactional
    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) throw new ResourceNotFoundException("Ingredient not found with id: " + id);

        ingredientRepository.deleteById(id);
    }

    /**
     * Creates a new ingredient.
     * @param dto The ingredient data.
     * @return The created ingredient.
     */
    @Transactional
    public IngredientResponseDto addIngredient(IngredientCreateDto dto) {
        if (ingredientRepository.existsByNameIgnoreCase(dto.name()))
            throw new DuplicateResourceException("Name already exists: " + dto.name());

        Ingredient ingredient = new Ingredient(dto.name());

        return IngredientResponseDto.from(ingredientRepository.save(ingredient));
    }

    /**
     * Updates an existing ingredient by the given ID.
     * @param id The ingredient ID.
     * @param dto The updated data.
     * @return The updated ingredient.
     */
    @Transactional
    public IngredientResponseDto updateIngredient(Long id, IngredientUpdateDto dto) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));

        if (dto.name() != null && !dto.name().isBlank()) {
            Optional<Ingredient> optionalIngredient = ingredientRepository.findByNameIgnoreCase(dto.name());

            if (optionalIngredient.isPresent() && !optionalIngredient.get().getId().equals(id))
                throw new DuplicateResourceException("Name already exists: " + dto.name());
        }

        ingredient.setName(dto.name());

        return IngredientResponseDto.from(ingredientRepository.save(ingredient));
    }

}
