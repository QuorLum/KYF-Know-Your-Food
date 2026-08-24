package com.kyf.knowyourfood.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "raw_foods")
data class RawFoodEntity(
    @PrimaryKey
    @ColumnInfo(name = "fdc_id")
    val fdcId: Long,
    val name: String,
    val category: String, // "Fruits", "Vegetables", "Legumes", "Nuts & Seeds", "Grains", "Poultry", "Fish & Seafood", "Eggs & Dairy", "Meat"
    @ColumnInfo(name = "serving_g")
    val servingG: Double = 100.0,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val iron: Double,
    @ColumnInfo(name = "vit_c")
    val vitC: Double,
    @ColumnInfo(name = "energy_kcal")
    val energyKcal: Double = 0.0,
    @ColumnInfo(name = "nutrients_json")
    val nutrientsJson: String,
    val source: String // "USDA Foundation", "USDA SR Legacy", "INDB 2024"
)
