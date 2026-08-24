package com.kyf.knowyourfood.domain.engine

import com.kyf.knowyourfood.data.local.entity.ProductEntity
import com.kyf.knowyourfood.data.model.*

object SafetyRecommendationEngine {

    /**
     * Official UK/EU Front-of-Pack Traffic Light standards (per 100g solid food) from Source 2:
     * Total Fat:      Low <= 3.0g  | Medium 3.0 - 17.5g | High > 17.5g
     * Saturated Fat:  Low <= 1.5g  | Medium 1.5 - 5.0g  | High > 5.0g
     * Total Sugars:   Low <= 5.0g  | Medium 5.0 - 22.5g | High > 22.5g
     * Salt:           Low <= 0.3g  | Medium 0.3 - 1.5g  | High > 1.5g
     */
    fun evaluateTrafficLights(product: ProductEntity): TrafficLights {
        val fatLevel = when {
            product.fat100g <= 3.0 -> TrafficLightLevel.LOW
            product.fat100g <= 17.5 -> TrafficLightLevel.MEDIUM
            else -> TrafficLightLevel.HIGH
        }

        val satFatLevel = when {
            product.satFat100g <= 1.5 -> TrafficLightLevel.LOW
            product.satFat100g <= 5.0 -> TrafficLightLevel.MEDIUM
            else -> TrafficLightLevel.HIGH
        }

        val sugarsLevel = when {
            product.sugars100g <= 5.0 -> TrafficLightLevel.LOW
            product.sugars100g <= 22.5 -> TrafficLightLevel.MEDIUM
            else -> TrafficLightLevel.HIGH
        }

        val saltLevel = when {
            product.salt100g <= 0.3 -> TrafficLightLevel.LOW
            product.salt100g <= 1.5 -> TrafficLightLevel.MEDIUM
            else -> TrafficLightLevel.HIGH
        }

        return TrafficLights(
            totalFatLevel = fatLevel,
            totalFatValue = product.fat100g,
            satFatLevel = satFatLevel,
            satFatValue = product.satFat100g,
            sugarsLevel = sugarsLevel,
            sugarsValue = product.sugars100g,
            saltLevel = saltLevel,
            saltValue = product.salt100g
        )
    }

    /**
     * Computes Nutri-Score (A to E) based on energy, saturated fat, sugars, sodium, fiber, protein.
     */
    fun computeNutriScore(
        energyKcal: Double,
        sugars100g: Double,
        satFat100g: Double,
        sodiumMg: Double,
        fiber100g: Double,
        protein100g: Double,
        isBeverage: Boolean = false
    ): NutriScoreGrade {
        // Negative points (N)
        var nPoints = 0
        // Energy points (kJ = kcal * 4.184)
        val energyKj = energyKcal * 4.184
        nPoints += when {
            energyKj <= 335 -> 0
            energyKj <= 670 -> 1
            energyKj <= 1005 -> 2
            energyKj <= 1340 -> 3
            energyKj <= 1675 -> 4
            energyKj <= 2010 -> 5
            energyKj <= 2345 -> 6
            energyKj <= 2680 -> 7
            energyKj <= 3015 -> 8
            energyKj <= 3350 -> 9
            else -> 10
        }
        // Sugars
        nPoints += when {
            sugars100g <= 4.5 -> 0
            sugars100g <= 9.0 -> 1
            sugars100g <= 13.5 -> 2
            sugars100g <= 18.0 -> 3
            sugars100g <= 22.5 -> 4
            sugars100g <= 27.0 -> 5
            sugars100g <= 31.0 -> 6
            sugars100g <= 36.0 -> 7
            sugars100g <= 40.0 -> 8
            sugars100g <= 45.0 -> 9
            else -> 10
        }
        // Sat Fat
        nPoints += when {
            satFat100g <= 1.0 -> 0
            satFat100g <= 2.0 -> 1
            satFat100g <= 3.0 -> 2
            satFat100g <= 4.0 -> 3
            satFat100g <= 5.0 -> 4
            satFat100g <= 6.0 -> 5
            satFat100g <= 7.0 -> 6
            satFat100g <= 8.0 -> 7
            satFat100g <= 9.0 -> 8
            satFat100g <= 10.0 -> 9
            else -> 10
        }
        // Sodium
        nPoints += when {
            sodiumMg <= 90 -> 0
            sodiumMg <= 180 -> 1
            sodiumMg <= 270 -> 2
            sodiumMg <= 360 -> 3
            sodiumMg <= 450 -> 4
            sodiumMg <= 540 -> 5
            sodiumMg <= 630 -> 6
            sodiumMg <= 720 -> 7
            sodiumMg <= 810 -> 8
            sodiumMg <= 900 -> 9
            else -> 10
        }

        // Positive points (P)
        var pPoints = 0
        // Fiber
        pPoints += when {
            fiber100g <= 0.9 -> 0
            fiber100g <= 1.9 -> 1
            fiber100g <= 2.8 -> 2
            fiber100g <= 3.7 -> 3
            fiber100g <= 4.7 -> 4
            else -> 5
        }
        // Protein
        pPoints += when {
            protein100g <= 1.6 -> 0
            protein100g <= 3.2 -> 1
            protein100g <= 4.8 -> 2
            protein100g <= 6.4 -> 3
            protein100g <= 8.0 -> 4
            else -> 5
        }

        val totalScore = if (nPoints >= 11) {
            nPoints - (pPoints - (if (fiber100g > 3.5) 0 else 0)) // simplified standard Nutri-Score calculation
        } else {
            nPoints - pPoints
        }

        return when {
            totalScore <= -1 -> NutriScoreGrade.A
            totalScore <= 2 -> NutriScoreGrade.B
            totalScore <= 10 -> NutriScoreGrade.C
            totalScore <= 18 -> NutriScoreGrade.D
            else -> NutriScoreGrade.E
        }
    }
}
