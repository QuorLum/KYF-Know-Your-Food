package com.kyf.knowyourfood.ui.screens.produce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.RawFoodItem
import com.kyf.knowyourfood.data.repository.PlateRepository
import com.kyf.knowyourfood.data.repository.ProfileRepository
import com.kyf.knowyourfood.data.repository.RawFoodRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class NutrientFilter(val displayName: String) {
    ALL("All Produce"),
    IRON_RICH("Iron-Rich (≥2.5mg)"),
    VIT_C_RICH("Vitamin C-Rich (≥40mg)"),
    HIGH_FIBER("High Fiber (≥5g)"),
    HIGH_PROTEIN("High Protein (≥8g)"),
    LOW_POTASSIUM("Low Potassium (≤200mg)"),
    LOW_CALORIE("Low Calorie (≤40 kcal)")
}

data class ProduceUiState(
    val query: String = "",
    val selectedCategory: String = "",
    val selectedNutrientFilter: NutrientFilter = NutrientFilter.ALL,
    val selectedSourceFilter: String = "", // "USDA Foundation", "INDB 2024", ""
    val categories: List<String> = emptyList(),
    val rawFoods: List<RawFoodItem> = emptyList(),
    val selectedFoodForPlate: RawFoodItem? = null,
    val customServingGrams: Double = 100.0,
    val activeProfile: ProfileEntity? = null,
    val addedSuccessMessage: String? = null,
    val isLoading: Boolean = true
)

class ProduceViewModel(
    private val rawFoodRepository: RawFoodRepository,
    private val plateRepository: PlateRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("")
    private val _nutrientFilter = MutableStateFlow(NutrientFilter.ALL)
    private val _sourceFilter = MutableStateFlow("")

    private val _uiState = MutableStateFlow(ProduceUiState())
    val uiState: StateFlow<ProduceUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadActiveProfile()
        observeRawFoods()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            rawFoodRepository.getAllProduceCategories().collect { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }
    }

    private fun loadActiveProfile() {
        viewModelScope.launch {
            profileRepository.getAllProfiles().collect { profiles ->
                _uiState.update { it.copy(activeProfile = profiles.firstOrNull()) }
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeRawFoods() {
        viewModelScope.launch {
            combine(
                _query.debounce(300L), // Debounce search query to prevent input lag
                _selectedCategory,
                _nutrientFilter,
                _sourceFilter
            ) { q, cat, nFilter, src ->
                ProduceFilterParams(q, cat, nFilter, src)
            }
            .onEach { _uiState.update { it.copy(isLoading = true) } }
            .flatMapLatest { params ->
                rawFoodRepository.searchRawFoods(params.query, params.category).map { list ->
                    var filtered = list

                    if (params.source.isNotBlank()) {
                        filtered = filtered.filter { it.source.contains(params.source, ignoreCase = true) }
                    }

                    when (params.nutrientFilter) {
                        NutrientFilter.IRON_RICH -> filtered.filter { it.iron >= 2.5 }
                        NutrientFilter.VIT_C_RICH -> filtered.filter { it.vitC >= 40.0 }
                        NutrientFilter.HIGH_FIBER -> filtered.filter { it.fiber >= 5.0 }
                        NutrientFilter.HIGH_PROTEIN -> filtered.filter { it.protein >= 8.0 }
                        NutrientFilter.LOW_POTASSIUM -> filtered.filter { (it.micronutrients.potassium_mg ?: 999.0) <= 200.0 }
                        NutrientFilter.LOW_CALORIE -> filtered.filter { it.energyKcal <= 40.0 }
                        NutrientFilter.ALL -> filtered
                    }
                }
            }.collect { results ->
                _uiState.update {
                    it.copy(
                        query = _query.value,
                        selectedCategory = _selectedCategory.value,
                        selectedNutrientFilter = _nutrientFilter.value,
                        selectedSourceFilter = _sourceFilter.value,
                        rawFoods = results,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        // Immediately update the displayed text for responsive typing
        _uiState.update { it.copy(query = newQuery) }
        _query.value = newQuery
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = if (_selectedCategory.value == category) "" else category
    }

    fun selectNutrientFilter(filter: NutrientFilter) {
        _nutrientFilter.value = filter
    }

    fun selectSourceFilter(source: String) {
        _sourceFilter.value = if (_sourceFilter.value == source) "" else source
    }

    fun openAddToPlateModal(food: RawFoodItem) {
        _uiState.update {
            it.copy(
                selectedFoodForPlate = food,
                customServingGrams = food.servingG
            )
        }
    }

    fun closeAddToPlateModal() {
        _uiState.update { it.copy(selectedFoodForPlate = null) }
    }

    fun updateCustomServingGrams(grams: Double) {
        _uiState.update { it.copy(customServingGrams = grams) }
    }

    fun confirmAddToPlate() {
        val food = _uiState.value.selectedFoodForPlate ?: return
        val profile = _uiState.value.activeProfile ?: return
        val grams = _uiState.value.customServingGrams

        viewModelScope.launch {
            plateRepository.addToPlate(profile.id, food.fdcId, grams)
            _uiState.update {
                it.copy(
                    selectedFoodForPlate = null,
                    addedSuccessMessage = "Added ${String.format("%.0f", grams)}g of ${food.name} to Plate!"
                )
            }
        }
    }

    fun dismissSuccessMessage() {
        _uiState.update { it.copy(addedSuccessMessage = null) }
    }

    private data class ProduceFilterParams(
        val query: String,
        val category: String,
        val nutrientFilter: NutrientFilter,
        val source: String
    )
}
