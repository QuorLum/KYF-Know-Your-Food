package com.kyf.knowyourfood.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "avatar_path")
    val avatarPath: String? = null,
    val age: Int,
    val gender: String,
    val weight: Double, // in kg
    val height: Double, // in cm
    @ColumnInfo(name = "allergies_json")
    val allergiesJson: String // Serialized AllergyProfile
)
