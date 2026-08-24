package com.kyf.knowyourfood.data.local.dao

import androidx.room.*
import com.kyf.knowyourfood.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    fun observeProductByBarcode(barcode: String): Flow<ProductEntity?>

    @Query("""
        SELECT * FROM products 
        WHERE (:query = '' OR name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%')
        AND (:nutriScore = '' OR nutri_score = :nutriScore)
        AND (:category = '' OR category = :category)
        ORDER BY name ASC
    """)
    fun searchProducts(query: String, nutriScore: String, category: String): Flow<List<ProductEntity>>

    @Query("SELECT DISTINCT category FROM products ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM products WHERE category = :category AND barcode != :currentBarcode ORDER BY CASE nutri_score WHEN 'A' THEN 1 WHEN 'B' THEN 2 WHEN 'C' THEN 3 WHEN 'D' THEN 4 ELSE 5 END ASC LIMIT :limit")
    suspend fun getHealthierCategoryAlternatives(category: String, currentBarcode: String, limit: Int = 3): List<ProductEntity>

    @Query("SELECT * FROM products WHERE barcode IN (:barcodes)")
    suspend fun getProductsByBarcodes(barcodes: List<String>): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
}
