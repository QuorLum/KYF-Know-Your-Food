package com.kyf.knowyourfood.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val barcode: String,
    val name: String,
    val brand: String,
    val category: String,
    @ColumnInfo(name = "nutri_score")
    val nutriScore: String, // "A", "B", "C", "D", "E"
    @ColumnInfo(name = "sugars_100g")
    val sugars100g: Double,
    @ColumnInfo(name = "fat_100g")
    val fat100g: Double,
    @ColumnInfo(name = "sat_fat_100g")
    val satFat100g: Double,
    @ColumnInfo(name = "salt_100g")
    val salt100g: Double,
    @ColumnInfo(name = "protein_100g")
    val protein100g: Double = 0.0,
    @ColumnInfo(name = "energy_kcal_100g")
    val energyKcal100g: Double = 0.0,
    @ColumnInfo(name = "fiber_100g")
    val fiber100g: Double = 0.0,
    @ColumnInfo(name = "ingredients_text")
    val ingredientsText: String,
    @ColumnInfo(name = "allergens_json")
    val allergensJson: String,
    @ColumnInfo(name = "healthier_alternatives_json")
    val healthierAlternativesJson: String? = null
)
