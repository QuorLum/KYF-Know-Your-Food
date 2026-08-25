package com.kyf.knowyourfood.data.remote

import com.kyf.knowyourfood.data.local.entity.ProductEntity
import com.kyf.knowyourfood.data.model.AllergenTags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class OpenFoodFactsApi(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
) {
    private val jsonParser = Json { ignoreUnknownKeys = true }

    /**
     * Fetches real-time product data for any global barcode from OpenFoodFacts API.
     * Returns null if product is not found or device is offline.
     */
    suspend fun fetchProductByBarcode(barcode: String): ProductEntity? = withContext(Dispatchers.IO) {
        try {
            val cleanBarcode = barcode.trim()
            val url = "https://world.openfoodfacts.org/api/v2/product/$cleanBarcode.json"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "KYF-KnowYourFood-Android/1.0 (contact: info@kyf.app)")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val bodyString = response.body?.string() ?: return@withContext null
            val rootObj = jsonParser.parseToJsonElement(bodyString).jsonObject
            val status = rootObj["status"]?.jsonPrimitive?.intOrNull ?: 0
            if (status != 1) return@withContext null

            val productObj = rootObj["product"]?.jsonObject ?: return@withContext null
            parseProductObject(cleanBarcode, productObj)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Searches global products on OpenFoodFacts by query terms.
     */
    suspend fun searchProductsOnline(query: String): List<ProductEntity> = withContext(Dispatchers.IO) {
        try {
            val encoded = java.net.URLEncoder.encode(query.trim(), "UTF-8")
            val url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=$encoded&search_simple=1&action=process&json=1&page_size=15"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "KYF-KnowYourFood-Android/1.0 (contact: info@kyf.app)")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()

            val bodyString = response.body?.string() ?: return@withContext emptyList()
            val rootObj = jsonParser.parseToJsonElement(bodyString).jsonObject
            val productsArray = rootObj["products"]?.jsonArray ?: return@withContext emptyList()

            productsArray.mapNotNull { element ->
                val obj = element.jsonObject
                val code = obj["code"]?.jsonPrimitive?.contentOrNull
                    ?: obj["_id"]?.jsonPrimitive?.contentOrNull
                    ?: return@mapNotNull null
                parseProductObject(code, obj)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseProductObject(barcode: String, productObj: JsonObject): ProductEntity? {
        val name = productObj["product_name"]?.jsonPrimitive?.contentOrNull
            ?: productObj["product_name_en"]?.jsonPrimitive?.contentOrNull
            ?: productObj["generic_name"]?.jsonPrimitive?.contentOrNull
            ?: return null

        val brand = productObj["brands"]?.jsonPrimitive?.contentOrNull
            ?: productObj["brand_owner"]?.jsonPrimitive?.contentOrNull
            ?: "Unknown Brand"

        val categoriesStr = productObj["categories"]?.jsonPrimitive?.contentOrNull ?: "General Grocery"
        val primaryCategory = categoriesStr.split(",").firstOrNull()?.trim() ?: "General Grocery"

        val nutriScoreRaw = productObj["nutriscore_grade"]?.jsonPrimitive?.contentOrNull
            ?: productObj["nutrition_grades"]?.jsonPrimitive?.contentOrNull
            ?: "C"
        val nutriScore = nutriScoreRaw.trim().uppercase().ifEmpty { "C" }

        val nutriments = productObj["nutriments"]?.jsonObject

        val sugars = nutriments?.get("sugars_100g")?.jsonPrimitive?.doubleOrNull
            ?: nutriments?.get("sugars")?.jsonPrimitive?.doubleOrNull ?: 0.0
        val fat = nutriments?.get("fat_100g")?.jsonPrimitive?.doubleOrNull
            ?: nutriments?.get("fat")?.jsonPrimitive?.doubleOrNull ?: 0.0
        val satFat = nutriments?.get("saturated-fat_100g")?.jsonPrimitive?.doubleOrNull
            ?: nutriments?.get("saturated_fat_100g")?.jsonPrimitive?.doubleOrNull ?: 0.0
        val salt = nutriments?.get("salt_100g")?.jsonPrimitive?.doubleOrNull
            ?: ((nutriments?.get("sodium_100g")?.jsonPrimitive?.doubleOrNull ?: 0.0) * 2.5)
        val protein = nutriments?.get("proteins_100g")?.jsonPrimitive?.doubleOrNull
            ?: nutriments?.get("proteins")?.jsonPrimitive?.doubleOrNull ?: 0.0
        val energyKcal = nutriments?.get("energy-kcal_100g")?.jsonPrimitive?.doubleOrNull
            ?: ((nutriments?.get("energy_100g")?.jsonPrimitive?.doubleOrNull ?: 0.0) / 4.184)
        val fiber = nutriments?.get("fiber_100g")?.jsonPrimitive?.doubleOrNull
            ?: nutriments?.get("fiber")?.jsonPrimitive?.doubleOrNull ?: 0.0

        val ingredientsText = productObj["ingredients_text"]?.jsonPrimitive?.contentOrNull
            ?: productObj["ingredients_text_en"]?.jsonPrimitive?.contentOrNull
            ?: "Ingredients not specified."

        // Parse allergens and traces
        val allergensTagsList = productObj["allergens_tags"]?.jsonArray?.mapNotNull {
            it.jsonPrimitive.contentOrNull?.replace("en:", "")?.replace("-", " ")
        } ?: emptyList()

        val tracesTagsList = productObj["traces_tags"]?.jsonArray?.mapNotNull {
            it.jsonPrimitive.contentOrNull?.replace("en:", "")?.replace("-", " ")
        } ?: emptyList()

        val allergensJson = jsonParser.encodeToString(
            AllergenTags.serializer(),
            AllergenTags(contains = allergensTagsList, may_contain = tracesTagsList)
        )

        return ProductEntity(
            barcode = barcode,
            name = name,
            brand = brand,
            category = primaryCategory,
            nutriScore = if (nutriScore in listOf("A", "B", "C", "D", "E")) nutriScore else "C",
            sugars100g = sugars,
            fat100g = fat,
            satFat100g = satFat,
            salt100g = salt,
            protein100g = protein,
            energyKcal100g = energyKcal,
            fiber100g = fiber,
            ingredientsText = ingredientsText,
            allergensJson = allergensJson,
            healthierAlternativesJson = "[]"
        )
    }
}
