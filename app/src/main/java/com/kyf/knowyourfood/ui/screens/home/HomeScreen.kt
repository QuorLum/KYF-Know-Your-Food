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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import com.kyf.knowyourfood.ui.components.CircularRing
import com.kyf.knowyourfood.ui.components.FoodImageHelper
import com.kyf.knowyourfood.ui.components.FoodImageThumbnail
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
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
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Emerald400, strokeWidth = 3.dp)
        }
        return
    }

    val active = state.activeProfile

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Switcher Rail (Avatar Carousel)
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
                        val avatarUrl = p.avatarPath ?: getFallbackAvatar(p.id, p.name)

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) Emerald400 else Color.White.copy(alpha = 0.15f),
                                    shape = CircleShape
                                )
                                .clickable { viewModel.selectProfile(p.id) }
                        ) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = p.name,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Emerald400.copy(alpha = 0.6f), CircleShape)
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
                    fontSize = 13.5.sp,
                    color = Color.White.copy(alpha = 0.55f)
                )
            }
        }

        // 3. Active Profile Glass Card
        if (active != null) {
            item {
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToProfiles
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatarUrl = active.avatarPath ?: getFallbackAvatar(active.id, active.name)
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = active.name,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(1.dp, Emerald400.copy(alpha = 0.5f), CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACTIVE PROFILE",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald400
                            )
                            Text(
                                text = active.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            val allergenCount = if (active.allergiesJson.length > 5) "Guarded Allergens" else "No Allergens"
                            Text(
                                text = "${active.age} yrs · ${active.gender} · $allergenCount",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.55f)
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 4. Quick Actions 2x2 Grid
        item {
            Text(
                text = "Quick Actions",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionItem(
                    title = "Scan Product",
                    subtitle = "Barcode / QR",
                    icon = Icons.Default.QrCodeScanner,
                    tint = Emerald400,
                    onClick = onNavigateToScanner,
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    title = "Build Plate",
                    subtitle = "Analyze Meal",
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
                QuickActionItem(
                    title = "Search Foods",
                    subtitle = "Products / Recipes",
                    icon = Icons.Default.Search,
                    tint = Color(0xFFA78BFA),
                    onClick = onNavigateToSearch,
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    title = "Explore Produce",
                    subtitle = "Fruits, Veg & More",
                    icon = Icons.Default.Eco,
                    tint = Color(0xFF34D399),
                    onClick = onNavigateToProduce,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 5. Today's Summary Card (3-Column Layout with Vertical Dividers)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Summary",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald400,
                    modifier = Modifier.clickable(onClick = onNavigateToPlate)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val cal = state.plateTotals?.totalCaloriesKcal ?: 1450.0
                    val calPct = ((cal / 2000.0) * 100).toFloat().coerceIn(0f, 100f)

                    // Col 1: Calorie Ring
                    Row(
                        modifier = Modifier.weight(1.3f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularRing(percentage = calPct, size = 48.dp, ringColor = Emerald400) {
                            Text("${calPct.toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Emerald300)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("CALORIES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.45f))
                            Text("${cal.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("/ 2,000 kcal", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.4f))
                        }
                    }

                    // Col 2: Nutrients Balance
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .border(width = 0.dp, color = Color.Transparent)
                            .padding(start = 10.dp)
                    ) {
                        Text("NUTRIENTS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.45f))
                        Text("Good", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Emerald300)
                        Text("Balance", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.4f))
                    }

                    // Col 3: Alerts
                    val alertCount = state.plateTotals?.upperLimitAlerts?.size ?: 0
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp)
                    ) {
                        Text("ALERTS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.45f))
                        Text("$alertCount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(if (alertCount == 0) "All clear today" else "Attention needed", fontSize = 9.5.sp, color = if (alertCount == 0) Emerald400 else TrafficRed)
                    }
                }
            }
        }

        // 6. Recent Scans List
        if (state.recentProducts.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Scans",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
                                    size = 44.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = prod.name,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${prod.brand} · ${prod.nutriScore.letter} Grade",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Emerald500.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Emerald400,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Feature Highlight Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassmorphicCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Emerald400, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Allergen Guard", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Personalized family protection", fontSize = 10.5.sp, color = Color.White.copy(alpha = 0.5f))
                    }
                }
                GlassmorphicCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Cyan400, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("100% Offline", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Your data. Always private.", fontSize = 10.5.sp, color = Color.White.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = subtitle, fontSize = 11.sp, color = Color.White.copy(alpha = 0.45f))
        }
    }
}

private fun getFallbackAvatar(id: Long, name: String): String {
    return when (name.lowercase()) {
        "divyanshu" -> "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=160&h=160&fit=crop&crop=faces&auto=format"
        "ananya" -> "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=160&h=160&fit=crop&crop=faces&auto=format"
        "mother" -> "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=160&h=160&fit=crop&crop=faces&auto=format"
        "father" -> "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=160&h=160&fit=crop&crop=faces&auto=format"
        "grandfather" -> "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=160&h=160&fit=crop&crop=faces&auto=format"
        else -> "https://api.dicebear.com/9.x/adventurer/svg?seed=${name}&backgroundColor=b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf&radius=50"
    }
}
