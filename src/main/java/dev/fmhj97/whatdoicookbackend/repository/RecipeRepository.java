package dev.fmhj97.whatdoicookbackend.repository;

import dev.fmhj97.whatdoicookbackend.entity.Recipe;
import dev.fmhj97.whatdoicookbackend.entity.enums.FoodType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    // Fetches only the ingredient collection. Steps are resolved separately (lazily inside the
    // service's read-only transaction) to avoid a cartesian product between the two collections,
    // which would duplicate each ingredient once per step.
    @Query("SELECT r FROM Recipe r " +
            "LEFT JOIN FETCH r.recipeIngredients ri " +
            "LEFT JOIN FETCH ri.ingredient " +
            "WHERE r.id = :id AND r.owner.id = :ownerId")
    Optional<Recipe> findByIdAndOwnerIdWithIngredients(@Param("id") Long id, @Param("ownerId") Long ownerId);

    // Pessimistic row lock: serializes concurrent mutations on the same recipe (e.g. step numbering).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Recipe r WHERE r.id = :id")
    Optional<Recipe> findByIdWithLock(@Param("id") Long id);

    @Query(value = "SELECT * FROM recipes WHERE user_id = :ownerId ORDER BY RANDOM() LIMIT 1",
            nativeQuery = true)
    Optional<Recipe> findRandomByOwnerId(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT * FROM recipes WHERE user_id = :ownerId AND food_type = :foodType ORDER BY RANDOM() LIMIT 1",
            nativeQuery = true)
    Optional<Recipe> findRandomByOwnerIdAndFoodType(
            @Param("ownerId") Long ownerId,
            @Param("foodType") String foodType); // Native queries don't recognise Enums (JPQL does it).

    @Query(value = "SELECT r.* FROM recipes r " +
            "JOIN recipe_ingredient ri ON ri.recipe_id = r.id " +
            "WHERE r.user_id = :ownerId " +
            "AND ri.ingredient_id IN (:ingredientIds) " +
            "GROUP BY r.id " +
            "ORDER BY COUNT(ri.ingredient_id) DESC", nativeQuery = true)
    List<Recipe> findByOwnerIdAndIngredientIds(
            @Param("ownerId") Long ownerId,
            @Param("ingredientIds") List<Long> ingredientIds
    );

    @Query(value = "SELECT r.* FROM recipes r " +
            "JOIN recipe_ingredient ri ON ri.recipe_id = r.id " +
            "WHERE r.user_id = :ownerId " +
            "AND ri.ingredient_id IN (:ingredientIds) " +
            "AND r.food_type = :foodType " +
            "GROUP BY r.id " +
            "ORDER BY COUNT(ri.ingredient_id) DESC", nativeQuery = true)
    List<Recipe> findByOwnerIdAndIngredientIdsAndFoodType(
            @Param("ownerId") Long ownerId,
            @Param("ingredientIds") List<Long> ingredientIds,
            @Param("foodType") String foodType
    );
}
