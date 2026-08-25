package com.kyf.knowyourfood.ui.screens.food_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.model.RawFoodItem
import com.kyf.knowyourfood.data.repository.PlateRepository
import com.kyf.knowyourfood.data.repository.ProfileRepository
import com.kyf.knowyourfood.data.repository.RawFoodRepository
import com.kyf.knowyourfood.ui.components.FoodImageBanner
import com.kyf.knowyourfood.ui.components.FoodImageHelper
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.ServingSlider
import com.kyf.knowyourfood.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
        Box(modifier = Modifier.fillMaxSize().background(Slate950), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Emerald400)
        }
        return
    }

    val food = foodItem!!
    val scaled = food.scaleTo(currentGrams)
    val imageUrl = FoodImageHelper.getProduceImageUrl(food.name, food.category)
    val emoji = FoodImageHelper.getProduceEmoji(food.name, food.category)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(food.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Food Banner Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    FoodImageBanner(
                        imageUrl = imageUrl,
                        fallbackEmoji = emoji,
                        height = 180.dp,
                        contentDescription = food.name
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "$emoji ${food.name}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${food.category} · ${food.source}",
                            fontSize = 12.sp,
                            color = Slate300
                        )
                    }
                }
            }

            // Portion Slider
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Slate900) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Portion Size", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${currentGrams.toInt()} g", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        ServingSlider(
                            currentGrams = currentGrams,
                            onGramsChanged = { currentGrams = it }
                        )
                    }
                }
            }

            // 4 Macro Grid Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MacroDetailBox("Calories", "${scaled.energyKcal.toInt()} kcal", Emerald400, Modifier.weight(1f))
                    MacroDetailBox("Protein", "${String.format("%.1f", scaled.protein)} g", Cyan400, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MacroDetailBox("Carbs", "${String.format("%.1f", scaled.carbs)} g", Color(0xFFA78BFA), Modifier.weight(1f))
                    MacroDetailBox("Fat", "${String.format("%.1f", scaled.fat)} g", TrafficYellow, Modifier.weight(1f))
                }
            }

            // Micronutrients Card
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Slate900) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Micronutrients Breakdown", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        MicroRow("Dietary Fiber", "${String.format("%.1f", scaled.fiber)} g")
                        Divider(color = Slate800, modifier = Modifier.padding(vertical = 8.dp))
                        MicroRow("Iron", "${String.format("%.1f", scaled.iron)} mg")
                        Divider(color = Slate800, modifier = Modifier.padding(vertical = 8.dp))
                        MicroRow("Vitamin C", "${String.format("%.1f", scaled.vitC)} mg")
                        Divider(color = Slate800, modifier = Modifier.padding(vertical = 8.dp))
                        MicroRow("Potassium", "${scaled.potassium.toInt()} mg")
                        Divider(color = Slate800, modifier = Modifier.padding(vertical = 8.dp))
                        MicroRow("Calcium", "${scaled.calcium.toInt()} mg")
                    }
                }
            }

            // Add to Plate CTA Button
            item {
                Button(
                    onClick = {
                        scope.launch {
                            val active = profileRepository.getAllProfiles()
                            // Get active profile id
                            plateRepository.addToPlate(1L, food.fdcId, currentGrams)
                            isAdded = true
                            onNavigateToPlate()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950)
                ) {
                    if (isAdded) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Added to Plate ✓", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Add ${currentGrams.toInt()}g to Meal Plate", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MacroDetailBox(title: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    GlassmorphicCard(modifier = modifier, backgroundColor = Slate900) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = accent)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun MicroRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = Slate300)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
