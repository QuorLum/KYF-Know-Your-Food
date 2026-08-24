package com.kyf.knowyourfood.data.model

data class RecipeIngredient(
    val foodName: String,
    val quantityText: String
)

data class RecommendedRecipe(
    val id: String,
    val title: String,
    val category: String, // "Smoothie", "Salad", "Indian Sabzi / Stir-fry", "Warm Bowl", "Soup"
    val prepTimeMinutes: Int,
    val matchedIngredients: List<String>,
    val additionalIngredients: List<String>,
    val caloriesPerServing: Int,
    val proteinG: Double,
    val fiberG: Double,
    val healthHighlights: List<String>,
    val instructions: List<String>
)
