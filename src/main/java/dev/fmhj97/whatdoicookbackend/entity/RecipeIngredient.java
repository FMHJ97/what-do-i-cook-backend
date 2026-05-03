package dev.fmhj97.whatdoicookbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "recipe_ingredient", uniqueConstraints = {
        @UniqueConstraint(name = "uc_recipe_ingredient", columnNames = {"recipe_id", "ingredient_id"})
})
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false, foreignKey = @ForeignKey(name = "fk_recipe_ingredient_recipe"))
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_recipe_ingredient_ingredient"))
    private Ingredient ingredient;

    @Column(length = 10)
    private String quantity;

    @Column(length = 15)
    private String unit;

    /**
     * JPA Constructor
     */
    protected RecipeIngredient() {}

    /**
     * Constructor with args
     * @param recipe
     * @param ingredient
     * @param quantity
     * @param unit
     */
    public RecipeIngredient(Recipe recipe, Ingredient ingredient, String quantity, String unit) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
