package com.kyf.knowyourfood.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyf.knowyourfood.data.model.ProductItem
import com.kyf.knowyourfood.data.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val selectedNutriScore: String = "",
    val selectedCategory: String = "",
    val filterAllergenFree: String = "",
    val filterHighProtein: Boolean = false,
    val filterLowSugar: Boolean = false,
    val filterLowSalt: Boolean = false,
    val categories: List<String> = emptyList(),
    val products: List<ProductItem> = emptyList(),
    val isLoading: Boolean = true
)

class ProductSearchViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private data class FilterParams(
        val query: String = "",
        val nutriScore: String = "",
        val category: String = "",
        val allergenFree: String = "",
        val highProtein: Boolean = false,
        val lowSugar: Boolean = false,
        val lowSalt: Boolean = false
    )

    private val _filterParams = MutableStateFlow(FilterParams())
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        observeProducts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            productRepository.getAllCategories().collect { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeProducts() {
        viewModelScope.launch {
            _filterParams
                .debounce(300L) // Debounce to prevent rapid recomposition & input lag
                .onEach { _uiState.update { it.copy(isLoading = true) } }
                .flatMapLatest { params ->
                    productRepository.searchProducts(params.query, params.nutriScore, params.category).map { list ->
                        var filtered = list

                        if (params.allergenFree.isNotBlank()) {
                            val keywords = getAllergenKeywords(params.allergenFree)
                            filtered = filtered.filter { p ->
                                p.allergenTags.contains.none { tag ->
                                    keywords.any { kw -> tag.contains(kw, ignoreCase = true) }
                                } && keywords.none { kw ->
                                    p.ingredientsText.contains(kw, ignoreCase = true)
                                }
                            }
                        }

                        if (params.highProtein) {
                            filtered = filtered.filter { p -> p.protein100g >= 10.0 }
                        }

                        if (params.lowSugar) {
                            filtered = filtered.filter { p -> p.sugars100g <= 5.0 }
                        }

                        if (params.lowSalt) {
                            filtered = filtered.filter { p -> p.salt100g <= 0.3 }
                        }

                        filtered
                    }
                }.collect { results ->
                    val p = _filterParams.value
                    _uiState.update {
                        it.copy(
                            query = p.query,
                            selectedNutriScore = p.nutriScore,
                            selectedCategory = p.category,
                            filterAllergenFree = p.allergenFree,
                            filterHighProtein = p.highProtein,
                            filterLowSugar = p.lowSugar,
                            filterLowSalt = p.lowSalt,
                            products = results,
                            isLoading = false
                        )
                    }
                }
        }
    }

    /**
     * Maps allergen IDs to ingredient text keywords for accurate filtering.
     */
    private fun getAllergenKeywords(allergenId: String): List<String> {
        return when (allergenId.uppercase()) {
            "GLUTEN" -> listOf("gluten", "wheat", "barley", "rye", "malt", "spelt", "semolina", "durum", "seitan")
            "MILK" -> listOf("milk", "dairy", "casein", "whey", "butter", "cheese", "cream", "lactose", "yogurt", "ghee", "paneer")
            "PEANUT" -> listOf("peanut", "peanuts", "groundnut", "arachis")
            "TREE_NUTS" -> listOf("almond", "walnut", "cashew", "pistachio", "pecan", "hazelnut", "macadamia", "pine nut")
            "EGG" -> listOf("egg", "eggs", "albumin", "mayonnaise")
            "SOY", "SOYBEANS" -> listOf("soy", "soya", "soybean", "tofu", "tempeh", "edamame", "soy lecithin")
            "FISH" -> listOf("fish", "cod", "salmon", "tuna", "anchovy", "sardine")
            "SESAME" -> listOf("sesame", "tahini")
            else -> listOf(allergenId.lowercase())
        }
    }

    fun onQueryChanged(newQuery: String) {
        // Immediately update the displayed text for responsive typing
        _uiState.update { it.copy(query = newQuery) }
        _filterParams.update { it.copy(query = newQuery) }
    }

    fun selectNutriScoreFilter(score: String) {
        _filterParams.update {
            it.copy(nutriScore = if (it.nutriScore == score) "" else score)
        }
    }

    fun selectCategoryFilter(cat: String) {
        _filterParams.update {
            it.copy(category = if (it.category == cat) "" else cat)
        }
    }

    fun setAllergenFreeFilter(allergen: String) {
        _filterParams.update {
            it.copy(allergenFree = if (it.allergenFree == allergen) "" else allergen)
        }
    }

    fun toggleHighProtein() {
        _filterParams.update { it.copy(highProtein = !it.highProtein) }
    }

    fun toggleLowSugar() {
        _filterParams.update { it.copy(lowSugar = !it.lowSugar) }
    }

    fun toggleLowSalt() {
        _filterParams.update { it.copy(lowSalt = !it.lowSalt) }
    }

    fun resetFilters() {
        _filterParams.value = FilterParams()
        _uiState.update { it.copy(query = "") }
    }
}
