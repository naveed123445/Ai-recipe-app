package com.example.data.repository

import com.example.data.local.FoodAlchemistDao
import com.example.data.local.SavedRecipeEntity
import com.example.data.model.Recipe
import com.example.data.model.RecipeIngredient
import com.example.data.model.RecipeStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class RecipeRepository(private val dao: FoodAlchemistDao) {

    val savedRecipes: Flow<List<Recipe>> = dao.getAllSavedRecipes().map { entities ->
        entities.map { it.toRecipe() }
    }

    suspend fun saveRecipe(recipe: Recipe) {
        dao.insertSavedRecipe(recipe.toEntity())
    }

    suspend fun removeRecipe(id: String) {
        dao.deleteSavedRecipeById(id)
    }

    suspend fun isSaved(id: String): Boolean {
        return dao.getSavedRecipeById(id) != null
    }

    private fun Recipe.toEntity(): SavedRecipeEntity {
        val ingArray = JSONArray().apply {
            ingredients.forEach {
                put(JSONObject().apply {
                    put("name", it.name)
                    put("amount", it.amount)
                    put("emoji", it.emoji)
                    put("isOwned", it.isOwned)
                })
            }
        }

        val stepArray = JSONArray().apply {
            steps.forEach {
                put(JSONObject().apply {
                    put("stepNumber", it.stepNumber)
                    put("title", it.title)
                    put("instruction", it.instruction)
                    put("tip", it.tip)
                    put("durationMinutes", it.durationMinutes)
                })
            }
        }

        val subArray = JSONArray().apply {
            smartSubstitutions.forEach { put(it) }
        }

        return SavedRecipeEntity(
            id = id,
            title = title,
            subtitle = subtitle,
            description = description,
            cookTimeMinutes = cookTimeMinutes,
            difficulty = difficulty,
            servings = servings,
            imageDrawableName = imageDrawableName,
            goalTag = goalTag,
            ingredientsJson = ingArray.toString(),
            stepsJson = stepArray.toString(),
            substitutionsJson = subArray.toString(),
            calories = calories,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams,
            isFavorite = true
        )
    }

    private fun SavedRecipeEntity.toRecipe(): Recipe {
        val ings = mutableListOf<RecipeIngredient>()
        try {
            val jsonArray = JSONArray(ingredientsJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                ings.add(
                    RecipeIngredient(
                        name = obj.getString("name"),
                        amount = obj.getString("amount"),
                        emoji = obj.optString("emoji", "🧂"),
                        isOwned = obj.optBoolean("isOwned", true)
                    )
                )
            }
        } catch (_: Exception) {}

        val stepsList = mutableListOf<RecipeStep>()
        try {
            val jsonArray = JSONArray(stepsJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                stepsList.add(
                    RecipeStep(
                        stepNumber = obj.getInt("stepNumber"),
                        title = obj.getString("title"),
                        instruction = obj.getString("instruction"),
                        tip = if (obj.has("tip") && !obj.isNull("tip")) obj.getString("tip") else null,
                        durationMinutes = if (obj.has("durationMinutes") && !obj.isNull("durationMinutes")) obj.getInt("durationMinutes") else null
                    )
                )
            }
        } catch (_: Exception) {}

        val subs = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(substitutionsJson)
            for (i in 0 until jsonArray.length()) {
                subs.add(jsonArray.getString(i))
            }
        } catch (_: Exception) {}

        return Recipe(
            id = id,
            title = title,
            subtitle = subtitle,
            description = description,
            cookTimeMinutes = cookTimeMinutes,
            difficulty = difficulty,
            servings = servings,
            imageDrawableName = imageDrawableName,
            goalTag = goalTag,
            ingredients = ings,
            steps = stepsList,
            smartSubstitutions = subs,
            calories = calories,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams,
            isFavorite = true
        )
    }
}
