package dev.fmhj97.whatdoicookbackend.entity;

import dev.fmhj97.whatdoicookbackend.entity.enums.FoodType;
import dev.fmhj97.whatdoicookbackend.entity.enums.Source;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipes", uniqueConstraints = {
            @UniqueConstraint(name = "uc_recipe_owner_title", columnNames = {"user_id", "title"})
})
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_recipe_user"))
    private User owner;

    @Column(nullable = false, length = 100)
    private String title;

    private String description;

    @Column(name = "food_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodType foodType;

    @Column(name = "generated_by", nullable = false)
    @Enumerated(EnumType.STRING)
    private Source generatedBy;

    @Column(name = "servings")
    private Integer servings;

    @Column(name = "prep_time_min")
    private Integer prepTimeMin;

    @Column(name = "cook_time_min")
    private Integer cookTimeMin;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RecipeStep> recipeSteps;

    /**
     * JPA Constructor
     */
    protected Recipe() {}

    /**
     * Constructor with args
     * @param title
     * @param owner
     * @param description
     * @param foodType
     * @param generatedBy
     * @param servings
     * @param prepTimeMin
     * @param cookTimeMin
     */
    public Recipe(String title, User owner, String description, FoodType foodType, Source generatedBy, Integer servings, Integer prepTimeMin, Integer cookTimeMin) {
        this.title = title;
        this.owner = owner;
        this.description = description;
        this.foodType = foodType;
        this.generatedBy = generatedBy;
        this.servings = servings;
        this.prepTimeMin = prepTimeMin;
        this.cookTimeMin = cookTimeMin;
    }

    // PRE

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }

    public Source getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(Source generatedBy) {
        this.generatedBy = generatedBy;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public Integer getPrepTimeMin() {
        return prepTimeMin;
    }

    public void setPrepTimeMin(Integer prepTimeMin) {
        this.prepTimeMin = prepTimeMin;
    }

    public Integer getCookTimeMin() {
        return cookTimeMin;
    }

    public void setCookTimeMin(Integer cookTimeMin) {
        this.cookTimeMin = cookTimeMin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public List<RecipeStep> getRecipeSteps() {
        return recipeSteps;
    }
}
