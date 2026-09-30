package com.example.data.model

data class RecipeIngredient(
    val name: String,
    val amount: String,
    val emoji: String = "🧂",
    val isOwned: Boolean = true
)

data class RecipeStep(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val tip: String? = null,
    val durationMinutes: Int? = null,
    val imageType: String = "pan" // "prep", "pan", "finish"
)

data class Recipe(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val cookTimeMinutes: Int,
    val difficulty: String, // "Easy", "Medium", "Chef Level"
    val servings: Int = 2,
    val imageDrawableName: String = "img_spicy_skillet",
    val goalTag: String = "Unique",
    val ingredients: List<RecipeIngredient>,
    val steps: List<RecipeStep>,
    val smartSubstitutions: List<String> = emptyList(),
    val chefTips: List<String> = emptyList(),
    val calories: Int = 340,
    val proteinGrams: Int = 18,
    val carbsGrams: Int = 32,
    val fatGrams: Int = 14,
    val isFavorite: Boolean = false,
    val matchPercentage: Int = 100
)
