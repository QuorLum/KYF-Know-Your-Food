package com.kyf.knowyourfood.ui.screens.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.AllergyProfile
import com.kyf.knowyourfood.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class ProfilesUiState(
    val profiles: List<ProfileEntity> = emptyList(),
    val editingProfile: ProfileEntity? = null,
    val isCreatingNew: Boolean = false,
    val showEditDialog: Boolean = false
)

class ProfilesViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val json = Json { ignoreUnknownKeys = true }

    private val _uiState = MutableStateFlow(ProfilesUiState())
    val uiState: StateFlow<ProfilesUiState> = _uiState.asStateFlow()

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            profileRepository.getAllProfiles().collect { list ->
                _uiState.update { it.copy(profiles = list) }
            }
        }
    }

    fun openCreateProfile() {
        _uiState.update {
            it.copy(
                editingProfile = null,
                isCreatingNew = true,
                showEditDialog = true
            )
        }
    }

    fun openEditProfile(profile: ProfileEntity) {
        _uiState.update {
            it.copy(
                editingProfile = profile,
                isCreatingNew = false,
                showEditDialog = true
            )
        }
    }

    fun closeEditDialog() {
        _uiState.update { it.copy(showEditDialog = false, editingProfile = null) }
    }

    fun saveProfile(
        id: Long?,
        name: String,
        age: Int,
        gender: String,
        weight: Double,
        height: Double,
        allergens: List<String>,
        pollen: List<String>,
        conditions: List<String>,
        strictTraces: Boolean
    ) {
        val allergyProfile = AllergyProfile(
            allergens = allergens,
            pollenSensitivities = pollen,
            conditions = conditions,
            strictTraces = strictTraces
        )
        val allergiesJson = json.encodeToString(allergyProfile)

        viewModelScope.launch {
            if (id != null && id > 0) {
                val updated = ProfileEntity(
                    id = id,
                    name = name,
                    avatarPath = _uiState.value.editingProfile?.avatarPath,
                    age = age,
                    gender = gender,
                    weight = weight,
                    height = height,
                    allergiesJson = allergiesJson
                )
                profileRepository.updateProfile(updated)
            } else {
                val newEntity = ProfileEntity(
                    name = name,
                    avatarPath = "avatar_user",
                    age = age,
                    gender = gender,
                    weight = weight,
                    height = height,
                    allergiesJson = allergiesJson
                )
                profileRepository.insertProfile(newEntity)
            }
            closeEditDialog()
        }
    }

    fun deleteProfile(profile: ProfileEntity) {
        viewModelScope.launch {
            profileRepository.deleteProfile(profile)
        }
    }
}
