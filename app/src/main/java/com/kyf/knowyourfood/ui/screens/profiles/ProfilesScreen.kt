package com.kyf.knowyourfood.ui.screens.profiles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.AllergyProfile
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.theme.*
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    viewModel: ProfilesViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val json = remember { Json { ignoreUnknownKeys = true } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family & Allergy Profiles", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                actions = {
                    IconButton(
                        onClick = { viewModel.openCreateProfile() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Emerald500)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Profile", tint = Slate950)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate950)
            )
        },
        containerColor = Slate950
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Manage allergen guardrails for everyone in your household. Select any profile at scan time for immediate personalized protection.",
                    fontSize = 13.sp,
                    color = Slate400
                )
            }

            items(state.profiles, key = { it.id }) { profile ->
                val allergyData = remember(profile.allergiesJson) {
                    try {
                        json.decodeFromString<AllergyProfile>(profile.allergiesJson)
                    } catch (e: Exception) {
                        AllergyProfile()
                    }
                }

                ProfileCard(
                    profile = profile,
                    allergyProfile = allergyData,
                    onEdit = { viewModel.openEditProfile(profile) },
                    onDelete = {
                        if (state.profiles.size > 1) {
                            viewModel.deleteProfile(profile)
                        }
                    },
                    canDelete = state.profiles.size > 1
                )
            }
        }

        if (state.showEditDialog) {
            ProfileEditDialog(
                profile = state.editingProfile,
                isCreating = state.isCreatingNew,
                onDismiss = { viewModel.closeEditDialog() },
                onSave = { id, name, age, gender, weight, height, allergens, pollen, conditions, strictTraces ->
                    viewModel.saveProfile(id, name, age, gender, weight, height, allergens, pollen, conditions, strictTraces)
                }
            )
        }
    }
}

@Composable
fun ProfileCard(
    profile: ProfileEntity,
    allergyProfile: AllergyProfile,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Slate900
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Emerald500.copy(alpha = 0.2f))
                            .border(1.dp, Emerald400, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (profile.age < 12) Icons.Default.ChildCare else Icons.Default.Person,
                            contentDescription = null,
                            tint = Emerald400,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = profile.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${profile.age} yrs • ${profile.weight} kg • ${profile.height} cm",
                            fontSize = 12.sp,
                            color = Slate400
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Emerald400)
                    }
                    if (canDelete) {
                        IconButton(onClick = onDelete) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TrafficRed)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Configured Allergens
            if (allergyProfile.allergens.isNotEmpty()) {
                Text(text = "Allergies:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TrafficRed)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    allergyProfile.allergens.forEach {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x33EF4444))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = it, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Configured Pollen / Conditions
            if (allergyProfile.pollenSensitivities.isNotEmpty() || allergyProfile.conditions.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allergyProfile.pollenSensitivities.forEach {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x3306B6D4))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "OAS: $it", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Cyan400)
                        }
                    }
                    allergyProfile.conditions.forEach {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x33F59E0B))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = it, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TrafficYellow)
                        }
                    }
                }
            }
        }
    }
}
