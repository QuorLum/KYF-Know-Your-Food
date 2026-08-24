package com.kyf.knowyourfood.data.repository

import com.kyf.knowyourfood.data.local.dao.PlateDao
import com.kyf.knowyourfood.data.local.dao.RawFoodDao
import com.kyf.knowyourfood.data.local.entity.PlateItemEntity
import com.kyf.knowyourfood.data.local.entity.RawFoodEntity
import com.kyf.knowyourfood.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

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
