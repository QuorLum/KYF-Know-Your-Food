package com.kyf.knowyourfood.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyf.knowyourfood.data.model.NutriScoreGrade
import com.kyf.knowyourfood.data.model.ProductItem
import com.kyf.knowyourfood.data.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val selectedNutriScore: String = "",
    val selectedCategory: String = "",
    val filterAllergenFree: String = "", // e.g. "GLUTEN", "MILK", "PEANUT"
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

    private val _query = MutableStateFlow("")
    private val _selectedNutriScore = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("")
    private val _filterAllergenFree = MutableStateFlow("")
    private val _filterHighProtein = MutableStateFlow(false)
    private val _filterLowSugar = MutableStateFlow(false)
    private val _filterLowSalt = MutableStateFlow(false)

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
            combine(
                _query,
                _selectedNutriScore,
                _selectedCategory,
                _filterAllergenFree,
                _filterHighProtein,
                _filterLowSugar,
                _filterLowSalt
            ) { q, ns, cat, allergenFree, highProtein, lowSugar, lowSalt ->
                FilterParams(q, ns, cat, allergenFree, highProtein, lowSugar, lowSalt)
            }.flatMapLatest { params ->
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
                _uiState.update {
                    it.copy(
                        query = _query.value,
                        selectedNutriScore = _selectedNutriScore.value,
                        selectedCategory = _selectedCategory.value,
                        filterAllergenFree = _filterAllerfreeText(),
                        filterHighProtein = _filterHighProtein.value,
                        filterLowSugar = _filterLowSugar.value,
                        filterLowSalt = _filterLowSalt.value,
                        products = results,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun _filterAllerfreeText() = _filterAllergenFree.value

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun selectNutriScoreFilter(score: String) {
        _selectedNutriScore.value = if (_selectedNutriScore.value == score) "" else score
    }

    fun selectCategoryFilter(cat: String) {
        _selectedCategory.value = if (_selectedCategory.value == cat) "" else cat
    }

    fun setAllergenFreeFilter(allergen: String) {
        _filterAllergenFree.value = if (_filterAllergenFree.value == allergen) "" else allergen
    }

    fun toggleHighProtein() {
        _filterHighProtein.value = !_filterHighProtein.value
    }

    fun toggleLowSugar() {
        _filterLowSugar.value = !_filterLowSugar.value
    }

    fun toggleLowSalt() {
        _filterLowSalt.value = !_filterLowSalt.value
    }

    fun resetFilters() {
        _query.value = ""
        _selectedNutriScore.value = ""
        _selectedCategory.value = ""
        _filterAllergenFree.value = ""
        _filterHighProtein.value = false
        _filterLowSugar.value = false
        _filterLowSalt.value = false
    }

    private data class FilterParams(
        val query: String,
        val nutriScore: String,
        val category: String,
        val allergenFree: String,
        val highProtein: Boolean,
        val lowSugar: Boolean,
        val lowSalt: Boolean
    )
}
