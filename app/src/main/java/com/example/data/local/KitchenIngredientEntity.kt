package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kitchen_ingredients")
data class KitchenIngredientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val emoji: String,
    val category: String,
    val quantity: String,
    val dateAdded: Long = System.currentTimeMillis()
)
