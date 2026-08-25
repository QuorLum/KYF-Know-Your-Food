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
    val activeProfileId: Long = 1L,
    val editingProfile: ProfileEntity? = null,
    val isCreatingNew: Boolean = false,
    val showEditDialog: Boolean = false,
    val draftName: String = "",
    val draftAge: String = "25",
    val draftGender: String = "Male",
    val draftWeight: String = "65",
    val draftHeight: String = "170"
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
                val activeId = if (_uiState.value.activeProfileId != 1L) _uiState.value.activeProfileId else list.firstOrNull()?.id ?: 1L
                _uiState.update { it.copy(profiles = list, activeProfileId = activeId) }
            }
        }
    }

    fun selectProfile(id: Long) {
        _uiState.update { it.copy(activeProfileId = id) }
    }

    fun openCreateProfile() {
        _uiState.update {
            it.copy(
                editingProfile = null,
                isCreatingNew = true,
                showEditDialog = true,
                draftName = "",
                draftAge = "25",
                draftGender = "Male",
                draftWeight = "65",
                draftHeight = "170"
            )
        }
    }

    fun openEditProfile(profile: ProfileEntity) {
        _uiState.update {
            it.copy(
                editingProfile = profile,
                isCreatingNew = false,
                showEditDialog = true,
                draftName = profile.name,
                draftAge = profile.age.toString(),
                draftGender = profile.gender,
                draftWeight = profile.weight.toInt().toString(),
                draftHeight = profile.height.toInt().toString()
            )
        }
    }

    fun closeEditDialog() {
        _uiState.update { it.copy(showEditDialog = false, editingProfile = null, isCreatingNew = false) }
    }

    fun onNameChanged(name: String) = _uiState.update { it.copy(draftName = name) }
    fun onAgeChanged(age: String) = _uiState.update { it.copy(draftAge = age) }
    fun onGenderChanged(gender: String) = _uiState.update { it.copy(draftGender = gender) }
    fun onWeightChanged(weight: String) = _uiState.update { it.copy(draftWeight = weight) }
    fun onHeightChanged(height: String) = _uiState.update { it.copy(draftHeight = height) }

    fun saveDraftProfile() {
        val state = _uiState.value
        val name = state.draftName.ifBlank { "Family Member" }
        val age = state.draftAge.toIntOrNull() ?: 25
        val gender = state.draftGender.ifBlank { "Male" }
        val weight = state.draftWeight.toDoubleOrNull() ?: 65.0
        val height = state.draftHeight.toDoubleOrNull() ?: 170.0

        saveProfile(
            id = state.editingProfile?.id,
            name = name,
            age = age,
            gender = gender,
            weight = weight,
            height = height,
            allergens = emptyList(),
            pollen = emptyList(),
            conditions = emptyList(),
            strictTraces = false
        )
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
                    avatarPath = null,
                    age = age,
                    gender = gender,
                    weight = weight,
                    height = height,
                    allergiesJson = allergiesJson
                )
                val newId = profileRepository.insertProfile(newEntity)
                _uiState.update { it.copy(activeProfileId = newId) }
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
