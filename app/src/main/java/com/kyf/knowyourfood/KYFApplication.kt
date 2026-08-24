package com.kyf.knowyourfood

import android.app.Application
import com.kyf.knowyourfood.data.local.AppDatabase
import com.kyf.knowyourfood.data.repository.PlateRepository
import com.kyf.knowyourfood.data.repository.ProductRepository
import com.kyf.knowyourfood.data.repository.ProfileRepository
import com.kyf.knowyourfood.data.repository.RawFoodRepository

class KYFApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val profileRepository by lazy { ProfileRepository(database.profileDao()) }
    val productRepository by lazy { ProductRepository(database.productDao()) }
    val rawFoodRepository by lazy { RawFoodRepository(database.rawFoodDao()) }
    val plateRepository by lazy { PlateRepository(database.plateDao(), database.rawFoodDao()) }

    override fun onCreate() {
        super.onCreate()
    }
}
