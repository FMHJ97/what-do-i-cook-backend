package dev.fmhj97.whatdoicookbackend.repository;

import dev.fmhj97.whatdoicookbackend.entity.Recipe;
import dev.fmhj97.whatdoicookbackend.entity.enums.FoodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByOwnerId(Long ownerId);

    List<Recipe> findByOwnerIdAndTitleContainingIgnoreCase(Long ownerId, String title);

    List<Recipe> findByOwnerIdAndFoodType(Long ownerId, FoodType foodType);

    List<Recipe> findByOwnerIdAndTitleContainingIgnoreCaseAndFoodType(Long ownerId, String title, FoodType foodType);

    Boolean existsByOwnerIdAndTitleIgnoreCase(Long ownerId, String title);

    // Uses two separate JOIN FETCH (ingredients and steps) to load all data in one query.
    // recipeSteps must be a Set in the Recipe entity to avoid MultipleBagFetchException.
    @Query("SELECT r FROM Recipe r " +
            "LEFT JOIN FETCH r.recipeIngredients ri " +
            "LEFT JOIN FETCH ri.ingredient " +
            "LEFT JOIN FETCH r.recipeSteps " +
            "WHERE r.id = :id AND r.owner.id = :ownerId")
    Optional<Recipe> findByIdAndOwnerIdWithDetails(@Param("id") Long id, @Param("ownerId") Long ownerId);

    @Query(value = "SELECT * FROM recipes WHERE user_id = :ownerId ORDER BY RANDOM() LIMIT 1",
            nativeQuery = true)
    Optional<Recipe> findRandomByOwnerId(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT * FROM recipes WHERE user_id = :ownerId AND food_type = :foodType ORDER BY RANDOM() LIMIT 1",
            nativeQuery = true)
    Optional<Recipe> findRandomByOwnerIdAndFoodType(
            @Param("ownerId") Long ownerId,
            @Param("foodType") String foodType); // Native queries don't recognise Enums (JPQL does it).
}
