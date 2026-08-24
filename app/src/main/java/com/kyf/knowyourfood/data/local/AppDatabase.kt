package com.kyf.knowyourfood.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kyf.knowyourfood.data.local.dao.PlateDao
import com.kyf.knowyourfood.data.local.dao.ProductDao
import com.kyf.knowyourfood.data.local.dao.ProfileDao
import com.kyf.knowyourfood.data.local.dao.RawFoodDao
import com.kyf.knowyourfood.data.local.entity.PlateItemEntity
import com.kyf.knowyourfood.data.local.entity.ProductEntity
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.local.entity.RawFoodEntity

@Database(
    entities = [
        ProfileEntity::class,
        ProductEntity::class,
        RawFoodEntity::class,
        PlateItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun productDao(): ProductDao
    abstract fun rawFoodDao(): RawFoodDao
    abstract fun plateDao(): PlateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutrition_app.db"
                )
                    .createFromAsset("databases/nutrition_app.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
