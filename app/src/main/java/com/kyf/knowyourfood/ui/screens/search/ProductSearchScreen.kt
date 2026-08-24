package com.kyf.knowyourfood.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.model.ProductItem
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.NutriScoreBadge
import com.kyf.knowyourfood.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSearchScreen(
    viewModel: ProductSearchViewModel,
    onProductClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = state.query,
            onValueChange = { viewModel.onQueryChanged(it) },
            placeholder = { Text("Search products, brands, barcodes...", color = Slate400) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Emerald400)
            },
            trailingIcon = {
                if (state.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChanged("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Slate400)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Slate900,
                unfocusedContainerColor = Slate900,
                focusedBorderColor = Emerald500,
                unfocusedBorderColor = Slate700,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Nutri-Score Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Score:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Slate400,
                modifier = Modifier.padding(end = 8.dp)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(listOf("A", "B", "C", "D", "E")) { score ->
                    val isSelected = state.selectedNutriScore == score
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectNutriScoreFilter(score) },
                        label = { Text(score, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500,
                            selectedLabelColor = Slate950,
                            containerColor = Slate900,
                            labelColor = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Health & Allergen-Free Quick Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                FilterChip(
                    selected = state.filterHighProtein,
                    onClick = { viewModel.toggleHighProtein() },
                    label = { Text("High Protein (≥10g)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Cyan500,
                        selectedLabelColor = Slate950,
                        containerColor = Slate900,
                        labelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = state.filterLowSugar,
                    onClick = { viewModel.toggleLowSugar() },
                    label = { Text("Low Sugar (≤5g)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Emerald500,
                        selectedLabelColor = Slate950,
                        containerColor = Slate900,
                        labelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = state.filterLowSalt,
                    onClick = { viewModel.toggleLowSalt() },
                    label = { Text("Low Salt (≤0.3g)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Emerald500,
                        selectedLabelColor = Slate950,
                        containerColor = Slate900,
                        labelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = state.filterAllergenFree == "GLUTEN",
                    onClick = { viewModel.setAllergenFreeFilter("GLUTEN") },
                    label = { Text("Gluten-Free") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Emerald500,
                        selectedLabelColor = Slate950,
                        containerColor = Slate900,
                        labelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = state.filterAllergenFree == "MILK",
                    onClick = { viewModel.setAllergenFreeFilter("MILK") },
                    label = { Text("Dairy-Free") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Emerald500,
                        selectedLabelColor = Slate950,
                        containerColor = Slate900,
                        labelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = state.filterAllergenFree == "PEANUT",
                    onClick = { viewModel.setAllergenFreeFilter("PEANUT") },
                    label = { Text("Peanut-Free") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Emerald500,
                        selectedLabelColor = Slate950,
                        containerColor = Slate900,
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Product Count & Reset Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${state.products.size} Products Found",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate400
            )
            if (state.query.isNotEmpty() || state.selectedNutriScore.isNotEmpty() || state.selectedCategory.isNotEmpty() || state.filterHighProtein || state.filterLowSugar || state.filterLowSalt || state.filterAllergenFree.isNotEmpty()) {
                Text(
                    text = "Reset Filters",
                    fontSize = 12.sp,
                    color = Emerald400,
                    modifier = Modifier.clickable { viewModel.resetFilters() }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Products List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.products, key = { it.barcode }) { product ->
                ProductListItemCard(
                    product = product,
                    onClick = { onProductClick(product.barcode) }
                )
            }
        }
    }
}

@Composable
fun ProductListItemCard(
    product: ProductItem,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Slate900,
        onClick = onClick
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
                    text = product.brand,
                    fontSize = 11.sp,
                    color = Slate400
                )
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sugar: ${product.sugars100g}g",
                        fontSize = 11.sp,
                        color = if (product.sugars100g > 22.5) TrafficRed else Slate300
                    )
                    Text(text = "•", fontSize = 11.sp, color = Slate600)
                    Text(
                        text = "Protein: ${product.protein100g}g",
                        fontSize = 11.sp,
                        color = Slate300
                    )
                    Text(text = "•", fontSize = 11.sp, color = Slate600)
                    Text(
                        text = "Salt: ${product.salt100g}g",
                        fontSize = 11.sp,
                        color = if (product.salt100g > 1.5) TrafficRed else Slate300
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            NutriScoreBadge(grade = product.nutriScore, compact = true)
        }
    }
}
