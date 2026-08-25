package com.kyf.knowyourfood.ui.screens.history

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
import com.kyf.knowyourfood.data.model.ProductItem
import com.kyf.knowyourfood.data.repository.ProductRepository
import com.kyf.knowyourfood.data.repository.ProfileRepository
import com.kyf.knowyourfood.ui.components.EmptyState
import com.kyf.knowyourfood.ui.components.FoodImageHelper
import com.kyf.knowyourfood.ui.components.FoodImageThumbnail
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.ScreenHeader
import com.kyf.knowyourfood.ui.theme.*

private val tabs = listOf("All", "Scans", "Plates")

@Composable
fun HistoryScreen(
    productRepository: ProductRepository,
    profileRepository: ProfileRepository,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToPlate: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("All") }
    val products by productRepository.getAllProducts().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Screen Header
        ScreenHeader(title = "History")

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Emerald400 else Color(0xFF0F172A).copy(alpha = 0.7f))
                        .border(1.dp, if (isSelected) Emerald400 else Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = tab,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color(0xFF06210F) else Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (products.isEmpty() && selectedTab != "Plates") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Default.History,
                    title = "Nothing here yet",
                    body = "Your scanned products and analyzed plates will appear here for quick recall."
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
                items(products, key = { it.barcode }) { prod ->
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigateToProduct(prod.barcode) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
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
                                    text = "${prod.brand} · Today",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.45f)
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
}
