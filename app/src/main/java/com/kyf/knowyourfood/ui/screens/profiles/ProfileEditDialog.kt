package com.kyf.knowyourfood.ui.screens.profiles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.*
import com.kyf.knowyourfood.ui.theme.*
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditDialog(
    profile: ProfileEntity?,
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onSave: (id: Long?, name: String, age: Int, gender: String, weight: Double, height: Double, allergens: List<String>, pollen: List<String>, conditions: List<String>, strictTraces: Boolean) -> Unit
) {
    val initialAllergyProfile = remember(profile) {
        if (profile != null) {
            try {
                Json { ignoreUnknownKeys = true }.decodeFromString<AllergyProfile>(profile.allergiesJson)
            } catch (e: Exception) {
                AllergyProfile()
            }
        } else {
            AllergyProfile()
        }
    }

    var name by remember { mutableStateOf(profile?.name ?: "") }
    var ageText by remember { mutableStateOf(profile?.age?.toString() ?: "25") }
    var gender by remember { mutableStateOf(profile?.gender ?: "Male") }
    var weightText by remember { mutableStateOf(profile?.weight?.toString() ?: "70.0") }
    var heightText by remember { mutableStateOf(profile?.height?.toString() ?: "175.0") }

    val selectedAllergens = remember { mutableStateListOf<String>().apply { addAll(initialAllergyProfile.allergens) } }
    val selectedPollen = remember { mutableStateListOf<String>().apply { addAll(initialAllergyProfile.pollenSensitivities) } }
    val selectedConditions = remember { mutableStateListOf<String>().apply { addAll(initialAllergyProfile.conditions) } }
    var strictTraces by remember { mutableStateOf(initialAllergyProfile.strictTraces) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(20.dp)),
            color = Slate900,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isCreating) "Create Health Profile" else "Edit Profile & Allergies",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Basic Information
                    item {
                        Text(text = "Basic Information", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Profile Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate850,
                                unfocusedContainerColor = Slate850,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Emerald400
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = ageText,
                                onValueChange = { ageText = it },
                                label = { Text("Age (yrs)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate850,
                                    unfocusedContainerColor = Slate850,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Emerald400
                                )
                            )
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it },
                                label = { Text("Weight (kg)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate850,
                                    unfocusedContainerColor = Slate850,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Emerald400
                                )
                            )
                            OutlinedTextField(
                                value = heightText,
                                onValueChange = { heightText = it },
                                label = { Text("Height (cm)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate850,
                                    unfocusedContainerColor = Slate850,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Emerald400
                                )
                            )
                        }
                    }

                    // Strict Traces Mode Toggle
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate850)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Strict Trace Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = "Flag 'May contain traces' as high-risk allergen hazard", fontSize = 11.sp, color = Slate400)
                            }
                            Switch(
                                checked = strictTraces,
                                onCheckedChange = { strictTraces = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald400, checkedTrackColor = Emerald700)
                            )
                        }
                    }

                    // Regulated Major Allergens (FDA 9 / EU 14 / FSSAI 8)
                    item {
                        Text(text = "Regulated Major Allergens (FDA 9 / EU 14 / FSSAI 8)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                        Spacer(modifier = Modifier.height(6.dp))
                        MajorAllergen.entries.forEach { allergen ->
                            val isChecked = selectedAllergens.contains(allergen.id)
                            CheckboxRow(
                                title = allergen.displayName,
                                subtitle = allergen.category,
                                isChecked = isChecked,
                                onToggle = {
                                    if (isChecked) selectedAllergens.remove(allergen.id) else selectedAllergens.add(allergen.id)
                                }
                            )
                        }
                    }

                    // Pollen-Food Cross-Reactivity Syndromes (OAS)
                    item {
                        Text(text = "Pollen Cross-Reactivity (Oral Allergy Syndrome)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Cyan400)
                        Spacer(modifier = Modifier.height(6.dp))
                        PollenSyndrome.entries.forEach { syndrome ->
                            val isChecked = selectedPollen.contains(syndrome.id)
                            CheckboxRow(
                                title = syndrome.displayName,
                                subtitle = "Triggers: ${syndrome.crossFoods}",
                                isChecked = isChecked,
                                onToggle = {
                                    if (isChecked) selectedPollen.remove(syndrome.id) else selectedPollen.add(syndrome.id)
                                }
                            )
                        }
                    }

                    // Special Non-IgE Conditions
                    item {
                        Text(text = "Special Conditions & Non-IgE Sensitivities", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TrafficYellow)
                        Spacer(modifier = Modifier.height(6.dp))
                        NonIgECondition.entries.forEach { condition ->
                            val isChecked = selectedConditions.contains(condition.id)
                            CheckboxRow(
                                title = condition.displayName,
                                subtitle = condition.description,
                                isChecked = isChecked,
                                onToggle = {
                                    if (isChecked) selectedConditions.remove(condition.id) else selectedConditions.add(condition.id)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Save CTA Button
                Button(
                    onClick = {
                        val age = ageText.toIntOrNull() ?: 25
                        val weight = weightText.toDoubleOrNull() ?: 70.0
                        val height = heightText.toDoubleOrNull() ?: 175.0
                        onSave(
                            profile?.id,
                            if (name.isBlank()) "User Profile" else name,
                            age,
                            gender,
                            weight,
                            height,
                            selectedAllergens.toList(),
                            selectedPollen.toList(),
                            selectedConditions.toList(),
                            strictTraces
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950)
                ) {
                    Text("Save Profile & Allergy Guard", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CheckboxRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isChecked) Emerald500.copy(alpha = 0.15f) else Slate850)
            .border(1.dp, if (isChecked) Emerald500 else Slate800, RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = Emerald500, uncheckedColor = Slate500)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(text = subtitle, fontSize = 11.sp, color = Slate400)
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}
