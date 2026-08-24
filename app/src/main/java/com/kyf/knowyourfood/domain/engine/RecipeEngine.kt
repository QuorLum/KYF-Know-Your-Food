package com.kyf.knowyourfood.domain.engine

import com.kyf.knowyourfood.data.model.PlateItemWithFood
import com.kyf.knowyourfood.data.model.RecommendedRecipe

object RecipeEngine {

    // Curated catalog of whole-food recipes
    private val masterRecipes = listOf(
        RecommendedRecipe(
            id = "REC_001",
            title = "Ayurvedic Golden Spinach & Chickpea Sauté",
            category = "Indian Sabzi / Stir-fry",
            prepTimeMinutes = 15,
            matchedIngredients = listOf("Spinach", "Chickpeas", "Tomato"),
            additionalIngredients = listOf("Turmeric (1/2 tsp)", "Cumin seeds", "Olive / Mustard oil", "Garlic clove"),
            caloriesPerServing = 240,
            proteinG = 12.5,
            fiberG = 9.0,
            healthHighlights = listOf("High Iron & Folate", "Plant Protein Powerhouse", "Gut Prebiotics"),
            instructions = listOf(
                "Heat 1 tsp oil in a pan and temper with cumin seeds and crushed garlic.",
                "Add diced tomatoes and sauté until soft and aromatic.",
                "Fold in boiled chickpeas and turmeric powder; stir gently for 3 minutes.",
                "Add fresh chopped spinach leaves and cover for 2 minutes until wilted.",
                "Season with a pinch of sea salt and lemon juice before serving warm."
            )
        ),
        RecommendedRecipe(
            id = "REC_002",
            title = "Antioxidant Green Power Smoothie",
            category = "Smoothie",
            prepTimeMinutes = 5,
            matchedIngredients = listOf("Spinach", "Apple", "Banana", "Chia Seeds"),
            additionalIngredients = listOf("Water or Plant Milk (200ml)", "Squeeze of Lime"),
            caloriesPerServing = 210,
            proteinG = 6.2,
            fiberG = 8.5,
            healthHighlights = listOf("Mega Vitamin C & K", "Omega-3 ALA Rich", "Sustained Energy"),
            instructions = listOf(
                "Rinse fresh spinach thoroughly and place in blender.",
                "Add chopped apple, ripe banana, and 1 tbsp chia seeds.",
                "Pour in 200ml chilled water or oat/almond milk.",
                "Blend on high for 60 seconds until creamy and silky smooth.",
                "Serve immediately over ice for maximum nutrient retention."
            )
        ),
        RecommendedRecipe(
            id = "REC_003",
            title = "Tropical Immunity Guava & Papaya Bowl",
            category = "Fruit Bowl",
            prepTimeMinutes = 8,
            matchedIngredients = listOf("Guava", "Papaya", "Pomegranate", "Chia Seeds"),
            additionalIngredients = listOf("Pinch of Black Salt / Chaat Masala", "Mint Leaves"),
            caloriesPerServing = 185,
            proteinG = 4.8,
            fiberG = 11.2,
            healthHighlights = listOf("400% DV Vitamin C", "Digestive Papain Enzymes", "High Lycopene"),
            instructions = listOf(
                "Cube fresh ripe papaya and slice crisp guava into bite-sized wedges.",
                "Toss fruit gently in a bowl with ruby pomegranate arils.",
                "Sprinkle with soaked chia seeds and a dash of refreshing mint.",
                "Finish with a touch of roasted cumin or black salt if desired."
            )
        ),
        RecommendedRecipe(
            id = "REC_004",
            title = "Mediterranean Quinoa & Roasted Veggie Bowl",
            category = "Warm Bowl",
            prepTimeMinutes = 20,
            matchedIngredients = listOf("Quinoa", "Broccoli", "Red Bell Pepper", "Avocado", "Pumpkin Seeds"),
            additionalIngredients = listOf("Extra Virgin Olive Oil", "Lemon Dressing", "Black Pepper"),
            caloriesPerServing = 380,
            proteinG = 14.2,
            fiberG = 11.5,
            healthHighlights = listOf("Complete Amino Acid Protein", "Healthy Monounsaturated Fats", "Zinc & Magnesium"),
            instructions = listOf(
                "Cook quinoa in lightly salted water until fluffy (approx 12 min).",
                "Lightly steam or roast broccoli florets and sliced red bell peppers.",
                "Assemble the base of warm quinoa topped with colorful veggies and sliced avocado.",
                "Garnish with roasted pumpkin seeds for a crunchy zinc boost.",
                "Drizzle with fresh lemon juice and cold-pressed olive oil."
            )
        ),
        RecommendedRecipe(
            id = "REC_005",
            title = "High-Protein Lentil & Sweet Potato Mash",
            category = "Warm Bowl",
            prepTimeMinutes = 18,
            matchedIngredients = listOf("Red Lentils", "Sweet Potato", "Carrot"),
            additionalIngredients = listOf("Ginger", "Ground Cumin", "Coriander"),
            caloriesPerServing = 290,
            proteinG = 13.8,
            fiberG = 10.2,
            healthHighlights = listOf("Beta-Carotene Reservoir", "Slow-Release Carbohydrates", "Low Glycemic Index"),
            instructions = listOf(
                "Simmer red lentils with peeled diced sweet potato and carrot until tender.",
                "Gently mash with a wooden spoon for a rustic stew-like texture.",
                "Season with freshly grated ginger, cumin, and sea salt.",
                "Serve warm as a comforting, fiber-dense healing meal."
            )
        ),
        RecommendedRecipe(
            id = "REC_006",
            title = "Fresh Crunch Hydration Salad",
            category = "Salad",
            prepTimeMinutes = 10,
            matchedIngredients = listOf("Cucumber", "Tomato", "Apple", "Walnuts"),
            additionalIngredients = listOf("Apple Cider Vinegar (1 tsp)", "Cracked Pepper"),
            caloriesPerServing = 160,
            proteinG = 4.0,
            fiberG = 5.2,
            healthHighlights = listOf("Deep Cellular Hydration", "Omega-3 Fatty Acids", "Polyphenol Rich"),
            instructions = listOf(
                "Dice crisp cucumber, vine tomatoes, and sweet apples into a mixing bowl.",
                "Crush English walnuts over the top for crunchy texture and brain-healthy fats.",
                "Toss with 1 tsp apple cider vinegar, a pinch of pink salt, and cracked pepper.",
                "Enjoy immediately as a light, refreshing revitalizing snack."
            )
        )
    )

    /**
     * Finds matching recipes based on raw produce names present on the user's Plate.
     */
    fun findMatchingRecipes(plateItems: List<PlateItemWithFood>): List<RecommendedRecipe> {
        if (plateItems.isEmpty()) return masterRecipes.take(3)

        val plateFoodNames = plateItems.map { it.foodItem.name.lowercase() }

        // Score recipes by how many matched ingredients they share with the plate
        val scoredRecipes = masterRecipes.map { recipe ->
            val matchCount = recipe.matchedIngredients.count { req ->
                plateFoodNames.any { it.contains(req.lowercase()) }
            }
            Pair(recipe, matchCount)
        }

        // Return recipes sorted by relevance
        val sorted = scoredRecipes.sortedByDescending { it.second }
        val matches = sorted.filter { it.second > 0 }.map { it.first }

        return if (matches.isNotEmpty()) matches else masterRecipes.take(3)
    }
}
