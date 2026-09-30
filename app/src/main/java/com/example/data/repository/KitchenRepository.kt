package com.example.data.repository

import com.example.data.local.FoodAlchemistDao
import com.example.data.local.KitchenIngredientEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class KitchenRepository(private val dao: FoodAlchemistDao) {

    val allIngredients: Flow<List<KitchenIngredientEntity>> = dao.getAllKitchenIngredients()

    suspend fun initializeDefaultPantryIfEmpty() {
        val existing = dao.getAllKitchenIngredients().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val defaults = listOf(
                KitchenIngredientEntity("potato", "Potato", "🥔", "Produce", "2 medium"),
                KitchenIngredientEntity("tomato", "Tomato", "🍅", "Produce", "2 medium"),
                KitchenIngredientEntity("onion", "Onion", "🧅", "Produce", "1 medium"),
                KitchenIngredientEntity("egg", "Egg", "🥚", "Dairy & Eggs", "3 eggs"),
                KitchenIngredientEntity("milk", "Milk", "🥛", "Dairy & Eggs", "1 bottle"),
                KitchenIngredientEntity("cheese", "Cheese", "🧀", "Dairy & Eggs", "1 block"),
                KitchenIngredientEntity("garlic", "Garlic", "🧄", "Produce", "3 cloves"),
                KitchenIngredientEntity("apple", "Apple", "🍎", "Produce", "2 apples"),
                KitchenIngredientEntity("chili", "Chili Pepper", "🌶️", "Produce", "2 peppers")
            )
            dao.insertIngredients(defaults)
        }
    }

    suspend fun addIngredient(item: KitchenIngredientEntity) {
        dao.insertIngredient(item)
    }

    suspend fun removeIngredient(id: String) {
        dao.deleteIngredientById(id)
    }

    suspend fun clearAll() {
        dao.clearKitchenIngredients()
    }
}
