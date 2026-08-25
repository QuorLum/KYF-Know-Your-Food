package com.kyf.knowyourfood.ui.screens.product_detail

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
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.ProductItem
import com.kyf.knowyourfood.data.model.SafetyAssessment
import com.kyf.knowyourfood.data.model.SafetyStatus
import com.kyf.knowyourfood.data.repository.ProductRepository
import com.kyf.knowyourfood.data.repository.ProfileRepository
import com.kyf.knowyourfood.domain.engine.AllergyEngine
import com.kyf.knowyourfood.ui.components.*
import com.kyf.knowyourfood.ui.theme.*
import kotlinx.coroutines.flow.firstOrNull

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
    var showFullReport by remember { mutableStateOf(false) }

    LaunchedEffect(barcode) {
        isLoading = true
        val p = productRepository.getProductByBarcode(barcode)
        val profiles = profileRepository.getAllProfiles().firstOrNull() ?: emptyList()
        val currentProfile = profiles.firstOrNull()

        if (p != null) {
            product = p
            activeProfile = currentProfile
            if (currentProfile != null) {
                val entity = productRepository.getProductEntityByBarcode(barcode)
                if (entity != null) {
                    safetyAssessment = AllergyEngine.evaluateProductSafety(entity, currentProfile)
                    healthierAlternatives = productRepository.getHealthierAlternatives(entity)
                }
            }
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Screen Header
        ScreenHeader(
            title = "Product Report",
            onBack = onNavigateBack
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Emerald400, strokeWidth = 3.dp)
            }
        } else if (product != null) {
            val item = product!!
            val profile = activeProfile
            val assessment = safetyAssessment

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Product Identity Card
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FoodImageThumbnail(
                                imageUrl = FoodImageHelper.getProductImageUrl(item.barcode, item.name, item.brand, item.category),
                                fallbackEmoji = FoodImageHelper.getProductEmoji(item.category, item.name),
                                size = 64.dp
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = item.brand,
                                    fontSize = 12.5.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = item.category,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = 0.65f)
                                    )
                                }
                            }

                            NutriScoreBadge(grade = item.nutriScore, compact = true)
                        }
                    }
                }

                // 2. Safety Verdict Banner
                if (assessment != null) {
                    item {
                        val isSafe = assessment.status == SafetyStatus.SAFE
                        val isCaution = assessment.status == SafetyStatus.CAUTION
                        val bannerBg = if (isSafe) Emerald500.copy(alpha = 0.14f) else if (isCaution) TrafficYellow.copy(alpha = 0.14f) else TrafficRed.copy(alpha = 0.15f)
                        val bannerColor = if (isSafe) Emerald400 else if (isCaution) TrafficYellow else TrafficRed
                        val title = if (isSafe) "Safe Choice" else if (isCaution) "Caution" else "Not Safe"
                        val sub = if (isSafe) "No high-risk allergens detected" else if (isCaution) "Some trace or cross-reactivity risks" else "Contains allergens on this profile"

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(bannerBg)
                                .border(1.dp, bannerColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(bannerColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isSafe) Icons.Default.CheckCircle else if (isCaution) Icons.Default.Warning else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = Color(0xFF06210F),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = bannerColor)
                                    Text(
                                        text = "$sub${if (profile != null) " · for ${profile.name}" else ""}",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.65f)
                                    )
                                }
                            }
                        }
                    }

                    // 3. Why this result?
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Why this result?", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("for ${profile?.name ?: "You"}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.45f))
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = if (assessment.status == SafetyStatus.SAFE) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (assessment.status == SafetyStatus.SAFE) Emerald400 else TrafficYellow,
                                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = assessment.overallScoreText, fontSize = 12.5.sp, color = Color.White.copy(alpha = 0.75f), lineHeight = 18.sp)
                                }

                                val matchedAllergens = assessment.directAllergenMatches.map { it.triggerName }
                                val traceWarnings = assessment.traceAllergenMatches.map { it.triggerName }

                                if (matchedAllergens.isNotEmpty() || traceWarnings.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    if (matchedAllergens.isNotEmpty()) {
                                        Text("MATCHED ALLERGENS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TrafficRed)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            matchedAllergens.forEach { a ->
                                                DetailAllergenPill(label = a, isDanger = true)
                                            }
                                        }
                                    }

                                    if (traceWarnings.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("TRACE WARNINGS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TrafficYellow)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            traceWarnings.forEach { a ->
                                                DetailAllergenPill(label = "May contain $a", isDanger = false)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Front-of-Pack Traffic Lights (per 100g)
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Front-of-Pack (per 100g)", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                NutriScoreBadge(grade = item.nutriScore, compact = true)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            TrafficLightBar(trafficLights = item.trafficLights)
                        }
                    }
                }

                // 5. Nutrition Snapshot (per 100g)
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Nutrition Snapshot (per 100g)", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                NutriBox(label = "Energy", value = "${item.energyKcal100g.toInt()}", unit = "kcal", modifier = Modifier.weight(1f))
                                NutriBox(label = "Protein", value = "${String.format("%.1f", item.protein100g)}", unit = "g", modifier = Modifier.weight(1f))
                                NutriBox(label = "Fiber", value = "${String.format("%.1f", item.fiber100g)}", unit = "g", modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // 6. Ingredients & Declarations (Expandable)
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Ingredients", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.ingredientsText.ifEmpty { "Ingredients not listed by manufacturer." },
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.65f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // 7. Smarter Swaps
                if (healthierAlternatives.isNotEmpty()) {
                    item {
                        Column {
                            Text("Smarter Swaps", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                healthierAlternatives.forEach { alt ->
                                    GlassmorphicCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = { onNavigateToProduct(alt.barcode) }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            FoodImageThumbnail(
                                                imageUrl = FoodImageHelper.getProductImageUrl(alt.barcode, alt.name, alt.brand, alt.category),
                                                fallbackEmoji = FoodImageHelper.getProductEmoji(alt.category, alt.name),
                                                size = 40.dp
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(alt.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Text(alt.brand, fontSize = 11.sp, color = Color.White.copy(alpha = 0.45f))
                                            }
                                            NutriScoreBadge(grade = alt.nutriScore, compact = true)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Product not found.", color = Slate400)
            }
        }
    }
}

@Composable
private fun NutriBox(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.7f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.45f))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text(text = unit, fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun DetailAllergenPill(label: String, isDanger: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDanger) TrafficRed.copy(alpha = 0.15f) else TrafficYellow.copy(alpha = 0.15f))
            .border(1.dp, if (isDanger) TrafficRed.copy(alpha = 0.35f) else TrafficYellow.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDanger) Color(0xFFFCA5A5) else Color(0xFFFDE68A)
        )
    }
}
