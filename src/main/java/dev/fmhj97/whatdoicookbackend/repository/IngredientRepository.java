package dev.fmhj97.whatdoicookbackend.repository;

import dev.fmhj97.whatdoicookbackend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findByNameContainingIgnoreCase(String name);

    Optional<Ingredient> findByNameIgnoreCase(String name);

    Boolean existsByNameIgnoreCase(String name);
}
