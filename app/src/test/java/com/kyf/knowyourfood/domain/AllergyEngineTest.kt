package com.kyf.knowyourfood.domain

import com.kyf.knowyourfood.data.local.entity.ProductEntity
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.SafetyStatus
import com.kyf.knowyourfood.domain.engine.AllergyEngine
import org.junit.Assert.*
import org.junit.Test

class AllergyEngineTest {

    @Test
    fun testDirectPeanutAllergyConflict() {
        val profile = ProfileEntity(
            id = 1,
            name = "Test User",
            age = 25,
            gender = "Male",
            weight = 70.0,
            height = 175.0,
            allergiesJson = """{"allergens":["PEANUT"],"strict_traces":true}"""
        )

        val snickers = ProductEntity(
            barcode = "5000159461122",
            name = "Snickers Bar",
            brand = "Mars",
            category = "Confectionery",
            nutriScore = "E",
            sugars100g = 51.8,
            fat100g = 22.8,
            satFat100g = 8.5,
            salt100g = 0.63,
            ingredientsText = "Sugar, Peanuts, Milk, Cocoa Butter, Egg White Powder",
            allergensJson = """{"contains":["PEANUT","MILK","EGG"],"may_contain":[]}"""
        )

        val assessment = AllergyEngine.evaluateProductSafety(snickers, profile)
        assertEquals(SafetyStatus.UNSAFE, assessment.status)
        assertTrue(assessment.directAllergenMatches.any { it.matchedTerm.equals("peanuts", ignoreCase = true) || it.triggerName.contains("Peanut", ignoreCase = true) })
    }

    @Test
    fun testCeliacGlutenDetection() {
        val celiacProfile = ProfileEntity(
            id = 2,
            name = "David Celiac",
            age = 30,
            gender = "Male",
            weight = 75.0,
            height = 180.0,
            allergiesJson = """{"allergens":[],"conditions":["CELIAC"],"strict_traces":true}"""
        )

        val wheatBread = ProductEntity(
            barcode = "5010061001088",
            name = "Wholemeal Wheat Bread",
            brand = "Warburtons",
            category = "Bakery",
            nutriScore = "A",
            sugars100g = 3.0,
            fat100g = 2.5,
            satFat100g = 0.5,
            salt100g = 0.98,
            ingredientsText = "Wholemeal Wheat Flour, Water, Yeast, Salt, Wheat Gluten",
            allergensJson = """{"contains":["WHEAT","GLUTEN"],"may_contain":[]}"""
        )

        val assessment = AllergyEngine.evaluateProductSafety(wheatBread, celiacProfile)
        assertEquals(SafetyStatus.UNSAFE, assessment.status)
        assertTrue(assessment.nonIgEMatches.any { it.triggerName.contains("Celiac", ignoreCase = true) })
    }

    @Test
    fun testAlphaGalRedMeatDetection() {
        val alphaGalProfile = ProfileEntity(
            id = 3,
            name = "Sophie AlphaGal",
            age = 28,
            gender = "Female",
            weight = 60.0,
            height = 168.0,
            allergiesJson = """{"allergens":[],"conditions":["ALPHA_GAL"],"strict_traces":true}"""
        )

        val beefProduct = ProductEntity(
            barcode = "000000000001",
            name = "Beef Stew",
            brand = "Test",
            category = "Ready Meals",
            nutriScore = "C",
            sugars100g = 1.0,
            fat100g = 5.0,
            satFat100g = 2.0,
            salt100g = 1.0,
            ingredientsText = "Water, Beef, Potatoes, Carrots, Salt",
            allergensJson = """{"contains":[],"may_contain":[]}"""
        )

        val assessment = AllergyEngine.evaluateProductSafety(beefProduct, alphaGalProfile)
        assertEquals(SafetyStatus.UNSAFE, assessment.status)
        assertTrue(assessment.nonIgEMatches.any { it.triggerName.contains("Alpha-gal", ignoreCase = true) })
    }

    @Test
    fun testChildSugarGuardrail() {
        val childProfile = ProfileEntity(
            id = 4,
            name = "Child 7yo",
            age = 7,
            gender = "Female",
            weight = 22.0,
            height = 120.0,
            allergiesJson = """{"allergens":[],"conditions":[],"strict_traces":false}"""
        )

        val highSugarCereal = ProductEntity(
            barcode = "5000168001019",
            name = "Frosties",
            brand = "Kelloggs",
            category = "Cereals",
            nutriScore = "D",
            sugars100g = 37.0, // Exceeds 22.5g UK/EU high threshold
            fat100g = 0.6,
            satFat100g = 0.1,
            salt100g = 0.83,
            ingredientsText = "Maize, Sugar, Barley Malt Extract, Salt",
            allergensJson = """{"contains":[],"may_contain":[]}"""
        )

        val assessment = AllergyEngine.evaluateProductSafety(highSugarCereal, childProfile)
        assertEquals(SafetyStatus.UNSAFE, assessment.status)
        assertTrue(assessment.ageAlerts.isNotEmpty())
        assertTrue(assessment.ageAlerts.first().title.contains("Sugar", ignoreCase = true))
    }

    @Test
    fun testBirchPollenCrossReactivity() {
        val birchProfile = ProfileEntity(
            id = 5,
            name = "Birch Hayfever",
            age = 22,
            gender = "Male",
            weight = 68.0,
            height = 172.0,
            allergiesJson = """{"allergens":[],"pollen_sensitivities":["BIRCH"],"conditions":[],"strict_traces":false}"""
        )

        val appleJuice = ProductEntity(
            barcode = "5038862145672",
            name = "Apple Smoothie",
            brand = "Innocent",
            category = "Beverages",
            nutriScore = "B",
            sugars100g = 10.2,
            fat100g = 0.2,
            satFat100g = 0.0,
            salt100g = 0.01,
            ingredientsText = "Pressed Apples (80%), Mashed Banana, Kiwi",
            allergensJson = """{"contains":[],"may_contain":[]}"""
        )

        val assessment = AllergyEngine.evaluateProductSafety(appleJuice, birchProfile)
        assertEquals(SafetyStatus.CAUTION, assessment.status)
        assertTrue(assessment.pollenCrossMatches.isNotEmpty())
    }
}
