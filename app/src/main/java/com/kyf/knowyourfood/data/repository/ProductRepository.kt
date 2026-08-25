package com.kyf.knowyourfood.data.repository

import com.kyf.knowyourfood.data.local.dao.ProductDao
import com.kyf.knowyourfood.data.local.entity.ProductEntity
import com.kyf.knowyourfood.data.model.*
import com.kyf.knowyourfood.data.remote.OpenFoodFactsApi
import com.kyf.knowyourfood.domain.engine.SafetyRecommendationEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class ProductRepository(
    private val productDao: ProductDao,
    private val openFoodFactsApi: OpenFoodFactsApi = OpenFoodFactsApi()
) {

    private val json = Json { ignoreUnknownKeys = true }

    fun getAllProducts(): Flow<List<ProductItem>> {
        return productDao.getAllProducts().map { list -> list.map { it.toDomain() } }
    }

    /**
     * Offline-first product retrieval by barcode.
     * 1. Checks local SQLite database first.
     * 2. If not found locally, queries OpenFoodFacts Live API (3M+ products worldwide).
     * 3. Automatically saves newly discovered products into local SQLite for permanent offline access.
     */
    suspend fun getProductByBarcode(barcode: String): ProductItem? {
        val cleanBarcode = barcode.trim()
        if (cleanBarcode.isEmpty()) return null

        // 1. Try local SQLite
        val localProduct = productDao.getProductByBarcode(cleanBarcode)
        if (localProduct != null) {
            return localProduct.toDomain()
        }

        // 2. Fallback to OpenFoodFacts Live API
        val onlineProduct = openFoodFactsApi.fetchProductByBarcode(cleanBarcode)
        if (onlineProduct != null) {
            // Auto-cache into local SQLite database for future offline access
            productDao.insertProduct(onlineProduct)
            return onlineProduct.toDomain()
        }

        return null
    }

    /**
     * Returns the raw ProductEntity for engine evaluation, auto-fetching and caching
     * from OpenFoodFacts if not present locally.
     */
    suspend fun getProductEntityByBarcode(barcode: String): ProductEntity? {
        val cleanBarcode = barcode.trim()
        if (cleanBarcode.isEmpty()) return null

        val localEntity = productDao.getProductByBarcode(cleanBarcode)
        if (localEntity != null) return localEntity

        val onlineProduct = openFoodFactsApi.fetchProductByBarcode(cleanBarcode)
        if (onlineProduct != null) {
            productDao.insertProduct(onlineProduct)
            return onlineProduct
        }

        return null
    }

    fun observeProductByBarcode(barcode: String): Flow<ProductItem?> {
        return productDao.observeProductByBarcode(barcode).map { it?.toDomain() }
    }

    fun searchProducts(query: String, nutriScore: String = "", category: String = ""): Flow<List<ProductItem>> {
        return productDao.searchProducts(query, nutriScore, category).map { list ->
            list.map { it.toDomain() }
        }
    }

    /**
     * Searches OpenFoodFacts online when user searches for rare items and caches results.
     */
    suspend fun searchOnlineAndSync(query: String): List<ProductItem> {
        val cleanQuery = query.trim()
        if (cleanQuery.isEmpty()) return emptyList()

        val results = openFoodFactsApi.searchProductsOnline(cleanQuery)
        results.forEach { product ->
            try {
                productDao.insertProduct(product)
            } catch (e: Exception) {
                // Ignore if duplicate
            }
        }
        return results.map { it.toDomain() }
    }

    fun getAllCategories(): Flow<List<String>> = productDao.getAllCategories()

    suspend fun getHealthierAlternatives(product: ProductEntity): List<ProductItem> {
        val alternatives = mutableListOf<ProductEntity>()

        // 1. Try explicit linkages in healthier_alternatives_json
        if (!product.healthierAlternativesJson.isNullOrBlank()) {
            try {
                val barcodes: List<String> = json.decodeFromString(product.healthierAlternativesJson)
                val explicitMatches = productDao.getProductsByBarcodes(barcodes.filter { it != product.barcode })
                alternatives.addAll(explicitMatches)
            } catch (e: Exception) {
                // fallback to category query
            }
        }

        // 2. Query same category if needed
        if (alternatives.isEmpty()) {
            val categoryMatches = productDao.getHealthierCategoryAlternatives(product.category, product.barcode, limit = 3)
            alternatives.addAll(categoryMatches)
        }

        return alternatives.map { it.toDomain() }
    }

    suspend fun insertProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    private fun ProductEntity.toDomain(): ProductItem {
        val tags: AllergenTags = try {
            json.decodeFromString(allergensJson)
        } catch (e: Exception) {
            AllergenTags()
        }

        val altList: List<String> = try {
            if (!healthierAlternativesJson.isNullOrBlank()) json.decodeFromString(healthierAlternativesJson) else emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        return ProductItem(
            barcode = barcode,
            name = name,
            brand = brand,
            category = category,
            nutriScore = NutriScoreGrade.fromString(nutriScore),
            sugars100g = sugars100g,
            fat100g = fat100g,
            satFat100g = satFat100g,
            salt100g = salt100g,
            protein100g = protein100g,
            energyKcal100g = energyKcal100g,
            fiber100g = fiber100g,
            ingredientsText = ingredientsText,
            allergenTags = tags,
            trafficLights = SafetyRecommendationEngine.evaluateTrafficLights(this),
            healthierAlternatives = altList
        )
    }
}
