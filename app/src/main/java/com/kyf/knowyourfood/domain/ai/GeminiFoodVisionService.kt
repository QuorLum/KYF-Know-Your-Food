package com.kyf.knowyourfood.domain.ai

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.kyf.knowyourfood.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

@Serializable
data class RecognizedFoodItem(
    val name: String,
    val category: String = "Produce",
    val grams: Double = 100.0,
    val energy_kcal: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,
    val iron: Double = 0.0,
    val vit_c: Double = 0.0
)

data class AiPlateAnalysisResult(
    val isSuccess: Boolean,
    val items: List<RecognizedFoodItem>,
    val disclaimer: String = "⚠️ Estimated by AI Vision: Nutritional values and portion sizes are automated approximations and may vary. Not intended for medical dietary advice.",
    val errorMessage: String? = null
)

class GeminiFoodVisionService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    /**
     * Analyzes a food plate bitmap using Gemini 1.5 Flash Vision.
     */
    suspend fun analyzePlatePhoto(
        bitmap: Bitmap,
        customApiKey: String? = null
    ): AiPlateAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = customApiKey?.trim()?.ifEmpty { null }
            ?: BuildConfig.GEMINI_API_KEY.trim().ifEmpty { null }

        if (apiKey.isNullOrBlank()) {
            return@withContext AiPlateAnalysisResult(
                isSuccess = false,
                items = emptyList(),
                errorMessage = "No Gemini API Key found. Please add your key in Settings or local.properties."
            )
        }

        // Method 1: Try official Google Generative AI SDK
        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )

            val prompt = """
                You are a smart clinical nutritionist and food vision AI.
                Analyze this meal plate photo carefully.
                Identify each individual food item, ingredient, or portion on the plate.
                For each identified item, estimate the portion weight in grams and calculate its nutritional facts (calories, protein, carbs, fat, fiber, iron in mg, and vitamin C in mg).

                Return ONLY a pure JSON array (no markdown code blocks, no other text) with this exact schema:
                [
                  {
                    "name": "Food Name",
                    "category": "Vegetables/Fruits/Legumes/Grains/Meat/Dairy",
                    "grams": 120.0,
                    "energy_kcal": 150.0,
                    "protein": 5.0,
                    "carbs": 25.0,
                    "fat": 3.0,
                    "fiber": 4.0,
                    "iron": 1.5,
                    "vit_c": 12.0
                  }
                ]
            """.trimIndent()

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = generativeModel.generateContent(inputContent)
            val responseText = response.text

            if (!responseText.isNullOrBlank()) {
                val parsedItems = parseGeminiResponse(responseText)
                if (parsedItems.isNotEmpty()) {
                    return@withContext AiPlateAnalysisResult(
                        isSuccess = true,
                        items = parsedItems
                    )
                }
            }
        } catch (e: Exception) {
            // Fallback to direct REST API if SDK has serialization mismatch
        }

        // Method 2: Fallback to Direct REST Call
        try {
            val base64Image = bitmapToBase64(bitmap)
            val restUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

            val jsonBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "Analyze this food plate photo. Identify all individual food items on the plate. Estimate portion weight in grams, and provide calories, protein, carbs, fat, fiber, iron, vit_c. Return strictly a JSON array with schema: [{\"name\":\"string\",\"category\":\"string\",\"grams\":number,\"energy_kcal\":number,\"protein\":number,\"carbs\":number,\"fat\":number,\"fiber\":number,\"iron\":number,\"vit_c\":number}]"
                        },
                        {
                          "inline_data": {
                            "mime_type": "image/jpeg",
                            "data": "$base64Image"
                          }
                        }
                      ]
                    }
                  ]
                }
            """.trimIndent()

            val request = Request.Builder()
                .url(restUrl)
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && !body.isNullOrBlank()) {
                val root = json.parseToJsonElement(body).jsonObject
                val candidates = root["candidates"]?.jsonArray
                val firstCandidate = candidates?.firstOrNull()?.jsonObject
                val content = firstCandidate?.get("content")?.jsonObject
                val parts = content?.get("parts")?.jsonArray
                val text = parts?.firstOrNull()?.jsonObject?.get("text")?.jsonPrimitive?.contentOrNull

                if (!text.isNullOrBlank()) {
                    val parsedItems = parseGeminiResponse(text)
                    if (parsedItems.isNotEmpty()) {
                        return@withContext AiPlateAnalysisResult(
                            isSuccess = true,
                            items = parsedItems
                        )
                    }
                }
            }
        } catch (e: Exception) {
            return@withContext AiPlateAnalysisResult(
                isSuccess = false,
                items = emptyList(),
                errorMessage = "AI Vision analysis failed: ${e.localizedMessage ?: "Network or API error"}"
            )
        }

        return@withContext AiPlateAnalysisResult(
            isSuccess = false,
            items = emptyList(),
            errorMessage = "Could not detect distinct food items on the plate. Please try another angle with better lighting."
        )
    }

    private fun parseGeminiResponse(rawText: String): List<RecognizedFoodItem> {
        val cleanJson = rawText
            .replace("```json", "")
            .replace("```", "")
            .trim()

        return try {
            val jsonArray = json.parseToJsonElement(cleanJson).jsonArray
            jsonArray.mapNotNull { elem ->
                val obj = elem.jsonObject
                val name = obj["name"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                val cat = obj["category"]?.jsonPrimitive?.contentOrNull ?: "Produce"
                val grams = obj["grams"]?.jsonPrimitive?.doubleOrNull ?: 100.0
                val energy = obj["energy_kcal"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val protein = obj["protein"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val carbs = obj["carbs"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val fat = obj["fat"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val fiber = obj["fiber"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val iron = obj["iron"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val vitC = obj["vit_c"]?.jsonPrimitive?.doubleOrNull ?: 0.0

                RecognizedFoodItem(
                    name = name,
                    category = cat,
                    grams = grams,
                    energy_kcal = energy,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    fiber = fiber,
                    iron = iron,
                    vit_c = vitC
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        // Scale down to max 1024px to reduce upload latency and token costs
        val maxDim = 1024
        val width = bitmap.width
        val height = bitmap.height
        val scaled = if (width > maxDim || height > maxDim) {
            val ratio = width.toFloat() / height.toFloat()
            if (ratio > 1) {
                Bitmap.createScaledBitmap(bitmap, maxDim, (maxDim / ratio).toInt(), true)
            } else {
                Bitmap.createScaledBitmap(bitmap, (maxDim * ratio).toInt(), maxDim, true)
            }
        } else {
            bitmap
        }

        val outputStream = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
    }
}
