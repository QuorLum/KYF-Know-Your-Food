package com.kyf.knowyourfood.ui.screens.plate

import android.content.Intent
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
import com.kyf.knowyourfood.data.model.RecommendedRecipe
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.MacroPill
import com.kyf.knowyourfood.ui.components.ServingSlider
import com.kyf.knowyourfood.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlateScreen(
    viewModel: PlateViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Plate & Meal Builder",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (state.plateItems.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                val text = viewModel.generateExportText()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, text)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Plate Summary")
                                context.startActivity(shareIntent)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Emerald400)
                        }

                        IconButton(onClick = { viewModel.clearPlate() }) {
                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear", tint = TrafficRed)
                        }
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
            // Profile & Aggregate Weight Card
            item {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Slate900
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Plate for: ${state.activeProfile?.name ?: "User"}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${state.plateItems.size} Ingredients sitting on plate",
                                fontSize = 12.sp,
                                color = Slate400
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Emerald500.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${state.totals?.totalGrams?.toInt() ?: 0}g Total",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald400
                            )
                        }
                    }
                }
            }

            // Aggregated Nutrition Breakdown
            if (state.totals != null && state.plateItems.isNotEmpty()) {
                item {
                    val t = state.totals!!
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Slate900
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Aggregated Plate Nutrition",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            // Macronutrients
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MacroPill(label = "Calories", value = "${t.totalCaloriesKcal.toInt()} kcal", accentColor = Emerald400, modifier = Modifier.weight(1f))
                                MacroPill(label = "Protein", value = "${String.format("%.1f", t.totalProteinG)}g", accentColor = Cyan400, modifier = Modifier.weight(1f))
                                MacroPill(label = "Carbs", value = "${String.format("%.1f", t.totalCarbsG)}g", accentColor = Color.White, modifier = Modifier.weight(1f))
                                MacroPill(label = "Fiber", value = "${String.format("%.1f", t.totalFiberG)}g", accentColor = TrafficYellow, modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Micronutrients
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MacroPill(label = "Iron", value = "${String.format("%.1f", t.totalIronMg)}mg", accentColor = Color(0xFFF472B6), modifier = Modifier.weight(1f))
                                MacroPill(label = "Vit C", value = "${String.format("%.1f", t.totalVitCMg)}mg", accentColor = Color(0xFF60A5FA), modifier = Modifier.weight(1f))
                                MacroPill(label = "Potassium", value = "${t.totalPotassiumMg.toInt()}mg", accentColor = Color(0xFFA78BFA), modifier = Modifier.weight(1f))
                                MacroPill(label = "Calcium", value = "${t.totalCalciumMg.toInt()}mg", accentColor = Color(0xFF34D399), modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Upper Limit (UL) Safety Alerts
                if (state.totals!!.upperLimitAlerts.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            state.totals!!.upperLimitAlerts.forEach { alert ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x33EF4444))
                                        .border(1.dp, TrafficRed, RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = TrafficRed, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = alert.message,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Plate Items List
            item {
                Text(
                    text = "Items on Your Plate",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if (state.plateItems.isEmpty()) {
                item {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Slate900
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = Emerald400,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Your Plate is Empty",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Go to Produce & tap 'Add to Plate' on any fruit, veggie or legume to start calculating nutrition.",
                                fontSize = 12.sp,
                                color = Slate400,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(state.plateItems, key = { it.plateId }) { item ->
                    PlateItemCard(
                        item = item,
                        onEdit = { viewModel.openEditQuantity(item) },
                        onRemove = { viewModel.removeItem(item) }
                    )
                }
            }

            // Recipe Recommendations Section
            if (state.recommendedRecipes.isNotEmpty() && state.plateItems.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✨ Recipes You Can Make With This Plate",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                items(state.recommendedRecipes, key = { it.id }) { recipe ->
                    RecipeCard(recipe = recipe)
                }
            }
        }

        // Edit Grams Dialog
        if (state.isEditingItem != null) {
            val item = state.isEditingItem!!
            AlertDialog(
                onDismissRequest = { viewModel.closeEditDialog() },
                containerColor = Slate900,
                title = { Text("Adjust ${item.foodItem.name} Portion", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        ServingSlider(
                            currentGrams = state.editGrams,
                            onGramsChanged = { viewModel.updateEditGrams(it) }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.confirmEditQuantity() },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950)
                    ) {
                        Text("Update")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closeEditDialog() }) {
                        Text("Cancel", color = Slate400)
                    }
                }
            )
        }
    }
}

@Composable
fun PlateItemCard(
    item: PlateItemWithFood,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    val s = item.scaledNutrition
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Slate900
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.foodItem.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${String.format("%.0f", item.quantityG)}g • ${s.energyKcal.toInt()} kcal • ${String.format("%.1f", s.protein)}g Protein • ${String.format("%.1f", s.fiber)}g Fiber",
                    fontSize = 12.sp,
                    color = Slate300
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Emerald400, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onRemove) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = TrafficRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: RecommendedRecipe) {
    var expanded by remember { mutableStateOf(false) }

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald400
                    )
                    Text(
                        text = recipe.title,
                        fontSize = 15.sp,
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
                    Text(text = "⏱️ ${recipe.prepTimeMinutes} min", fontSize = 11.sp, color = Slate200)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Matched Ingredients Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                recipe.matchedIngredients.forEach { ing ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Emerald500.copy(alpha = 0.2f))
                            .border(1.dp, Emerald500, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "✓ $ing", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${recipe.caloriesPerServing} kcal • ${recipe.proteinG}g Protein • ${recipe.fiberG}g Fiber",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate300
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Instructions:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                recipe.instructions.forEachIndexed { index, step ->
                    Text(
                        text = "${index + 1}. $step",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Slate200
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (expanded) "Show Less ▲" else "View Step-by-Step Instructions ▼",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Emerald400,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}
