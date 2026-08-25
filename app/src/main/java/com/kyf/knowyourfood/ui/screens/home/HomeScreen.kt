package com.kyf.knowyourfood.ui.screens.home

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.data.model.ProductItem
import com.kyf.knowyourfood.ui.components.*
import com.kyf.knowyourfood.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToScanner: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProduce: () -> Unit,
    onNavigateToPlate: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToProfiles: () -> Unit,
    onNavigateToHistory: () -> Unit
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

    val active = state.activeProfile

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Switcher Horizontal Rail
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(state.profiles, key = { it.id }) { p ->
                        val isSelected = p.id == active?.id
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Emerald500.copy(alpha = 0.25f) else Slate800)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Emerald400 else Slate700,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.selectProfile(p.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p.name.take(1).uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Emerald400 else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Slate800)
                        .border(1.dp, Emerald400.copy(alpha = 0.5f), CircleShape)
                        .clickable(onClick = onNavigateToProfiles),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Profile",
                        tint = Emerald400,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. Greeting Header
        item {
            Column {
                Text(
                    text = "Hello, ${active?.name ?: "Friend"} 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "What are we analyzing today?",
                    fontSize = 13.sp,
                    color = Slate400
                )
            }
        }

        // 3. Active Profile Quick Card
        if (active != null) {
            item {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Slate900,
                    onClick = onNavigateToProfiles
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Emerald500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = active.name.take(1).uppercase(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald400
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "ACTIVE PROFILE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald400
                                )
                                Text(
                                    text = active.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${active.age} yrs · ${active.gender} · ${if (active.allergiesJson.length > 5) "Guarded Allergens" else "No Allergens"}",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 4. Quick Actions 2x2 Grid
        item {
            Text(text = "Quick Actions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = "Scan Product",
                    subtitle = "Barcode / QR",
                    icon = Icons.Default.QrCodeScanner,
                    tint = Emerald400,
                    onClick = onNavigateToScanner,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Build Plate",
                    subtitle = "AI Meal Vision",
                    icon = Icons.Default.Restaurant,
                    tint = Cyan400,
                    onClick = onNavigateToPlate,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = "Search Foods",
                    subtitle = "3M+ Products",
                    icon = Icons.Default.Search,
                    tint = Color(0xFFA78BFA),
                    onClick = onNavigateToSearch,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Explore Produce",
                    subtitle = "Whole Foods",
                    icon = Icons.Default.Eco,
                    tint = Color(0xFF34D399),
                    onClick = onNavigateToProduce,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 5. Today's Summary Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Today's Summary", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = "See Plate",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald400,
                    modifier = Modifier.clickable(onClick = onNavigateToPlate)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            GlassmorphicCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Slate900) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val cal = state.plateTotals?.totalCaloriesKcal ?: 0.0
                    val calPct = ((cal / 2000.0) * 100).toFloat()

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularRing(percentage = calPct, size = 50.dp, ringColor = Emerald400) {
                            Text("${calPct.toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("CALORIES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400)
                            Text("${cal.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("/ 2,000 kcal", fontSize = 10.sp, color = Slate400)
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NUTRIENTS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400)
                        Text("Good", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                        Text("Balanced", fontSize = 10.sp, color = Slate400)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("ALERTS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400)
                        Text("${state.plateTotals?.upperLimitAlerts?.size ?: 0}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("All clear", fontSize = 10.sp, color = Emerald400)
                    }
                }
            }
        }

        // 6. Recent Scans
        if (state.recentProducts.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Recent Scans", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        text = "See All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald400,
                        modifier = Modifier.clickable(onClick = onNavigateToHistory)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.recentProducts.take(3).forEach { prod ->
                        GlassmorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Slate900,
                            onClick = { onNavigateToProductDetail(prod.barcode) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FoodImageThumbnail(
                                    imageUrl = FoodImageHelper.getProductImageUrl(prod.barcode, prod.name, prod.brand, prod.category),
                                    fallbackEmoji = FoodImageHelper.getProductEmoji(prod.name, prod.category),
                                    size = 40.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = prod.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(text = "${prod.brand} · ${prod.nutriScore.letter} Grade", fontSize = 10.sp, color = Slate400)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Emerald500.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "SAFE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Feature Highlights Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassmorphicCard(modifier = Modifier.weight(1f), backgroundColor = Slate900) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Emerald400, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Allergen Guard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Family safety shields", fontSize = 10.sp, color = Slate400)
                    }
                }
                GlassmorphicCard(modifier = Modifier.weight(1f), backgroundColor = Slate900) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Cyan400, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("100% Offline", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Private on-device data", fontSize = 10.sp, color = Slate400)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier,
        backgroundColor = Slate900,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = subtitle, fontSize = 11.sp, color = Slate400)
        }
    }
}
