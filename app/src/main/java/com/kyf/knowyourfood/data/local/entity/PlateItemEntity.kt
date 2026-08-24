package com.kyf.knowyourfood.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plate",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RawFoodEntity::class,
            parentColumns = ["fdc_id"],
            childColumns = ["food_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("profile_id"),
        Index("food_id")
    ]
)
data class PlateItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "profile_id")
    val profileId: Long,
    @ColumnInfo(name = "food_id")
    val foodId: Long,
    @ColumnInfo(name = "quantity_g")
    val quantityG: Double
)
