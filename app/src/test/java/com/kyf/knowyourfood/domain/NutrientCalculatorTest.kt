package com.kyf.knowyourfood.domain

import com.kyf.knowyourfood.data.model.MicronutrientProfile
import com.kyf.knowyourfood.data.model.PlateItemWithFood
import com.kyf.knowyourfood.data.model.RawFoodItem
import com.kyf.knowyourfood.domain.engine.NutrientCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NutrientCalculatorTest {

    @Test
    fun testDynamicPortionScaling() {
        val apple = RawFoodItem(
            fdcId = 1001,
            name = "Fresh Apple",
            category = "Fruits",
            servingG = 100.0,
            protein = 0.3,
            carbs = 14.0,
            fat = 0.2,
            fiber = 2.4,
            iron = 0.12,
            vitC = 5.0,
            energyKcal = 52.0,
            micronutrients = MicronutrientProfile(potassium_mg = 100.0, calcium_mg = 6.0),
            source = "USDA Foundation"
        )

        val scaled200g = apple.scaleTo(200.0)
        assertEquals(200.0, scaled200g.grams, 0.01)
        assertEquals(104.0, scaled200g.energyKcal, 0.01)
        assertEquals(0.6, scaled200g.protein, 0.01)
        assertEquals(28.0, scaled200g.carbs, 0.01)
        assertEquals(4.8, scaled200g.fiber, 0.01)
        assertEquals(10.0, scaled200g.vitC, 0.01)
        assertEquals(200.0, scaled200g.potassium, 0.01)
    }

    @Test
    fun testSodiumUpperLimitAlert() {
        val highSodiumFood = RawFoodItem(
            fdcId = 9999,
            name = "High Salt Item",
            category = "Snacks",
            servingG = 100.0,
            protein = 5.0,
            carbs = 10.0,
            fat = 2.0,
            fiber = 1.0,
            iron = 1.0,
            vitC = 0.0,
            energyKcal = 100.0,
            micronutrients = MicronutrientProfile(sodium_mg = 3000.0), // Exceeds 2300mg CDRR
            source = "Test"
        )

        val plateItem = PlateItemWithFood(
            plateId = 1,
            profileId = 1,
            foodItem = highSodiumFood,
            quantityG = 100.0,
            scaledNutrition = highSodiumFood.scaleTo(100.0)
        )

        val totals = NutrientCalculator.calculatePlateTotals(listOf(plateItem))
        assertEquals(3000.0, totals.totalSodiumMg, 0.01)
        assertTrue(totals.upperLimitAlerts.any { it.nutrientName == "Sodium" })
    }
}
