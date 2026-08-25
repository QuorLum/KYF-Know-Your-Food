package com.kyf.knowyourfood.ui.screens.plate

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.PlateItemWithFood
import com.kyf.knowyourfood.data.model.PlateNutritionTotals
import com.kyf.knowyourfood.data.model.RecommendedRecipe
import com.kyf.knowyourfood.data.repository.PlateRepository
import com.kyf.knowyourfood.data.repository.ProfileRepository
import com.kyf.knowyourfood.domain.ai.AiPlateAnalysisResult
import com.kyf.knowyourfood.domain.ai.GeminiFoodVisionService
import com.kyf.knowyourfood.domain.ai.RecognizedFoodItem
import com.kyf.knowyourfood.domain.engine.NutrientCalculator
import com.kyf.knowyourfood.domain.engine.RecipeEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlateUiState(
    val activeProfile: ProfileEntity? = null,
    val plateItems: List<PlateItemWithFood> = emptyList(),
    val totals: PlateNutritionTotals? = null,
    val recommendedRecipes: List<RecommendedRecipe> = emptyList(),
    val isEditingItem: PlateItemWithFood? = null,
    val editGrams: Double = 100.0,
    val exportShareText: String? = null,
    val isAiAnalyzing: Boolean = false,
    val aiResult: AiPlateAnalysisResult? = null,
    val showAiResultModal: Boolean = false,
    val scannedMealBitmap: Bitmap? = null,
    val aiErrorMessage: String? = null
)

class PlateViewModel(
    private val plateRepository: PlateRepository,
    private val profileRepository: ProfileRepository,
    private val geminiFoodVisionService: GeminiFoodVisionService = GeminiFoodVisionService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlateUiState())
    val uiState: StateFlow<PlateUiState> = _uiState.asStateFlow()

    init {
        loadActiveProfileAndPlate()
    }

    private fun loadActiveProfileAndPlate() {
        viewModelScope.launch {
            profileRepository.getAllProfiles().collect { profiles ->
                val active = profiles.firstOrNull()
                _uiState.update { it.copy(activeProfile = active) }
                if (active != null) {
                    observePlate(active.id)
                }
            }
        }
    }

    private fun observePlate(profileId: Long) {
        viewModelScope.launch {
            plateRepository.getPlateItemsForProfile(profileId).collect { items ->
                val totals = NutrientCalculator.calculatePlateTotals(items)
                val recipes = RecipeEngine.findMatchingRecipes(items)
                _uiState.update {
                    it.copy(
                        plateItems = items,
                        totals = totals,
                        recommendedRecipes = recipes
                    )
                }
            }
        }
    }

    /**
     * Triggered when user captures or picks a meal photo.
     * Analyzes image with Gemini 1.5 Flash Vision AI.
     */
    fun analyzeMealPhoto(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                isAiAnalyzing = true,
                scannedMealBitmap = bitmap,
                aiErrorMessage = null
            )
        }

        viewModelScope.launch {
            val result = geminiFoodVisionService.analyzePlatePhoto(bitmap)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isAiAnalyzing = false,
                        aiResult = result,
                        showAiResultModal = true,
                        aiErrorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isAiAnalyzing = false,
                        aiResult = null,
                        showAiResultModal = false,
                        aiErrorMessage = result.errorMessage ?: "Could not recognize food items."
                    )
                }
            }
        }
    }

    fun confirmAddAiItemsToPlate(items: List<RecognizedFoodItem>) {
        val profileId = _uiState.value.activeProfile?.id ?: return
        viewModelScope.launch {
            items.forEach { item ->
                plateRepository.addAiRecognizedItem(profileId, item)
            }
            _uiState.update {
                it.copy(
                    showAiResultModal = false,
                    aiResult = null,
                    scannedMealBitmap = null
                )
            }
        }
    }

    fun closeAiResultModal() {
        _uiState.update {
            it.copy(
                showAiResultModal = false,
                aiResult = null,
                scannedMealBitmap = null
            )
        }
    }

    fun dismissAiError() {
        _uiState.update { it.copy(aiErrorMessage = null) }
    }

    fun openEditQuantity(item: PlateItemWithFood) {
        _uiState.update { it.copy(isEditingItem = item, editGrams = item.quantityG) }
    }

    fun updateEditGrams(grams: Double) {
        _uiState.update { it.copy(editGrams = grams) }
    }

    fun confirmEditQuantity() {
        val item = _uiState.value.isEditingItem ?: return
        val newGrams = _uiState.value.editGrams
        viewModelScope.launch {
            plateRepository.updatePlateItemQuantity(item.plateId, item.profileId, item.foodItem.fdcId, newGrams)
            _uiState.update { it.copy(isEditingItem = null) }
        }
    }

    fun closeEditDialog() {
        _uiState.update { it.copy(isEditingItem = null) }
    }

    fun removeItem(item: PlateItemWithFood) {
        viewModelScope.launch {
            plateRepository.deletePlateItem(item.plateId)
        }
    }

    fun clearPlate() {
        val profileId = _uiState.value.activeProfile?.id ?: return
        viewModelScope.launch {
            plateRepository.clearPlate(profileId)
        }
    }

    fun generateExportText(): String {
        val state = _uiState.value
        val profileName = state.activeProfile?.name ?: "User"
        val totals = state.totals ?: return "Plate is empty."

        val sb = StringBuilder()
        sb.appendLine("🥗 KYF (Know Your Food) - Plate & Nutrition Summary")
        sb.appendLine("Profile: $profileName")
        sb.appendLine("Total Weight: ${String.format("%.0f", totals.totalGrams)}g")
        sb.appendLine("----------------------------------------")
        sb.appendLine("Items on Plate:")
        state.plateItems.forEach { item ->
            sb.appendLine("• ${item.foodItem.name} - ${String.format("%.0f", item.quantityG)}g (${item.scaledNutrition.energyKcal.toInt()} kcal, ${String.format("%.1f", item.scaledNutrition.protein)}g Protein)")
        }
        sb.appendLine("----------------------------------------")
        sb.appendLine("Macronutrient & Micronutrient Totals:")
        sb.appendLine("Calories: ${totals.totalCaloriesKcal.toInt()} kcal")
        sb.appendLine("Protein: ${String.format("%.1f", totals.totalProteinG)}g")
        sb.appendLine("Carbohydrates: ${String.format("%.1f", totals.totalCarbsG)}g")
        sb.appendLine("Dietary Fiber: ${String.format("%.1f", totals.totalFiberG)}g")
        sb.appendLine("Total Fat: ${String.format("%.1f", totals.totalFatG)}g")
        sb.appendLine("Iron: ${String.format("%.1f", totals.totalIronMg)}mg")
        sb.appendLine("Vitamin C: ${String.format("%.1f", totals.totalVitCMg)}mg")
        sb.appendLine("Potassium: ${String.format("%.0f", totals.totalPotassiumMg)}mg")
        sb.appendLine("Calcium: ${String.format("%.0f", totals.totalCalciumMg)}mg")
        sb.appendLine("Sodium: ${String.format("%.0f", totals.totalSodiumMg)}mg")

        if (totals.upperLimitAlerts.isNotEmpty()) {
            sb.appendLine("----------------------------------------")
            sb.appendLine("⚠️ Upper Limit (UL) Alerts:")
            totals.upperLimitAlerts.forEach { sb.appendLine("• ${it.message}") }
        }

        if (state.recommendedRecipes.isNotEmpty()) {
            sb.appendLine("----------------------------------------")
            sb.appendLine("Suggested Recipe: ${state.recommendedRecipes.first().title}")
        }

        return sb.toString()
    }
}
