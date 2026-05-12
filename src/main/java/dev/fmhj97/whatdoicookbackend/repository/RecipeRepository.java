package dev.fmhj97.whatdoicookbackend.repository;

import dev.fmhj97.whatdoicookbackend.entity.Recipe;
import dev.fmhj97.whatdoicookbackend.entity.enums.FoodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByOwnerId(Long ownerId);

    List<Recipe> findByOwnerIdAndTitleContainingIgnoreCase(Long ownerId, String title);

    List<Recipe> findByOwnerIdAndFoodType(Long ownerId, FoodType foodType);

    List<Recipe> findByOwnerIdAndTitleContainingIgnoreCaseAndFoodType(Long ownerId, String title, FoodType foodType);

    Boolean existsByOwnerIdAndTitleIgnoreCase(Long ownerId, String title);
}
