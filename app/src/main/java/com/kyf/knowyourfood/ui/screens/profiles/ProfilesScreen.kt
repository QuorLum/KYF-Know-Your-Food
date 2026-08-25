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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.AllergyProfile
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.ScreenHeader
import com.kyf.knowyourfood.ui.theme.*
import kotlinx.serialization.json.Json

@Composable
fun ProfilesScreen(
    viewModel: ProfilesViewModel,
    onNavigateToSettings: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val json = remember { Json { ignoreUnknownKeys = true } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Screen Header
        ScreenHeader(
            title = "Family Profiles",
            right = {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.openCreateProfile() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Emerald400, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.profiles, key = { it.id }) { profile ->
                val isActive = profile.id == state.activeProfileId
                val allergyProfile = remember(profile.allergiesJson) {
                    try {
                        json.decodeFromString<AllergyProfile>(profile.allergiesJson)
                    } catch (e: Exception) {
                        AllergyProfile()
                    }
                }

                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (isActive) Modifier.border(1.5.dp, Emerald400.copy(alpha = 0.5f), RoundedCornerShape(24.dp)) else Modifier),
                    onClick = { viewModel.openEditProfile(profile) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val avatarUrl = profile.avatarPath ?: getFallbackAvatar(profile.id, profile.name)
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = profile.name,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .border(if (isActive) 1.5.dp else 1.dp, if (isActive) Emerald400 else Color.White.copy(alpha = 0.15f), CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(Family Member)",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.45f)
                                    )
                                    if (isActive) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Emerald400)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("ACTIVE", fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF04220F))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "${profile.age} yrs · ${profile.gender} · ${profile.height.toInt()}cm · ${profile.weight.toInt()}kg",
                                    fontSize = 11.5.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Allergen & condition chips
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val allergens = allergyProfile.allergens
                                    if (allergens.isEmpty()) {
                                        Text("No restrictions", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
                                    } else {
                                        allergens.take(3).forEach { a ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(TrafficRed.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(text = a, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFCA5A5))
                                            }
                                        }
                                    }
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Switch to {name} button if not active
                        if (!isActive) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .clickable { viewModel.selectProfile(profile.id) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Switch to ${profile.name}", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Emerald300)
                            }
                        }
                    }
                }
            }

            // Settings & Preferences Card Link
            item {
                Spacer(modifier = Modifier.height(6.dp))
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToSettings
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Emerald400, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Settings & Preferences", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Units, language, theme, offline data", fontSize = 11.5.sp, color = Color.White.copy(alpha = 0.45f))
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Create / Edit Profile Bottom Sheet Dialog
    if (state.showEditDialog) {
        ProfileEditModal(
            viewModel = viewModel,
            onDismiss = { viewModel.closeEditDialog() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileEditModal(
    viewModel: ProfilesViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val isNew = state.isCreatingNew
    val title = if (isNew) "New Profile" else "Edit Profile"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

            OutlinedTextField(
                value = state.draftName,
                onValueChange = { viewModel.onNameChanged(it) },
                label = { Text("Full Name", color = Slate400) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Emerald400,
                    unfocusedBorderColor = Slate700
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.draftAge,
                    onValueChange = { viewModel.onAgeChanged(it) },
                    label = { Text("Age (yrs)", color = Slate400) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Emerald400,
                        unfocusedBorderColor = Slate700
                    ),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = state.draftGender,
                    onValueChange = { viewModel.onGenderChanged(it) },
                    label = { Text("Gender", color = Slate400) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Emerald400,
                        unfocusedBorderColor = Slate700
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.draftWeight,
                    onValueChange = { viewModel.onWeightChanged(it) },
                    label = { Text("Weight (kg)", color = Slate400) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Emerald400,
                        unfocusedBorderColor = Slate700
                    ),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = state.draftHeight,
                    onValueChange = { viewModel.onHeightChanged(it) },
                    label = { Text("Height (cm)", color = Slate400) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Emerald400,
                        unfocusedBorderColor = Slate700
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = { viewModel.saveDraftProfile() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950)
            ) {
                Text(if (isNew) "Create Profile" else "Save Changes", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun getFallbackAvatar(id: Long, name: String): String {
    return when (name.lowercase()) {
        "divyanshu" -> "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=160&h=160&fit=crop&crop=faces&auto=format"
        "ananya" -> "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=160&h=160&fit=crop&crop=faces&auto=format"
        "mother" -> "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=160&h=160&fit=crop&crop=faces&auto=format"
        "father" -> "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=160&h=160&fit=crop&crop=faces&auto=format"
        "grandfather" -> "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=160&h=160&fit=crop&crop=faces&auto=format"
        else -> "https://api.dicebear.com/9.x/adventurer/svg?seed=${name}&backgroundColor=b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf&radius=50"
    }
}
