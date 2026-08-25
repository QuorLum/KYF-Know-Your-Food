package com.kyf.knowyourfood.ui.screens.analysis

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.kyf.knowyourfood.ui.screens.plate.PlateViewModel
import com.kyf.knowyourfood.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlateAnalysisScreen(
    viewModel: PlateViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRecipes: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val totals = state.totals

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nutritional Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate950)
            )
        },
        containerColor = Slate950
    ) { padding ->
        if (totals == null || state.plateItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No items on plate to analyze.", color = Slate400)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Macro Rings Card
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Slate900) {
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

            // UL Safety Check Banner
            item {
                if (totals.upperLimitAlerts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Emerald500.copy(alpha = 0.15f))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✓", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("All nutrients within safe limits", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                                Text("UL check passed · DRI compliant for ${state.activeProfile?.name ?: "User"}", fontSize = 11.sp, color = Slate300)
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        totals.upperLimitAlerts.forEach { alert ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TrafficRed.copy(alpha = 0.15f))
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚠️", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("High ${alert.nutrientName} Alert", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TrafficRed)
                                        Text(alert.message, fontSize = 11.sp, color = Slate200)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Detailed Nutrient Breakdown
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Slate900) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Nutrient Breakdown", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)

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

            // Recipe Recommendations Button
            item {
                Button(
                    onClick = onNavigateToRecipes,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950)
                ) {
                    Text("🍳 View Recipe Recommendations", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MacroRingItem(label: String, value: String, pct: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularRing(percentage = pct, size = 58.dp, ringColor = color) {
            Text("${pct.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 10.sp, color = Slate400)
    }
}

@Composable
fun NutrientBarRow(label: String, current: Double, target: Double, unit: String, color: Color) {
    val progress = (current / target).toFloat().coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = Slate300)
            Text(text = "${String.format("%.1f", current)} / ${target.toInt()} $unit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Slate800
        )
    }
}
