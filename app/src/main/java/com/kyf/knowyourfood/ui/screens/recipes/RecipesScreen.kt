package com.kyf.knowyourfood.ui.screens.recipes

import androidx.compose.animation.AnimatedVisibility
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
import com.kyf.knowyourfood.data.model.RecommendedRecipe
import com.kyf.knowyourfood.ui.components.EmptyState
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.ScreenHeader
import com.kyf.knowyourfood.ui.screens.plate.PlateViewModel
import com.kyf.knowyourfood.ui.theme.*

@Composable
fun RecipesScreen(
    viewModel: PlateViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val recipes = state.recommendedRecipes
    var expandedRecipeTitle by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Screen Header
        ScreenHeader(
            title = "Recipe Ideas",
            onBack = onNavigateBack
        )

        if (recipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Default.Eco,
                    title = "No recipes yet",
                    body = "Add a few whole foods to your plate and we'll suggest recipes you can make right now."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Based on ${state.plateItems.size} items on your plate",
                        fontSize = 12.5.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }

                items(recipes, key = { it.title }) { r ->
                    val isOpen = expandedRecipeTitle == r.title
                    val matchPct = if (r.matchedIngredients.isNotEmpty()) (r.matchedIngredients.size * 100 / (r.matchedIngredients.size + r.additionalIngredients.size).coerceAtLeast(1)) else 80

                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            expandedRecipeTitle = if (isOpen) null else r.title
                        }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🍲", fontSize = 24.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = r.title,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(13.dp))
                                        Text("${r.prepTimeMinutes} min", fontSize = 11.5.sp, color = Color.White.copy(alpha = 0.5f))
                                        Text("· ${r.matchedIngredients.size} ready", fontSize = 11.5.sp, color = Color.White.copy(alpha = 0.5f))
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$matchPct%",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (matchPct >= 100) Emerald400 else if (matchPct >= 66) Cyan400 else TrafficYellow
                                    )
                                    Text("match", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.4f))
                                }
                            }

                            // Expanded Step-by-Step Instructions
                            AnimatedVisibility(visible = isOpen) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Ingredients tags
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        r.matchedIngredients.forEach { ing ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Emerald500.copy(alpha = 0.15f))
                                                    .border(1.dp, Emerald500.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Emerald400, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(text = ing, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Emerald300)
                                                }
                                            }
                                        }
                                    }

                                    if (r.additionalIngredients.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Missing: ${r.additionalIngredients.joinToString(", ")}",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFFFCD34D)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("STEPS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.45f))
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        r.instructions.forEachIndexed { idx, step ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clip(CircleShape)
                                                        .background(Emerald400.copy(alpha = 0.2f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("${idx + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Emerald300)
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = step,
                                                    fontSize = 12.5.sp,
                                                    color = Color.White.copy(alpha = 0.75f),
                                                    lineHeight = 17.sp,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
