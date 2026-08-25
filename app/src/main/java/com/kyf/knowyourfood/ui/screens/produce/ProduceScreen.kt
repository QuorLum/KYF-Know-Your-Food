package com.kyf.knowyourfood.ui.screens.produce

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.model.RawFoodItem
import com.kyf.knowyourfood.ui.components.*
import com.kyf.knowyourfood.ui.theme.*

private val categories = listOf("Fruits", "Vegetables", "Grains", "Nuts & Seeds", "Lentils & Legumes")

data class ProduceFilterPill(val id: String, val label: String, val color: Color)

private val nutrientFilters = listOf(
    ProduceFilterPill("iron", "Iron-Rich", Color(0xFFF87171)),
    ProduceFilterPill("vitc", "Vitamin C", Color(0xFFFBBF24)),
    ProduceFilterPill("fiber", "High Fiber", Color(0xFF34D399)),
    ProduceFilterPill("protein", "High Protein", Color(0xFF22D3EE)),
    ProduceFilterPill("lowk", "Low Potassium", Color(0xFFA78BFA)),
    ProduceFilterPill("lowcal", "Low Calorie", Color(0xFF34D399))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduceScreen(
    viewModel: ProduceViewModel,
    onNavigateToPlate: () -> Unit,
    onNavigateToFoodDetail: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf("Fruits") }
    var selectedNutrient by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Screen Header
        ScreenHeader(
            title = "Explore Produce",
            right = {
                IconButton(onClick = onNavigateToPlate) {
                    Icon(imageVector = Icons.Default.Restaurant, contentDescription = "Plate", tint = Emerald400)
                }
            }
        )

        // 1. Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.7f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.45f), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = state.query,
                        onValueChange = { viewModel.onQueryChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.5.sp),
                        decorationBox = { innerTextField ->
                            if (state.query.isEmpty()) {
                                Text("Search fruits, vegetables, grains…", color = Color.White.copy(alpha = 0.4f), fontSize = 13.5.sp)
                            }
                            innerTextField()
                        }
                    )
                }
            }
        }

        // 2. Banner: Eat Natural. Eat Whole.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Emerald500.copy(alpha = 0.22f),
                            Cyan400.copy(alpha = 0.14f)
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Eat Natural. Eat Whole.", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Explore 1000+ whole foods · USDA & INDB", fontSize = 12.sp, color = Color.White.copy(alpha = 0.65f))
                }
                Icon(imageVector = Icons.Default.Eco, contentDescription = null, tint = Emerald300, modifier = Modifier.size(34.dp))
            }
        }

        // 3. Category Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat && state.query.isEmpty()
                Column(
                    modifier = Modifier
                        .clickable {
                            selectedCategory = cat
                            viewModel.selectCategory(cat)
                        }
                        .padding(bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = cat,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.45f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (isSelected) {
                        Box(modifier = Modifier.width(20.dp).height(2.dp).background(Emerald400, RoundedCornerShape(1.dp)))
                    }
                }
            }
        }

        // 4. Nutrient Filter Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(nutrientFilters) { nf ->
                val isActive = selectedNutrient == nf.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isActive) nf.color else Color(0xFF0F172A).copy(alpha = 0.7f))
                        .border(1.dp, if (isActive) nf.color else Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .clickable {
                            selectedNutrient = if (isActive) null else nf.id
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = nf.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActive) Color(0xFF04220F) else Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }

        // 5. Produce Foods List
        val filteredFoods = state.rawFoods.filter { food ->
            val matchesNutrient = when (selectedNutrient) {
                "iron" -> food.iron >= 2.5
                "vitc" -> food.vitC >= 40.0
                "fiber" -> food.fiber >= 5.0
                "protein" -> food.protein >= 8.0
                "lowk" -> (food.micronutrients.potassium_mg ?: 0.0) <= 200.0
                "lowcal" -> food.energyKcal <= 40.0
                else -> true
            }
            matchesNutrient
        }

        if (filteredFoods.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Default.Eco,
                    title = "Nothing matches",
                    body = "No produce in this category meets the selected nutrient filter."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredFoods, key = { it.fdcId }) { f ->
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigateToFoodDetail(f.fdcId) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FoodImageThumbnail(
                                imageUrl = FoodImageHelper.getProduceImageUrl(f.name, f.category),
                                fallbackEmoji = FoodImageHelper.getProduceEmoji(f.name, f.category),
                                size = 48.dp
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = f.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Per 100g · ${f.source}",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.45f)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${f.energyKcal.toInt()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "kcal",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.45f)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Emerald400,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
