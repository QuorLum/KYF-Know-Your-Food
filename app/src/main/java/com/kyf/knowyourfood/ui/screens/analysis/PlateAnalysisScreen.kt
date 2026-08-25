package com.kyf.knowyourfood.ui.screens.analysis

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
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
import com.kyf.knowyourfood.ui.components.CircularRing
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.PrimaryButton
import com.kyf.knowyourfood.ui.components.ScreenHeader
import com.kyf.knowyourfood.ui.screens.plate.PlateViewModel
import com.kyf.knowyourfood.ui.theme.*

@Composable
fun PlateAnalysisScreen(
    viewModel: PlateViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRecipes: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val totals = state.totals

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Screen Header
        ScreenHeader(
            title = "Nutritional Summary",
            onBack = onNavigateBack,
            right = {
                IconButton(onClick = {
                    val text = viewModel.generateExportText()
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, text)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share Plate Summary")
                    context.startActivity(shareIntent)
                }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Emerald400)
                }
            }
        )

        if (totals == null || state.plateItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No items on plate to analyze.", color = Slate400)
            }
            return
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Macro Rings Card (Calories, Protein, Fiber)
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val calPct = ((totals.totalCaloriesKcal / 600.0) * 100).toFloat()
                        val protPct = ((totals.totalProteinG / 50.0) * 100).toFloat()
                        val fiberPct = ((totals.totalFiberG / 25.0) * 100).toFloat()

                        MacroRingItem(label = "Calories", value = "${totals.totalCaloriesKcal.toInt()} kcal", pct = calPct, color = Emerald400)
                        MacroRingItem(label = "Protein", value = "${String.format("%.1f", totals.totalProteinG)}g", pct = protPct, color = Cyan400)
                        MacroRingItem(label = "Fiber", value = "${String.format("%.1f", totals.totalFiberG)}g", pct = fiberPct, color = Color(0xFFA78BFA))
                    }
                }
            }

            // 2. UL Safety Check Banner
            item {
                if (totals.upperLimitAlerts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Emerald500.copy(alpha = 0.14f))
                            .border(1.dp, Emerald500.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✓", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("All nutrients within safe limits", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                                Text("UL check passed · DRI compliant for ${state.activeProfile?.name ?: "User"}", fontSize = 11.5.sp, color = Slate300)
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        totals.upperLimitAlerts.forEach { alert ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(TrafficRed.copy(alpha = 0.15f))
                                    .border(1.dp, TrafficRed.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚠️", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("High ${alert.nutrientName} Alert", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TrafficRed)
                                        Text(alert.message, fontSize = 11.5.sp, color = Slate200)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Detailed Nutrient Breakdown Progress Bars
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Nutrient Breakdown", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)

                        NutrientBarRow("Total Fat", totals.totalFatG, 70.0, "g", Emerald400)
                        NutrientBarRow("Carbohydrates", totals.totalCarbsG, 260.0, "g", Cyan400)
                        NutrientBarRow("Dietary Fiber", totals.totalFiberG, 25.0, "g", Color(0xFFA78BFA))
                        NutrientBarRow("Iron", totals.totalIronMg, 45.0, "mg", Color(0xFFF472B6))
                        NutrientBarRow("Vitamin C", totals.totalVitCMg, 2000.0, "mg", TrafficYellow)
                        NutrientBarRow("Potassium", totals.totalPotassiumMg, 4700.0, "mg", Emerald400)
                        NutrientBarRow("Calcium", totals.totalCalciumMg, 2500.0, "mg", Cyan400)
                    }
                }
            }

            // 4. View Recipe Recommendations Button
            item {
                PrimaryButton(
                    text = "🍳 View Recipe Recommendations",
                    onClick = onNavigateToRecipes
                )
            }
        }
    }
}

@Composable
private fun MacroRingItem(label: String, value: String, pct: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularRing(percentage = pct, size = 58.dp, ringColor = color) {
            Text("${pct.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 10.sp, color = Slate400)
    }
}

@Composable
private fun NutrientBarRow(label: String, current: Double, target: Double, unit: String, color: Color) {
    val progress = (current / target).toFloat().coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.5.sp, color = Slate300)
            Text(text = "${String.format("%.1f", current)} / ${target.toInt()} $unit", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color.White.copy(alpha = 0.08f)
        )
    }
}
