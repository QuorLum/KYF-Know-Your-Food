package com.kyf.knowyourfood.ui.screens.plate

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.model.PlateItemWithFood
import com.kyf.knowyourfood.ui.components.EmptyState
import com.kyf.knowyourfood.ui.components.FoodImageHelper
import com.kyf.knowyourfood.ui.components.FoodImageThumbnail
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.PrimaryButton
import com.kyf.knowyourfood.ui.components.ScreenHeader
import com.kyf.knowyourfood.ui.theme.*

@Composable
fun PlateScreen(
    viewModel: PlateViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProduce: () -> Unit,
    onNavigateToAnalysis: () -> Unit = {},
    onNavigateToRecipes: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Camera & Gallery Launchers
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.analyzeMealPhoto(bitmap)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    viewModel.analyzeMealPhoto(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val items = state.plateItems
    val totals = state.totals

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Screen Header
        ScreenHeader(
            title = "Build Your Plate",
            right = {
                if (items.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onNavigateToProduce)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Emerald400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                    }
                }
            }
        )

        if (items.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 60.dp)
                ) {
                    EmptyState(
                        icon = Icons.Default.Restaurant,
                        title = "Your plate is empty",
                        body = "Add whole foods and produce to build a meal, then analyze its full nutrition and UL safety."
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PrimaryButton(
                        text = "Explore Produce",
                        onClick = onNavigateToProduce,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // AI Vision Quick Button in Empty State
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        onClick = { cameraLauncher.launch(null) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Cyan400, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Or Snap Meal Photo with AI Vision", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Cyan400)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Visual Circular Plate Graphic
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(210.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F172A).copy(alpha = 0.8f))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    items.take(4).forEach { item ->
                                        Text(
                                            text = FoodImageHelper.getProduceEmoji(item.foodItem.name, item.foodItem.category),
                                            fontSize = 24.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Floating Kcal Badge at bottom
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Emerald400)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${totals?.totalCaloriesKcal?.toInt() ?: 0} kcal",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF04220F)
                            )
                        }
                    }
                }

                // 2. AI Meal Vision Banner (Camera & Gallery)
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Scan Plate with AI Vision", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "⚠️ Estimated by AI Vision: Nutritional values are automated approximations. Not medical dietary advice.",
                                fontSize = 10.5.sp,
                                color = Slate400
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { cameraLauncher.launch(null) },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950),
                                    enabled = !state.isAiAnalyzing
                                ) {
                                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Take Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { galleryLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    enabled = !state.isAiAnalyzing
                                ) {
                                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pick Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (state.isAiAnalyzing) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Slate800)
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(color = Emerald400, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Analyzing meal ingredients with Gemini AI...", fontSize = 11.sp, color = Slate200)
                                }
                            }
                        }
                    }
                }

                // 3. Plate Items List with [-] grams [+] Stepper
                items(items, key = { it.plateId }) { item ->
                    val food = item.foodItem
                    val scaledKcal = (food.energyKcal * (item.quantityG / 100.0)).toInt()

                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FoodImageThumbnail(
                                imageUrl = FoodImageHelper.getProduceImageUrl(food.name, food.category),
                                fallbackEmoji = FoodImageHelper.getProduceEmoji(food.name, food.category),
                                size = 44.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${FoodImageHelper.getProduceEmoji(food.name, food.category)} ${food.name}",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "$scaledKcal kcal · ${item.quantityG.toInt()}g",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }

                            // Stepper [-] qty [+]
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .clickable {
                                            val newGrams = (item.quantityG - 25.0).coerceAtLeast(10.0)
                                            viewModel.updateItemGramsDirectly(item.plateId, newGrams)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White, modifier = Modifier.size(14.dp))
                                }

                                Text(
                                    text = "${item.quantityG.toInt()}g",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.width(42.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Emerald400)
                                        .clickable {
                                            val newGrams = (item.quantityG + 25.0).coerceAtMost(500.0)
                                            viewModel.updateItemGramsDirectly(item.plateId, newGrams)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = Color(0xFF04220F), modifier = Modifier.size(14.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { viewModel.removeItem(item) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Remove", tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // 4. Plate Totals Card (4 Columns: Calories, Protein, Carbs, Fiber)
                if (totals != null) {
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Plate Totals", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TotalBox(label = "Calories", value = "${totals.totalCaloriesKcal.toInt()}", unit = "", modifier = Modifier.weight(1f))
                                    TotalBox(label = "Protein", value = "${totals.totalProteinG.toInt()}", unit = "g", modifier = Modifier.weight(1f))
                                    TotalBox(label = "Carbs", value = "${totals.totalCarbsG.toInt()}", unit = "g", modifier = Modifier.weight(1f))
                                    TotalBox(label = "Fiber", value = "${totals.totalFiberG.toInt()}", unit = "g", modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // 5. Action Row: Clear + Analyze Plate
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.clearPlate() },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f), contentColor = Color.White),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("Clear", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }

                        PrimaryButton(
                            text = "Analyze Plate",
                            onClick = onNavigateToAnalysis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 6. See Recipe Recommendations CTA
                item {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNavigateToRecipes
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Eco, contentDescription = null, tint = Emerald400, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("See Recipe Recommendations", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Emerald300)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalBox(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                if (unit.isNotEmpty()) {
                    Text(text = unit, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.45f))
        }
    }
}
