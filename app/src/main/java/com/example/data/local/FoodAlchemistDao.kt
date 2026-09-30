package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodAlchemistDao {
    // Kitchen Ingredients
    @Query("SELECT * FROM kitchen_ingredients ORDER BY dateAdded DESC")
    fun getAllKitchenIngredients(): Flow<List<KitchenIngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: KitchenIngredientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<KitchenIngredientEntity>)

    @Query("DELETE FROM kitchen_ingredients WHERE id = :id")
    suspend fun deleteIngredientById(id: String)

    @Query("DELETE FROM kitchen_ingredients")
    suspend fun clearKitchenIngredients()

    // Saved Recipes
    @Query("SELECT * FROM saved_recipes ORDER BY savedTimestamp DESC")
    fun getAllSavedRecipes(): Flow<List<SavedRecipeEntity>>

    @Query("SELECT * FROM saved_recipes WHERE id = :id LIMIT 1")
    suspend fun getSavedRecipeById(id: String): SavedRecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedRecipe(recipe: SavedRecipeEntity)

    @Query("DELETE FROM saved_recipes WHERE id = :id")
    suspend fun deleteSavedRecipeById(id: String)
}
