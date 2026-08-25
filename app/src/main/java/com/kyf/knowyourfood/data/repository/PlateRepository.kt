package com.kyf.knowyourfood.data.repository

import com.kyf.knowyourfood.data.local.dao.PlateDao
import com.kyf.knowyourfood.data.local.dao.RawFoodDao
import com.kyf.knowyourfood.data.local.entity.PlateItemEntity
import com.kyf.knowyourfood.data.local.entity.RawFoodEntity
import com.kyf.knowyourfood.data.model.*
import com.kyf.knowyourfood.domain.ai.RecognizedFoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.math.abs

class PlateRepository(
    private val plateDao: PlateDao,
    private val rawFoodDao: RawFoodDao
) {

    private val json = Json { ignoreUnknownKeys = true }

    fun getPlateItemsForProfile(profileId: Long): Flow<List<PlateItemWithFood>> {
        return plateDao.getPlateItemsForProfile(profileId).map { entities ->
            val foodIds = entities.map { it.foodId }
            val foodsMap = rawFoodDao.getRawFoodsByIds(foodIds).associateBy { it.fdcId }

            entities.mapNotNull { entity ->
                val foodEntity = foodsMap[entity.foodId] ?: return@mapNotNull null
                val domainFood = foodEntity.toDomain()
                PlateItemWithFood(
                    plateId = entity.id,
                    profileId = entity.profileId,
                    foodItem = domainFood,
                    quantityG = entity.quantityG,
                    scaledNutrition = domainFood.scaleTo(entity.quantityG)
                )
            }
        }
    }

    suspend fun addToPlate(profileId: Long, foodId: Long, quantityG: Double) {
        val existing = plateDao.getPlateItem(profileId, foodId)
        if (existing != null) {
            val updated = existing.copy(quantityG = existing.quantityG + quantityG)
            plateDao.updatePlateItem(updated)
        } else {
            val newItem = PlateItemEntity(
                profileId = profileId,
                foodId = foodId,
                quantityG = quantityG
            )
            plateDao.insertOrUpdatePlateItem(newItem)
        }
    }

    /**
     * Adds an AI Vision recognized food item directly to the plate,
     * resolving against existing USDA/INDB items or inserting an AI-derived food entity.
     */
    suspend fun addAiRecognizedItem(profileId: Long, item: RecognizedFoodItem) {
        // Try to find matching local food by name
        val matched = rawFoodDao.findRawFoodByName(item.name)

        val foodId = if (matched != null) {
            matched.fdcId
        } else {
            // Generate a unique ID for dynamic AI food entity
            val dynamicId = -(abs(item.name.hashCode().toLong() * 1000 + (System.currentTimeMillis() % 1000)))
            val entity = RawFoodEntity(
                fdcId = dynamicId,
                name = item.name,
                category = item.category.ifEmpty { "AI Recognized" },
                servingG = 100.0,
                protein = if (item.grams > 0) (item.protein / item.grams) * 100.0 else item.protein,
                carbs = if (item.grams > 0) (item.carbs / item.grams) * 100.0 else item.carbs,
                fat = if (item.grams > 0) (item.fat / item.grams) * 100.0 else item.fat,
                fiber = if (item.grams > 0) (item.fiber / item.grams) * 100.0 else item.fiber,
                iron = if (item.grams > 0) (item.iron / item.grams) * 100.0 else item.iron,
                vitC = if (item.grams > 0) (item.vit_c / item.grams) * 100.0 else item.vit_c,
                energyKcal = if (item.grams > 0) (item.energy_kcal / item.grams) * 100.0 else item.energy_kcal,
                nutrientsJson = "{}",
                source = "Gemini AI Vision"
            )
            rawFoodDao.insertRawFood(entity)
            dynamicId
        }

        addToPlate(profileId, foodId, if (item.grams > 0) item.grams else 100.0)
    }

    suspend fun updatePlateItemQuantity(plateId: Long, profileId: Long, foodId: Long, newQuantityG: Double) {
        if (newQuantityG <= 0) {
            plateDao.deletePlateItemById(plateId)
        } else {
            plateDao.updatePlateItem(
                PlateItemEntity(
                    id = plateId,
                    profileId = profileId,
                    foodId = foodId,
                    quantityG = newQuantityG
                )
            )
        }
    }

    suspend fun deletePlateItem(plateId: Long) {
        plateDao.deletePlateItemById(plateId)
    }

    suspend fun clearPlate(profileId: Long) {
        plateDao.clearPlateForProfile(profileId)
    }

    private fun RawFoodEntity.toDomain(): RawFoodItem {
        val micros: MicronutrientProfile = try {
            json.decodeFromString(nutrientsJson)
        } catch (e: Exception) {
            MicronutrientProfile()
        }

        return RawFoodItem(
            fdcId = fdcId,
            name = name,
            category = category,
            servingG = servingG,
            protein = protein,
            carbs = carbs,
            fat = fat,
            fiber = fiber,
            iron = iron,
            vitC = vitC,
            energyKcal = energyKcal,
            micronutrients = micros,
            source = source
        )
    }
}
