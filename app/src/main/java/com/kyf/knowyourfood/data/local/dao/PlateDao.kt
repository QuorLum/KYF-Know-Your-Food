package com.kyf.knowyourfood.data.local.dao

import androidx.room.*
import com.kyf.knowyourfood.data.local.entity.PlateItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlateDao {
    @Query("SELECT * FROM plate WHERE profile_id = :profileId")
    fun getPlateItemsForProfile(profileId: Long): Flow<List<PlateItemEntity>>

    @Query("SELECT * FROM plate WHERE profile_id = :profileId")
    suspend fun getPlateItemsList(profileId: Long): List<PlateItemEntity>

    @Query("SELECT * FROM plate WHERE profile_id = :profileId AND food_id = :foodId LIMIT 1")
    suspend fun getPlateItem(profileId: Long, foodId: Long): PlateItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePlateItem(item: PlateItemEntity): Long

    @Update
    suspend fun updatePlateItem(item: PlateItemEntity)

    @Query("DELETE FROM plate WHERE id = :id")
    suspend fun deletePlateItemById(id: Long)

    @Query("DELETE FROM plate WHERE profile_id = :profileId")
    suspend fun clearPlateForProfile(profileId: Long)
}
