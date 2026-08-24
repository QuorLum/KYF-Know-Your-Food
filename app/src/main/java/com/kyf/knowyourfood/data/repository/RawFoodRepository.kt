package com.kyf.knowyourfood.data.repository

import com.kyf.knowyourfood.data.local.dao.RawFoodDao
import com.kyf.knowyourfood.data.local.entity.RawFoodEntity
import com.kyf.knowyourfood.data.model.MicronutrientProfile
import com.kyf.knowyourfood.data.model.RawFoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class RawFoodRepository(private val rawFoodDao: RawFoodDao) {

    private val json = Json { ignoreUnknownKeys = true }

    fun getAllRawFoods(): Flow<List<RawFoodItem>> {
        return rawFoodDao.getAllRawFoods().map { list -> list.map { it.toDomain() } }
    }

    suspend fun getRawFoodById(fdcId: Long): RawFoodItem? {
        return rawFoodDao.getRawFoodById(fdcId)?.toDomain()
    }

    fun searchRawFoods(query: String, category: String = ""): Flow<List<RawFoodItem>> {
        return rawFoodDao.searchRawFoods(query, category).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getAllProduceCategories(): Flow<List<String>> = rawFoodDao.getAllProduceCategories()

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
