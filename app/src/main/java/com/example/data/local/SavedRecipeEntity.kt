package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_recipes")
data class SavedRecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val cookTimeMinutes: Int,
    val difficulty: String,
    val servings: Int,
    val imageDrawableName: String,
    val goalTag: String,
    val ingredientsJson: String,
    val stepsJson: String,
    val substitutionsJson: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val isFavorite: Boolean = true,
    val savedTimestamp: Long = System.currentTimeMillis()
)
