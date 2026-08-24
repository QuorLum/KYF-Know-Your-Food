package com.kyf.knowyourfood.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AllergenTags(
    val contains: List<String> = emptyList(),
    val may_contain: List<String> = emptyList()
)

enum class TrafficLightLevel(val displayName: String, val hexColor: Long) {
    LOW("LOW", 0xFF10B981),      // Emerald Green
    MEDIUM("MEDIUM", 0xFFF59E0B), // Amber / Yellow
    HIGH("HIGH", 0xFFEF4444)      // Crimson Red
}

data class TrafficLights(
    val totalFatLevel: TrafficLightLevel,
    val totalFatValue: Double,
    val satFatLevel: TrafficLightLevel,
    val satFatValue: Double,
    val sugarsLevel: TrafficLightLevel,
    val sugarsValue: Double,
    val saltLevel: TrafficLightLevel,
    val saltValue: Double
)

enum class NutriScoreGrade(val letter: String, val colorHex: Long) {
    A("A", 0xFF008B4C), // Dark Green
    B("B", 0xFF85BB2F), // Light Green
    C("C", 0xFFFECB02), // Yellow
    D("D", 0xFFEE8100), // Orange
    E("E", 0xFFE63E11); // Red

    companion object {
        fun fromString(score: String): NutriScoreGrade {
            return entries.find { it.letter.equals(score.trim(), ignoreCase = true) } ?: C
        }
    }
}

data class ProductItem(
    val barcode: String,
    val name: String,
    val brand: String,
    val category: String,
    val nutriScore: NutriScoreGrade,
    val sugars100g: Double,
    val fat100g: Double,
    val satFat100g: Double,
    val salt100g: Double,
    val protein100g: Double,
    val energyKcal100g: Double,
    val fiber100g: Double,
    val ingredientsText: String,
    val allergenTags: AllergenTags,
    val trafficLights: TrafficLights,
    val healthierAlternatives: List<String> = emptyList()
)
