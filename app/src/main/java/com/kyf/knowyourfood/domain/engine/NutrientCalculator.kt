package com.kyf.knowyourfood.domain.engine

import com.kyf.knowyourfood.data.model.NutrientUpperLimitAlert
import com.kyf.knowyourfood.data.model.PlateItemWithFood
import com.kyf.knowyourfood.data.model.PlateNutritionTotals

object NutrientCalculator {

    // Tolerable Upper Intake Levels (UL) and Chronic Disease Risk Reduction (CDRR) from Source 2
    const val SODIUM_CDRR_MG = 2300.0     // Sodium CDRR (hypertension/cardiovascular risk)
    const val VIT_A_UL_MCG = 3000.0       // Vitamin A Retinol UL
    const val VIT_C_UL_MG = 2000.0        // Vitamin C UL
    const val IRON_UL_MG = 45.0           // Iron UL
    const val CALCIUM_UL_MG = 2500.0      // Calcium UL
    const val ZINC_UL_MG = 40.0           // Zinc UL

    /**
     * Aggregates all nutrition across plate items and checks for Upper Limit breaches.
     */
    fun calculatePlateTotals(plateItems: List<PlateItemWithFood>): PlateNutritionTotals {
        var totalG = 0.0
        var totalKcal = 0.0
        var totalProtein = 0.0
        var totalCarbs = 0.0
        var totalFat = 0.0
        var totalFiber = 0.0
        var totalIron = 0.0
        var totalVitC = 0.0
        var totalPotassium = 0.0
        var totalCalcium = 0.0
        var totalMagnesium = 0.0
        var totalSodium = 0.0

        for (item in plateItems) {
            val s = item.scaledNutrition
            totalG += s.grams
            totalKcal += s.energyKcal
            totalProtein += s.protein
            totalCarbs += s.carbs
            totalFat += s.fat
            totalFiber += s.fiber
            totalIron += s.iron
            totalVitC += s.vitC
            totalPotassium += s.potassium
            totalCalcium += s.calcium
            totalMagnesium += s.magnesium
            totalSodium += s.sodium
        }

        val alerts = mutableListOf<NutrientUpperLimitAlert>()

        if (totalSodium > SODIUM_CDRR_MG) {
            alerts.add(
                NutrientUpperLimitAlert(
                    nutrientName = "Sodium",
                    currentAmount = totalSodium,
                    upperLimit = SODIUM_CDRR_MG,
                    unit = "mg",
                    message = "Sodium intake (${String.format("%.0f", totalSodium)}mg) exceeds the daily CDRR of 2,300mg."
                )
            )
        }
        if (totalIron > IRON_UL_MG) {
            alerts.add(
                NutrientUpperLimitAlert(
                    nutrientName = "Iron",
                    currentAmount = totalIron,
                    upperLimit = IRON_UL_MG,
                    unit = "mg",
                    message = "Iron intake (${String.format("%.1f", totalIron)}mg) exceeds the tolerable upper limit of 45mg/day."
                )
            )
        }
        if (totalVitC > VIT_C_UL_MG) {
            alerts.add(
                NutrientUpperLimitAlert(
                    nutrientName = "Vitamin C",
                    currentAmount = totalVitC,
                    upperLimit = VIT_C_UL_MG,
                    unit = "mg",
                    message = "Vitamin C (${String.format("%.0f", totalVitC)}mg) exceeds the tolerable upper limit of 2,000mg/day."
                )
            )
        }
        if (totalCalcium > CALCIUM_UL_MG) {
            alerts.add(
                NutrientUpperLimitAlert(
                    nutrientName = "Calcium",
                    currentAmount = totalCalcium,
                    upperLimit = CALCIUM_UL_MG,
                    unit = "mg",
                    message = "Calcium (${String.format("%.0f", totalCalcium)}mg) exceeds the adult upper limit of 2,500mg/day."
                )
            )
        }

        return PlateNutritionTotals(
            totalGrams = totalG,
            totalCaloriesKcal = totalKcal,
            totalProteinG = totalProtein,
            totalCarbsG = totalCarbs,
            totalFatG = totalFat,
            totalFiberG = totalFiber,
            totalIronMg = totalIron,
            totalVitCMg = totalVitC,
            totalPotassiumMg = totalPotassium,
            totalCalciumMg = totalCalcium,
            totalMagnesiumMg = totalMagnesium,
            totalSodiumMg = totalSodium,
            upperLimitAlerts = alerts
        )
    }
}
