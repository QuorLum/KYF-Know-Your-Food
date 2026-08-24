package com.kyf.knowyourfood.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.*
import com.kyf.knowyourfood.data.repository.PlateRepository
import com.kyf.knowyourfood.data.repository.ProductRepository
import com.kyf.knowyourfood.data.repository.ProfileRepository
import com.kyf.knowyourfood.domain.engine.NutrientCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val profiles: List<ProfileEntity> = emptyList(),
    val activeProfile: ProfileEntity? = null,
    val recentProducts: List<ProductItem> = emptyList(),
    val plateItems: List<PlateItemWithFood> = emptyList(),
    val plateTotals: PlateNutritionTotals? = null,
    val featuredHealthySwaps: List<Pair<ProductItem, ProductItem>> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val profileRepository: ProfileRepository,
    private val productRepository: ProductRepository,
    private val plateRepository: PlateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedProfileId = MutableStateFlow<Long?>(null)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                profileRepository.getAllProfiles(),
                _selectedProfileId,
                productRepository.getAllProducts()
            ) { profiles, selectedId, products ->
                val active = profiles.find { it.id == selectedId } ?: profiles.firstOrNull()
                val topProducts = products.take(6)

                // Healthier swaps: Pair D/E products with A/B alternatives
                val swaps = mutableListOf<Pair<ProductItem, ProductItem>>()
                val unhealthy = products.filter { it.nutriScore == NutriScoreGrade.D || it.nutriScore == NutriScoreGrade.E }
                val healthy = products.filter { it.nutriScore == NutriScoreGrade.A || it.nutriScore == NutriScoreGrade.B }
                for (u in unhealthy.take(3)) {
                    val match = healthy.find { it.category == u.category } ?: healthy.firstOrNull()
                    if (match != null) {
                        swaps.add(Pair(u, match))
                    }
                }

                Triple(profiles, active, Pair(topProducts, swaps))
            }.collect { (profiles, active, productData) ->
                _uiState.update { current ->
                    current.copy(
                        profiles = profiles,
                        activeProfile = active,
                        recentProducts = productData.first,
                        featuredHealthySwaps = productData.second,
                        isLoading = false
                    )
                }

                if (active != null) {
                    observePlateForProfile(active.id)
                }
            }
        }
    }

    fun selectProfile(profileId: Long) {
        _selectedProfileId.value = profileId
        observePlateForProfile(profileId)
    }

    private fun observePlateForProfile(profileId: Long) {
        viewModelScope.launch {
            plateRepository.getPlateItemsForProfile(profileId).collect { items ->
                val totals = NutrientCalculator.calculatePlateTotals(items)
                _uiState.update { it.copy(plateItems = items, plateTotals = totals) }
            }
        }
    }
}
