package com.kyf.knowyourfood.ui.screens.home

import androidx.compose.animation.*
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.ProductItem
import com.kyf.knowyourfood.ui.components.*
import com.kyf.knowyourfood.ui.theme.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToScanner: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProduce: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToProfiles: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate950),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Emerald400, strokeWidth = 3.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading your dashboard...", fontSize = 14.sp, color = Slate400)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Header with Active Profile Switcher
        item {
            HeaderSection(
                activeProfile = state.activeProfile,
                profiles = state.profiles,
                onProfileSelected = { viewModel.selectProfile(it.id) },
                onManageProfiles = onNavigateToProfiles
            )
        }

        // 2. Global Nutrition Explorer & Quick Search Hub (No duplicate scan button)
        item {
            QuickDiscoveryBanner(
                onSearchClick = onNavigateToSearch,
                onExploreProduce = onNavigateToProduce
            )
        }

        // 3. Active Profile Allergy Alert Overview
        if (state.activeProfile != null) {
            item {
                ActiveProfileSummaryCard(
                    profile = state.activeProfile!!,
                    onEditClick = onNavigateToProfiles
                )
            }
        }

        // 4. Daily Plate Snapshot
        item {
            DailyPlateSnapshotCard(
                plateItemsCount = state.plateItems.size,
                totals = state.plateTotals,
                onOpenPlate = onNavigateToProduce
            )
        }

        // 5. Featured Healthy Alternatives Swaps (guarded against empty states)
        if (state.featuredHealthySwaps.isNotEmpty()) {
            item {
                Text(
                    text = "Smart Healthier Swaps",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.featuredHealthySwaps.take(3).forEach { (bad, good) ->
                        HealthySwapCard(
                            unhealthyProduct = bad,
                            healthyAlternative = good,
                            onClickUnhealthy = { onNavigateToProductDetail(bad.barcode) },
                            onClickHealthy = { onNavigateToProductDetail(good.barcode) }
                        )
                    }
                }
            }
        }

        // 6. Popular Discoverable Products Carousel
        if (state.recentProducts.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Products in Database",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Explore All",
                        fontSize = 13.sp,
                        color = Emerald400,
                        modifier = Modifier.clickable(onClick = onNavigateToSearch)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.recentProducts) { product ->
                        ProductMiniCard(
                            product = product,
                            onClick = { onNavigateToProductDetail(product.barcode) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(
    activeProfile: ProfileEntity?,
    profiles: List<ProfileEntity>,
    onProfileSelected: (ProfileEntity) -> Unit,
    onManageProfiles: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Know Your Food",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Smart Allergen & Nutrition Guard",
                    fontSize = 13.sp,
                    color = Slate400
                )
            }

            IconButton(
                onClick = onManageProfiles,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Slate800)
            ) {
                Icon(
                    imageVector = Icons.Default.ManageAccounts,
                    contentDescription = "Manage Profiles",
                    tint = Emerald400
                )
            }
        }

        if (profiles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))

            // Profile Selector Pills
            Text(
                text = "Active Family Member:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Slate400
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(profiles) { profile ->
                    val isSelected = activeProfile?.id == profile.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Emerald500 else Slate850)
                            .border(1.dp, if (isSelected) Emerald400 else Slate700, RoundedCornerShape(20.dp))
                            .clickable { onProfileSelected(profile) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (profile.age < 12) Icons.Default.ChildCare else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isSelected) Slate950 else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = profile.name,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Slate950 else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickDiscoveryBanner(
    onSearchClick: () -> Unit,
    onExploreProduce: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Slate900
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Emerald500.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Global Nutrition Explorer",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "3M+ global products & USDA/INDB whole foods",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Emerald500.copy(alpha = 0.2f))
                        .border(1.dp, Emerald500, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "GLOBAL SYNC",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald400
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Search Searchbar Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Slate800)
                    .border(1.dp, GlassBorderDark, RoundedCornerShape(12.dp))
                    .clickable(onClick = onSearchClick)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Search any product, brand, or ingredient...",
                            fontSize = 12.sp,
                            color = Slate400
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Emerald400,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Category Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onExploreProduce,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("🥦 Whole Foods", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onSearchClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("📦 Packaged Goods", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun ActiveProfileSummaryCard(
    profile: ProfileEntity,
    onEditClick: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Emerald400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${profile.name}'s Safety Guard",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "Edit Rules",
                    fontSize = 12.sp,
                    color = Emerald400,
                    modifier = Modifier.clickable(onClick = onEditClick)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Age: ${profile.age} yrs • Weight: ${profile.weight} kg • Height: ${profile.height} cm",
                fontSize = 12.sp,
                color = Slate400
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Active Protection: Automated checks for major FDA 9, EU 14, FSSAI 8 triggers, cross-reactivity syndromes & pediatric sugar guardrails.",
                fontSize = 12.sp,
                color = Slate200
            )
        }
    }
}

@Composable
fun DailyPlateSnapshotCard(
    plateItemsCount: Int,
    totals: com.kyf.knowyourfood.data.model.PlateNutritionTotals?,
    onOpenPlate: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Slate900,
        onClick = onOpenPlate
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = Cyan400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Produce & Plate Planner",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "$plateItemsCount items",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Cyan400
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (totals != null && plateItemsCount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MacroPill(
                        label = "Calories",
                        value = "${totals.totalCaloriesKcal.toInt()} kcal",
                        accentColor = Emerald400,
                        modifier = Modifier.weight(1f)
                    )
                    MacroPill(
                        label = "Protein",
                        value = "${String.format("%.1f", totals.totalProteinG)}g",
                        accentColor = Cyan400,
                        modifier = Modifier.weight(1f)
                    )
                    MacroPill(
                        label = "Fiber",
                        value = "${String.format("%.1f", totals.totalFiberG)}g",
                        accentColor = TrafficYellow,
                        modifier = Modifier.weight(1f)
                    )
                    MacroPill(
                        label = "Iron",
                        value = "${String.format("%.1f", totals.totalIronMg)}mg",
                        accentColor = Color(0xFFF472B6),
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Text(
                    text = "Plate is currently empty. Tap to explore global fruits, vegetables & legumes and build your meal!",
                    fontSize = 13.sp,
                    color = Slate400
                )
            }
        }
    }
}

@Composable
fun HealthySwapCard(
    unhealthyProduct: ProductItem,
    healthyAlternative: ProductItem,
    onClickUnhealthy: () -> Unit,
    onClickHealthy: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Slate900
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category: ${unhealthyProduct.category}",
                    fontSize = 11.sp,
                    color = Slate400
                )
                Text(
                    text = "RECOMMENDED SWAP",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald400
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Unhealthy Item
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onClickUnhealthy),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FoodImageThumbnail(
                        imageUrl = FoodImageHelper.getProductImageUrl(
                            barcode = unhealthyProduct.barcode,
                            name = unhealthyProduct.name,
                            brand = unhealthyProduct.brand,
                            category = unhealthyProduct.category
                        ),
                        fallbackEmoji = FoodImageHelper.getProductEmoji(unhealthyProduct.category, unhealthyProduct.name),
                        size = 38.dp,
                        contentDescription = unhealthyProduct.name
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = unhealthyProduct.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        NutriScoreBadge(grade = unhealthyProduct.nutriScore, compact = true)
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Swap To",
                    tint = Emerald400,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                // Healthy Item
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onClickHealthy),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FoodImageThumbnail(
                        imageUrl = FoodImageHelper.getProductImageUrl(
                            barcode = healthyAlternative.barcode,
                            name = healthyAlternative.name,
                            brand = healthyAlternative.brand,
                            category = healthyAlternative.category
                        ),
                        fallbackEmoji = FoodImageHelper.getProductEmoji(healthyAlternative.category, healthyAlternative.name),
                        size = 38.dp,
                        contentDescription = healthyAlternative.name
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = healthyAlternative.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald400,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        NutriScoreBadge(grade = healthyAlternative.nutriScore, compact = true)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductMiniCard(
    product: ProductItem,
    onClick: () -> Unit
) {
    val imageUrl = FoodImageHelper.getProductImageUrl(
        barcode = product.barcode,
        name = product.name,
        brand = product.brand,
        category = product.category
    )
    val emoji = FoodImageHelper.getProductEmoji(product.category, product.name)

    GlassmorphicCard(
        modifier = Modifier
            .width(160.dp)
            .height(175.dp),
        backgroundColor = Slate900,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            FoodImageThumbnail(
                imageUrl = imageUrl,
                fallbackEmoji = emoji,
                size = 48.dp,
                contentDescription = product.name,
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Text(
                    text = product.brand.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald400
                )
                Text(
                    text = product.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            NutriScoreBadge(grade = product.nutriScore, compact = true)
        }
    }
}
