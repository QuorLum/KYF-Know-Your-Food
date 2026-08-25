package com.kyf.knowyourfood.ui.screens.food_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kyf.knowyourfood.data.model.RawFoodItem
import com.kyf.knowyourfood.data.repository.PlateRepository
import com.kyf.knowyourfood.data.repository.ProfileRepository
import com.kyf.knowyourfood.data.repository.RawFoodRepository
import com.kyf.knowyourfood.ui.components.FoodImageHelper
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.PrimaryButton
import com.kyf.knowyourfood.ui.components.ScreenHeader
import com.kyf.knowyourfood.ui.theme.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun FoodDetailScreen(
    foodId: Long,
    rawFoodRepository: RawFoodRepository,
    plateRepository: PlateRepository,
    profileRepository: ProfileRepository,
    onNavigateBack: () -> Unit,
    onNavigateToPlate: () -> Unit
) {
    var foodItem by remember { mutableStateOf<RawFoodItem?>(null) }
    var currentGrams by remember { mutableDoubleStateOf(100.0) }
    var isAdded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(foodId) {
        foodItem = rawFoodRepository.getRawFoodById(foodId)
        if (foodItem != null) {
            currentGrams = foodItem!!.servingG
        }
    }

    if (foodItem == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Emerald400, strokeWidth = 3.dp)
        }
        return
    }

    val food = foodItem!!
    val scaled = food.scaleTo(currentGrams)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Screen Header
        ScreenHeader(
            title = food.name,
            onBack = onNavigateBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Food Image Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(28.dp))
                ) {
                    val imageUrl = FoodImageHelper.getProduceImageUrl(food.name, food.category)
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = food.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "${FoodImageHelper.getProduceEmoji(food.name, food.category)} ${food.name}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${food.category} · ${food.source}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
            }

            // 2. Portion Size Slider
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Portion Size", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                text = "${currentGrams.toInt()} g",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Emerald300
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = currentGrams.toFloat(),
                            onValueChange = { currentGrams = it.toDouble() },
                            valueRange = 10f..500f,
                            steps = 97,
                            colors = SliderDefaults.colors(
                                thumbColor = Emerald400,
                                activeTrackColor = Emerald400,
                                inactiveTrackColor = Color.White.copy(alpha = 0.12f)
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("10 g", fontSize = 10.5.sp, color = Color.White.copy(alpha = 0.4f))
                            Text("500 g", fontSize = 10.5.sp, color = Color.White.copy(alpha = 0.4f))
                        }
                    }
                }
            }

            // 3. 4-Macro Grid (Calories, Protein, Carbs, Fat)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MacroGridCard(
                        label = "CALORIES",
                        value = "${scaled.energyKcal.toInt()}",
                        unit = "kcal",
                        color = Emerald400,
                        modifier = Modifier.weight(1f)
                    )
                    MacroGridCard(
                        label = "PROTEIN",
                        value = "${String.format("%.1f", scaled.protein)}",
                        unit = "g",
                        color = Cyan400,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MacroGridCard(
                        label = "CARBS",
                        value = "${String.format("%.1f", scaled.carbs)}",
                        unit = "g",
                        color = Color(0xFFA78BFA),
                        modifier = Modifier.weight(1f)
                    )
                    MacroGridCard(
                        label = "FAT",
                        value = "${String.format("%.1f", scaled.fat)}",
                        unit = "g",
                        color = TrafficYellow,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. Micronutrients Card
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Micronutrients", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(10.dp))
                        MicroNutrientRow("Fiber", "${String.format("%.1f", scaled.fiber)} g")
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 8.dp))
                        MicroNutrientRow("Iron", "${String.format("%.1f", scaled.iron)} mg")
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 8.dp))
                        MicroNutrientRow("Vitamin C", "${String.format("%.1f", scaled.vitC)} mg")
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 8.dp))
                        MicroNutrientRow("Potassium", "${scaled.potassium.toInt()} mg")
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 8.dp))
                        MicroNutrientRow("Calcium", "${scaled.calcium.toInt()} mg")
                    }
                }
            }

            // 5. Add to Plate Button
            item {
                PrimaryButton(
                    text = if (isAdded) "✓ Added to Plate" else "Add ${currentGrams.toInt()}g to Plate",
                    onClick = {
                        scope.launch {
                            val profiles = profileRepository.getAllProfiles().firstOrNull() ?: emptyList()
                            val active = profiles.firstOrNull()
                            if (active != null) {
                                plateRepository.addToPlate(active.id, food.fdcId, currentGrams)
                                isAdded = true
                                kotlinx.coroutines.delay(600)
                                onNavigateToPlate()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun MacroGridCard(
    label: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = unit, fontSize = 12.sp, color = Color.White.copy(alpha = 0.45f))
            }
        }
    }
}

@Composable
private fun MicroNutrientRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
