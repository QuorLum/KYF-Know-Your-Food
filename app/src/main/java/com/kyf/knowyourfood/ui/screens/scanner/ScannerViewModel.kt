package com.kyf.knowyourfood.ui.screens.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.ProductItem
import com.kyf.knowyourfood.data.model.SafetyAssessment
import com.kyf.knowyourfood.data.repository.ProductRepository
import com.kyf.knowyourfood.data.repository.ProfileRepository
import com.kyf.knowyourfood.domain.engine.AllergyEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScannerUiState(
    val scannedBarcode: String? = null,
    val scannedProduct: ProductItem? = null,
    val safetyAssessment: SafetyAssessment? = null,
    val activeProfile: ProfileEntity? = null,
    val isScanning: Boolean = true,
    val showManualInputDialog: Boolean = false,
    val testProducts: List<ProductItem> = emptyList(),
    val errorMessage: String? = null
)

class ScannerViewModel(
    private val productRepository: ProductRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            productRepository.getAllProducts().collect { products ->
                _uiState.update { it.copy(testProducts = products) }
            }
        }
        viewModelScope.launch {
            profileRepository.getAllProfiles().collect { profiles ->
                _uiState.update { it.copy(activeProfile = profiles.firstOrNull()) }
            }
        }
    }

    fun onBarcodeDetected(barcode: String) {
        val cleanBarcode = barcode.trim()
        if (cleanBarcode.isEmpty()) return

        viewModelScope.launch {
            val product = productRepository.getProductByBarcode(cleanBarcode)
            val profile = _uiState.value.activeProfile

            if (product != null && profile != null) {
                // Use the entity directly from the repository for engine evaluation
                // This avoids the anti-pattern of re-serializing domain objects back to entities
                val entity = productRepository.getProductEntityByBarcode(cleanBarcode)
                val assessment = if (entity != null) {
                    AllergyEngine.evaluateProductSafety(entity, profile)
                } else null

                _uiState.update {
                    it.copy(
                        scannedBarcode = cleanBarcode,
                        scannedProduct = product,
                        safetyAssessment = assessment,
                        isScanning = false,
                        errorMessage = null
                    )
                }
            } else if (product != null) {
                _uiState.update {
                    it.copy(
                        scannedBarcode = cleanBarcode,
                        scannedProduct = product,
                        safetyAssessment = null,
                        isScanning = false,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        scannedBarcode = cleanBarcode,
                        scannedProduct = null,
                        safetyAssessment = null,
                        errorMessage = "Barcode '$cleanBarcode' not found in local database."
                    )
                }
            }
        }
    }

    fun openManualInputDialog() {
        _uiState.update { it.copy(showManualInputDialog = true) }
    }

    fun closeManualInputDialog() {
        _uiState.update { it.copy(showManualInputDialog = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resumeScanning() {
        _uiState.update {
            it.copy(
                scannedBarcode = null,
                scannedProduct = null,
                safetyAssessment = null,
                isScanning = true,
                errorMessage = null
            )
        }
    }
}
