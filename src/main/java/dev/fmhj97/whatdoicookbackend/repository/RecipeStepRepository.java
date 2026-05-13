package dev.fmhj97.whatdoicookbackend.repository;

import dev.fmhj97.whatdoicookbackend.entity.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {

    List<RecipeStep> findByRecipeIdOrderByStepNumberAsc(Long recipeId);

    @Query("SELECT COALESCE(MAX(rs.stepNumber), 0) FROM RecipeStep rs " +
            "WHERE rs.recipe.id = :recipeId")
    Integer findMaxStepNumberByRecipeId(@Param("recipeId") Long recipeId);

}
