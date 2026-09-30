package com.example.data.model

data class Ingredient(
    val id: String,
    val name: String,
    val emoji: String,
    val category: String = "Produce",
    val defaultAmount: String = "1 medium",
    val isSelected: Boolean = false
)

object DefaultIngredients {
    val commonIngredients = listOf(
        Ingredient("potato", "Potato", "🥔", "Produce", "2 medium"),
        Ingredient("tomato", "Tomato", "🍅", "Produce", "2 medium"),
        Ingredient("onion", "Onion", "🧅", "Produce", "1 medium"),
        Ingredient("egg", "Egg", "🥚", "Dairy & Eggs", "3 eggs"),
        Ingredient("milk", "Milk", "🥛", "Dairy & Eggs", "1 cup"),
        Ingredient("cheese", "Cheese", "🧀", "Dairy & Eggs", "100g"),
        Ingredient("garlic", "Garlic", "🧄", "Produce", "3 cloves"),
        Ingredient("apple", "Apple", "🍎", "Produce", "1 fruit"),
        Ingredient("chili", "Chili Pepper", "🌶️", "Produce", "1 pepper"),
        Ingredient("chicken", "Chicken", "🍗", "Meat & Protein", "200g"),
        Ingredient("rice", "Rice", "🍚", "Pantry", "1 cup"),
        Ingredient("pasta", "Pasta", "🍝", "Pantry", "200g"),
        Ingredient("bread", "Bread", "🍞", "Pantry", "2 slices"),
        Ingredient("butter", "Butter", "🧈", "Dairy & Eggs", "2 tbsp"),
        Ingredient("olive_oil", "Olive Oil", "🫒", "Pantry", "1 tbsp"),
        Ingredient("lemon", "Lemon", "🍋", "Produce", "1 fruit"),
        Ingredient("bell_pepper", "Bell Pepper", "🫑", "Produce", "1 pepper"),
        Ingredient("mushroom", "Mushroom", "🍄", "Produce", "150g"),
        Ingredient("carrot", "Carrot", "🥕", "Produce", "1 large"),
        Ingredient("avocado", "Avocado", "🥑", "Produce", "1 fruit")
    )
}
