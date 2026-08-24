package com.kyf.knowyourfood.data.local.dao

import androidx.room.*
import com.kyf.knowyourfood.data.local.entity.RawFoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RawFoodDao {
    @Query("SELECT * FROM raw_foods ORDER BY name ASC")
    fun getAllRawFoods(): Flow<List<RawFoodEntity>>

    @Query("SELECT * FROM raw_foods WHERE fdc_id = :fdcId LIMIT 1")
    suspend fun getRawFoodById(fdcId: Long): RawFoodEntity?

    @Query("SELECT * FROM raw_foods WHERE fdc_id IN (:fdcIds)")
    suspend fun getRawFoodsByIds(fdcIds: List<Long>): List<RawFoodEntity>

    @Query("""
        SELECT * FROM raw_foods 
        WHERE (:query = '' OR name LIKE '%' || :query || '%')
        AND (:category = '' OR category = :category)
        ORDER BY name ASC
    """)
    fun searchRawFoods(query: String, category: String): Flow<List<RawFoodEntity>>

    @Query("SELECT DISTINCT category FROM raw_foods ORDER BY category ASC")
    fun getAllProduceCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRawFood(food: RawFoodEntity)
}
