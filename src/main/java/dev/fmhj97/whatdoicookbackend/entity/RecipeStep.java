package dev.fmhj97.whatdoicookbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "recipe_steps", uniqueConstraints = {
        @UniqueConstraint(name = "uc_recipe_step_number", columnNames = {"recipe_id", "step_number"})
})
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(nullable = false, length = 1000)
    private String description;

    /**
     * JPA Constructor
     */
    protected RecipeStep() {}

    /**
     * Constructor with args
     * @param recipe
     * @param stepNumber
     * @param description
     */
    public RecipeStep(Recipe recipe, Integer stepNumber, String description) {
        this.recipe = recipe;
        this.stepNumber = stepNumber;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
