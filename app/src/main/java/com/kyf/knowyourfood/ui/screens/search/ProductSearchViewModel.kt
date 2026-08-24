package com.kyf.knowyourfood.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyf.knowyourfood.data.model.ProductItem
import com.kyf.knowyourfood.data.repository.ProductRepository
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
    val isLoading: Boolean = false
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

    private fun observeProducts() {
        viewModelScope.launch {
            _filterParams.flatMapLatest { params ->
                productRepository.searchProducts(params.query, params.nutriScore, params.category).map { list ->
                    var filtered = list

                    if (params.allergenFree.isNotBlank()) {
                        filtered = filtered.filter { p ->
                            p.allergenTags.contains.none { it.contains(params.allergenFree, ignoreCase = true) } &&
                            !p.ingredientsText.contains(params.allergenFree, ignoreCase = true)
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

    fun onQueryChanged(newQuery: String) {
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
    }
}
