package com.kyf.knowyourfood.ui.screens.product_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.ProductItem
import com.kyf.knowyourfood.data.model.SafetyAssessment
import com.kyf.knowyourfood.data.repository.ProductRepository
import com.kyf.knowyourfood.data.repository.ProfileRepository
import com.kyf.knowyourfood.domain.engine.AllergyEngine
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.NutriScoreBadge
import com.kyf.knowyourfood.ui.components.SafetyAlertBanner
import com.kyf.knowyourfood.ui.components.TrafficLightBar
import com.kyf.knowyourfood.ui.theme.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    barcode: String,
    productRepository: ProductRepository,
    profileRepository: ProfileRepository,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
    var product by remember { mutableStateOf<ProductItem?>(null) }
    var activeProfile by remember { mutableStateOf<ProfileEntity?>(null) }
    var safetyAssessment by remember { mutableStateOf<SafetyAssessment?>(null) }
    var healthierAlternatives by remember { mutableStateOf<List<ProductItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(barcode) {
        isLoading = true
        val p = productRepository.getProductByBarcode(barcode)
        val profiles = profileRepository.getAllProfiles().firstOrNull() ?: emptyList()
        val currentProfile = profiles.firstOrNull()

        if (p != null) {
            product = p
            activeProfile = currentProfile
            if (currentProfile != null) {
                // Convert domain ProductItem to entity for evaluation
                val entity = com.kyf.knowyourfood.data.local.entity.ProductEntity(
                    barcode = p.barcode,
                    name = p.name,
                    brand = p.brand,
                    category = p.category,
                    nutriScore = p.nutriScore.letter,
                    sugars100g = p.sugars100g,
                    fat100g = p.fat100g,
                    satFat100g = p.satFat100g,
                    salt100g = p.salt100g,
                    protein100g = p.protein100g,
                    energyKcal100g = p.energyKcal100g,
                    fiber100g = p.fiber100g,
                    ingredientsText = p.ingredientsText,
                    allergensJson = kotlinx.serialization.json.Json.encodeToString(com.kyf.knowyourfood.data.model.AllergenTags.serializer(), p.allergenTags)
                )
                safetyAssessment = AllergyEngine.evaluateProductSafety(entity, currentProfile)
                healthierAlternatives = productRepository.getHealthierAlternatives(entity)
            }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Analysis", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate950)
            )
        },
        containerColor = Slate950
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Emerald400)
            }
        } else if (product != null) {
            val item = product!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Product Header Card
                item {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Slate900
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.brand.uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald400
                                )
                                Text(
                                    text = "EAN: ${item.barcode}",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Category: ${item.category}",
                                fontSize = 13.sp,
                                color = Slate300
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            // Nutri-Score Full Indicator
                            NutriScoreBadge(grade = item.nutriScore, compact = false)
                        }
                    }
                }

                // Personalized Allergen & Safety Assessment
                if (safetyAssessment != null) {
                    item {
                        SafetyAlertBanner(assessment = safetyAssessment!!)
                    }
                }

                // Front-of-Pack Traffic Lights Card
                item {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Slate900
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "UK/EU Front-of-Pack Traffic Lights (per 100g)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TrafficLightBar(trafficLights = item.trafficLights)
                        }
                    }
                }

                // Full Ingredients Breakdown
                item {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Slate900
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ingredients",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.ingredientsText,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = Slate200
                            )

                            if (item.allergenTags.contains.isNotEmpty() || item.allergenTags.may_contain.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Allergen Declarations:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Emerald400
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    item.allergenTags.contains.forEach {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0x33EF4444))
                                                .border(1.dp, TrafficRed, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = "Contains $it", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                    item.allergenTags.may_contain.forEach {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0x33F59E0B))
                                                .border(1.dp, TrafficYellow, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = "May contain $it", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Detailed Nutrition Facts Table (per 100g)
                item {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Slate900
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Nutrition Facts (per 100g)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            NutritionRow("Energy", "${item.energyKcal100g.toInt()} kcal (${(item.energyKcal100g * 4.184).toInt()} kJ)")
                            NutritionRow("Total Fat", "${item.fat100g} g")
                            NutritionRow("  - of which Saturates", "${item.satFat100g} g", indent = true)
                            NutritionRow("Total Sugars", "${item.sugars100g} g")
                            NutritionRow("Dietary Fiber", "${item.fiber100g} g")
                            NutritionRow("Protein", "${item.protein100g} g")
                            NutritionRow("Salt", "${item.salt100g} g")
                        }
                    }
                }

                // Healthier Alternatives Recommendation
                if (healthierAlternatives.isNotEmpty()) {
                    item {
                        Text(
                            text = "Healthier Alternatives in this Category",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            healthierAlternatives.forEach { alt ->
                                GlassmorphicCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    backgroundColor = Slate900,
                                    onClick = { onNavigateToProduct(alt.barcode) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = alt.brand, fontSize = 11.sp, color = Slate400)
                                            Text(text = alt.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(text = "Sugar: ${alt.sugars100g}g • Salt: ${alt.salt100g}g", fontSize = 11.sp, color = Slate300)
                                        }
                                        NutriScoreBadge(grade = alt.nutriScore, compact = true)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Product not found.", color = Slate400)
            }
        }
    }
}

@Composable
fun NutritionRow(label: String, value: String, indent: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (indent) Slate400 else Color.White,
            fontWeight = if (indent) FontWeight.Normal else FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
