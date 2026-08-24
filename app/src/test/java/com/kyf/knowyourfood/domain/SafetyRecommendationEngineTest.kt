package com.kyf.knowyourfood.domain

import com.kyf.knowyourfood.data.local.entity.ProductEntity
import com.kyf.knowyourfood.data.model.NutriScoreGrade
import com.kyf.knowyourfood.data.model.TrafficLightLevel
import com.kyf.knowyourfood.domain.engine.SafetyRecommendationEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class SafetyRecommendationEngineTest {

    @Test
    fun testUKFrontOfPackTrafficLights() {
        // High fat, high sat fat, high sugar, high salt item
        val junkFood = ProductEntity(
            barcode = "111111111111",
            name = "High Everything Snack",
            brand = "Test",
            category = "Snacks",
            nutriScore = "E",
            sugars100g = 25.0, // > 22.5g -> High
            fat100g = 20.0,    // > 17.5g -> High
            satFat100g = 6.0,  // > 5.0g  -> High
            salt100g = 2.0,    // > 1.5g  -> High
            ingredientsText = "Ingredients",
            allergensJson = "{}"
        )

        val lights = SafetyRecommendationEngine.evaluateTrafficLights(junkFood)
        assertEquals(TrafficLightLevel.HIGH, lights.totalFatLevel)
        assertEquals(TrafficLightLevel.HIGH, lights.satFatLevel)
        assertEquals(TrafficLightLevel.HIGH, lights.sugarsLevel)
        assertEquals(TrafficLightLevel.HIGH, lights.saltLevel)

        // Low fat, low sat fat, low sugar, low salt item
        val healthyItem = ProductEntity(
            barcode = "222222222222",
            name = "Super Healthy Food",
            brand = "Test",
            category = "Produce",
            nutriScore = "A",
            sugars100g = 2.0,  // <= 5.0g -> Low
            fat100g = 1.0,     // <= 3.0g -> Low
            satFat100g = 0.2,  // <= 1.5g -> Low
            salt100g = 0.1,    // <= 0.3g -> Low
            ingredientsText = "Ingredients",
            allergensJson = "{}"
        )

        val healthyLights = SafetyRecommendationEngine.evaluateTrafficLights(healthyItem)
        assertEquals(TrafficLightLevel.LOW, healthyLights.totalFatLevel)
        assertEquals(TrafficLightLevel.LOW, healthyLights.satFatLevel)
        assertEquals(TrafficLightLevel.LOW, healthyLights.sugarsLevel)
        assertEquals(TrafficLightLevel.LOW, healthyLights.saltLevel)
    }

    @Test
    fun testNutriScoreComputation() {
        val scoreA = SafetyRecommendationEngine.computeNutriScore(
            energyKcal = 45.0,
            sugars100g = 2.5,
            satFat100g = 0.3,
            sodiumMg = 50.0,
            fiber100g = 4.0,
            protein100g = 6.0
        )
        assertEquals(NutriScoreGrade.A, scoreA)

        val scoreE = SafetyRecommendationEngine.computeNutriScore(
            energyKcal = 540.0,
            sugars100g = 56.0,
            satFat100g = 12.0,
            sodiumMg = 950.0,
            fiber100g = 1.0,
            protein100g = 2.0
        )
        assertEquals(NutriScoreGrade.E, scoreE)
    }
}
