package dev.fmhj97.whatdoicookbackend.config;

import dev.fmhj97.whatdoicookbackend.entity.Ingredient;
import dev.fmhj97.whatdoicookbackend.entity.User;
import dev.fmhj97.whatdoicookbackend.entity.enums.Role;
import dev.fmhj97.whatdoicookbackend.repository.IngredientRepository;
import dev.fmhj97.whatdoicookbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String username;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    private static final List<String> INITIAL_INGREDIENTS = List.of(
            // Vegetables
            "Tomato", "Onion", "Garlic", "Potato", "Carrot", "Zucchini", "Eggplant",
            "Bell pepper", "Spinach", "Lettuce", "Broccoli", "Cauliflower", "Mushroom",
            "Celery", "Leek", "Asparagus", "Artichoke", "Cucumber", "Peas", "Green beans",
            "Corn", "Pumpkin", "Sweet potato", "Beetroot", "Cabbage",

            // Fruits
            "Lemon", "Orange", "Apple", "Banana", "Tomato cherry", "Avocado", "Lime",

            // Meat & Fish
            "Chicken breast", "Chicken thigh", "Ground beef", "Pork loin", "Bacon",
            "Lamb", "Turkey", "Salmon", "Tuna", "Cod", "Shrimp", "Squid", "Mussels",
            "Clams", "Sardine",

            // Dairy & Eggs
            "Egg", "Milk", "Butter", "Heavy cream", "Parmesan cheese", "Mozzarella",
            "Cheddar cheese", "Greek yogurt", "Feta cheese",

            // Grains & Pasta
            "Rice", "Pasta", "Spaghetti", "Bread", "Flour", "Breadcrumbs", "Oats",
            "Couscous", "Quinoa", "Lentils", "Chickpeas", "Black beans",

            // Oils & Sauces
            "Olive oil", "Sunflower oil", "Soy sauce", "Tomato sauce", "Vinegar",
            "Balsamic vinegar", "Mustard", "Mayonnaise", "Ketchup", "Hot sauce",
            "Worcestershire sauce", "Coconut milk",

            // Spices & Herbs
            "Salt", "Black pepper", "Paprika", "Cumin", "Turmeric", "Oregano",
            "Basil", "Thyme", "Rosemary", "Parsley", "Coriander", "Chili flakes",
            "Cinnamon", "Nutmeg", "Bay leaf", "Ginger", "Curry powder", "Saffron",

            // Others
            "Sugar", "Brown sugar", "Honey", "Baking powder", "Yeast", "Vegetable broth",
            "Chicken broth", "White wine", "Red wine"
    );
    private final IngredientRepository ingredientRepository;

    /**
     * Constructor
     * @param userRepository
     * @param passwordEncoder
     */
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           IngredientRepository ingredientRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Creates a default ADMIN user on application startup if none exists.
     * @param args incoming application arguments
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {

        // Checks if there is an existing ADMIN with the given credentials.
        if (!userRepository.existsByUsername(username) && !userRepository.existsByEmail(email)) {

            // New ADMIN user.
            User admin = new User(
                    username,
                    email,
                    passwordEncoder.encode(password),
                    Role.ADMIN
            );

            // Saves the ADMIN user.
            userRepository.save(admin);
        }

        // --- Add Ingredients ---

        // Seeds initial ingredients if the table is empty.
        if (ingredientRepository.count() == 0) {
            List<Ingredient> ingredients = INITIAL_INGREDIENTS.stream()
                    .map(Ingredient::new)
                    .toList();
            ingredientRepository.saveAll(ingredients);
        }
    }
}
