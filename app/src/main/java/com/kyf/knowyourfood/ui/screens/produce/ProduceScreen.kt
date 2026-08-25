package com.kyf.knowyourfood.ui.screens.produce

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.model.RawFoodItem
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.MacroPill
import com.kyf.knowyourfood.ui.components.ServingSlider
import com.kyf.knowyourfood.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduceScreen(
    viewModel: ProduceViewModel,
    onNavigateToPlate: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Show Snackbar when item is added to plate
    LaunchedEffect(state.addedSuccessMessage) {
        state.addedSuccessMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissSuccessMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate950)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            // Search & Plate Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Global Produce & Whole Foods",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "USDA Foundation + SR Legacy + INDB 2024",
                        fontSize = 12.sp,
                        color = Emerald400
                    )
                }

                Button(
                    onClick = onNavigateToPlate,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Slate950)
                ) {
                    Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Plate", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar with proper keyboard actions
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onQueryChanged(it) },
                placeholder = { Text("Search fruits, vegetables, lentils, seeds...", color = Slate400) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Emerald400)
                },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChanged("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Slate400)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Slate900,
                    unfocusedContainerColor = Slate900,
                    focusedBorderColor = Emerald500,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Emerald400
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChip(
                        selected = state.selectedCategory.isEmpty(),
                        onClick = { viewModel.selectCategory("") },
                        label = { Text("All Categories") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500,
                            selectedLabelColor = Slate950,
                            containerColor = Slate900,
                            labelColor = Color.White
                        )
                    )
                }
                items(state.categories) { cat ->
                    FilterChip(
                        selected = state.selectedCategory == cat,
                        onClick = { viewModel.selectCategory(cat) },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500,
                            selectedLabelColor = Slate950,
                            containerColor = Slate900,
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Targeted Nutrient Filters Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(NutrientFilter.entries) { filter ->
                    val isSelected = state.selectedNutrientFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectNutrientFilter(filter) },
                        label = { Text(filter.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Cyan500,
                            selectedLabelColor = Slate950,
                            containerColor = Slate900,
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Data Source Filter (USDA vs INDB)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.selectedSourceFilter == "USDA",
                    onClick = { viewModel.selectSourceFilter("USDA") },
                    label = { Text("🇺🇸 USDA Foundation/SR") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Emerald600,
                        selectedLabelColor = Color.White,
                        containerColor = Slate900,
                        labelColor = Slate300
                    )
                )
                FilterChip(
                    selected = state.selectedSourceFilter == "INDB",
                    onClick = { viewModel.selectSourceFilter("INDB") },
                    label = { Text("🇮🇳 INDB 2024 (Indian Database)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Emerald600,
                        selectedLabelColor = Color.White,
                        containerColor = Slate900,
                        labelColor = Slate300
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Results count
            Text(
                text = "${state.rawFoods.size} Whole Foods Available",
                fontSize = 12.sp,
                color = Slate400
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Content area with loading state
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Emerald400,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Loading produce database...",
                                fontSize = 13.sp,
                                color = Slate400
                            )
                        }
                    }
                } else if (state.rawFoods.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = Slate600,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Produce Found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate400
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try adjusting your search or filter criteria.",
                                fontSize = 13.sp,
                                color = Slate500,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Raw Foods List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.rawFoods, key = { it.fdcId }) { food ->
                            RawFoodCard(
                                food = food,
                                onAddToPlate = { viewModel.openAddToPlateModal(food) }
                            )
                        }
                    }
                }
            }

            // Add to Plate Bottom Sheet Dialog
            if (state.selectedFoodForPlate != null) {
                val food = state.selectedFoodForPlate!!
                val scaled = food.scaleTo(state.customServingGrams)

                ModalBottomSheet(
                    onDismissRequest = { viewModel.closeAddToPlateModal() },
                    containerColor = Slate900,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = food.category.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald400
                                )
                                Text(
                                    text = food.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Slate800)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = food.source, fontSize = 10.sp, color = Slate300)
                            }
                        }

                        // Interactive Serving Slider
                        ServingSlider(
                            currentGrams = state.customServingGrams,
                            onGramsChanged = { viewModel.updateCustomServingGrams(it) }
                        )

                        // Scaled Real-Time Nutritional Facts
                        Text(
                            text = "Scaled Nutrition for ${String.format("%.0f", scaled.grams)}g:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MacroPill(label = "Energy", value = "${scaled.energyKcal.toInt()} kcal", accentColor = Emerald400, modifier = Modifier.weight(1f))
                            MacroPill(label = "Protein", value = "${String.format("%.1f", scaled.protein)}g", accentColor = Cyan400, modifier = Modifier.weight(1f))
                            MacroPill(label = "Fiber", value = "${String.format("%.1f", scaled.fiber)}g", accentColor = TrafficYellow, modifier = Modifier.weight(1f))
                            MacroPill(label = "Vit C", value = "${String.format("%.1f", scaled.vitC)}mg", accentColor = Color(0xFFF472B6), modifier = Modifier.weight(1f))
                        }

                        // Confirm Add to Plate CTA Button
                        Button(
                            onClick = { viewModel.confirmAddToPlate() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add ${String.format("%.0f", state.customServingGrams)}g to Plate",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Snackbar host for success messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Emerald500,
                    contentColor = Slate950,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        )
    }
}

@Composable
fun RawFoodCard(
    food: RawFoodItem,
    onAddToPlate: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Slate900
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = food.category.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald400
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${food.source}",
                            fontSize = 10.sp,
                            color = Slate400
                        )
                    }
                    Text(
                        text = food.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onAddToPlate,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add to Plate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Per 100g Macro & Micro Overview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MacroPill(label = "Cal", value = "${food.energyKcal.toInt()} kcal", accentColor = Color.White, modifier = Modifier.weight(1f))
                MacroPill(label = "Protein", value = "${food.protein}g", accentColor = Cyan400, modifier = Modifier.weight(1f))
                MacroPill(label = "Carbs", value = "${food.carbs}g", accentColor = Emerald400, modifier = Modifier.weight(1f))
                MacroPill(label = "Fiber", value = "${food.fiber}g", accentColor = TrafficYellow, modifier = Modifier.weight(1f))
                MacroPill(label = "Iron", value = "${food.iron}mg", accentColor = Color(0xFFF472B6), modifier = Modifier.weight(1f))
                MacroPill(label = "Vit C", value = "${food.vitC}mg", accentColor = Color(0xFF60A5FA), modifier = Modifier.weight(1f))
            }

            // Cross-reactivity or special notes if present
            if (food.micronutrients.allergenic_pollen_cross.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Slate800)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️ Pollen Cross-Reactivity: ${food.micronutrients.allergenic_pollen_cross.joinToString()}",
                        fontSize = 11.sp,
                        color = TrafficYellow
                    )
                }
            }
        }
    }
}
