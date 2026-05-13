package dev.fmhj97.whatdoicookbackend.repository;

import dev.fmhj97.whatdoicookbackend.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    List<RecipeIngredient> findByRecipeId(Long recipeId);

    Boolean existsByRecipeIdAndIngredientId(Long recipeId, Long ingredientId);

    Optional<RecipeIngredient> findByIdAndRecipeId(Long id, Long recipeId);
}
