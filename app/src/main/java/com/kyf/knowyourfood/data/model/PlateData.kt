package com.kyf.knowyourfood.data.model

data class PlateItemWithFood(
    val plateId: Long,
    val profileId: Long,
    val foodItem: RawFoodItem,
    val quantityG: Double,
    val scaledNutrition: ScaledNutrition
)

data class NutrientUpperLimitAlert(
    val nutrientName: String,
    val currentAmount: Double,
    val upperLimit: Double,
    val unit: String,
    val message: String
)

data class PlateNutritionTotals(
    val totalGrams: Double,
    val totalCaloriesKcal: Double,
    val totalProteinG: Double,
    val totalCarbsG: Double,
    val totalFatG: Double,
    val totalFiberG: Double,
    val totalIronMg: Double,
    val totalVitCMg: Double,
    val totalPotassiumMg: Double,
    val totalCalciumMg: Double,
    val totalMagnesiumMg: Double,
    val totalSodiumMg: Double,
    val upperLimitAlerts: List<NutrientUpperLimitAlert> = emptyList()
)
