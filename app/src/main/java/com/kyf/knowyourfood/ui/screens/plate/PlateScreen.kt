package com.kyf.knowyourfood.ui.screens.plate

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.model.PlateItemWithFood
import com.kyf.knowyourfood.data.model.RecommendedRecipe
import com.kyf.knowyourfood.domain.ai.RecognizedFoodItem
import com.kyf.knowyourfood.ui.components.*
import com.kyf.knowyourfood.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlateScreen(
    viewModel: PlateViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProduce: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Camera Photo Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.analyzeMealPhoto(bitmap)
        }
    }

    // Gallery Photo Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                null
            }
            if (bitmap != null) {
                viewModel.analyzeMealPhoto(bitmap)
            }
        }
    }

    // Show error snackbar if AI analysis fails
    LaunchedEffect(state.aiErrorMessage) {
        state.aiErrorMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Long
            )
            viewModel.dismissAiError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                                .border(1.dp, Emerald500, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${String.format("%.0f", state.totals?.totalGrams ?: 0.0)}g Total",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald400
                            )
                        }
                    }
                }
            }

            // AI Meal Plate Vision Scanner Action Hub
            item {
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
                                Text(text = "📸", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "AI Meal Photo Scanner",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Auto-detect foods & portions with Gemini Vision",
                                        fontSize = 11.sp,
                                        color = Slate400
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Emerald500.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "AI VISION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { cameraLauncher.launch(null) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950),
                                enabled = !state.isAiAnalyzing
                            ) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Take Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                enabled = !state.isAiAnalyzing
                            ) {
                                Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pick Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (state.isAiAnalyzing) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate800)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = Emerald400,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Analyzing plate ingredients with Gemini AI...",
                                    fontSize = 12.sp,
                                    color = Slate200
                                )
                            }
                        }
                    }
                }
            }

            // Real-Time Aggregate Macronutrients Grid
            if (state.totals != null && state.plateItems.isNotEmpty()) {
                val t = state.totals!!
                item {
                    Text(text = "Total Plate Nutrition", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MacroPill(label = "Calories", value = "${t.totalCaloriesKcal.toInt()} kcal", accentColor = Color.White, modifier = Modifier.weight(1f))
                            MacroPill(label = "Protein", value = "${String.format("%.1f", t.totalProteinG)}g", accentColor = Cyan400, modifier = Modifier.weight(1f))
                            MacroPill(label = "Carbs", value = "${String.format("%.1f", t.totalCarbsG)}g", accentColor = Emerald400, modifier = Modifier.weight(1f))
                            MacroPill(label = "Fiber", value = "${String.format("%.1f", t.totalFiberG)}g", accentColor = TrafficYellow, modifier = Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MacroPill(label = "Iron", value = "${String.format("%.1f", t.totalIronMg)}mg", accentColor = Color(0xFFF472B6), modifier = Modifier.weight(1f))
                            MacroPill(label = "Vit C", value = "${String.format("%.1f", t.totalVitCMg)}mg", accentColor = Color(0xFF60A5FA), modifier = Modifier.weight(1f))
                            MacroPill(label = "Potassium", value = "${t.totalPotassiumMg.toInt()}mg", accentColor = Color(0xFFA78BFA), modifier = Modifier.weight(1f))
                            MacroPill(label = "Calcium", value = "${t.totalCalciumMg.toInt()}mg", accentColor = Color(0xFF34D399), modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Tolerable Upper Limit (UL) Safety Warnings
                if (t.upperLimitAlerts.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            t.upperLimitAlerts.forEach { alert ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(TrafficRed.copy(alpha = 0.15f))
                                        .border(1.dp, TrafficRed, RoundedCornerShape(12.dp))
                                        .padding(14.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "⚠️", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = "High ${alert.nutrientName} Warning", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TrafficRed)
                                            Text(text = alert.message, fontSize = 11.sp, color = Slate200)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Plate Ingredients List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Ingredients on Plate", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    TextButton(onClick = onNavigateToProduce) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Emerald400, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add More Produce", color = Emerald400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Empty State
            if (state.plateItems.isEmpty()) {
                item {
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Slate900
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🍽️", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Your Plate is Empty", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Scan a meal photo using AI Vision above, or tap below to browse whole foods and produce.",
                                fontSize = 12.sp,
                                color = Slate400,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onNavigateToProduce,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950)
                            ) {
                                Text("Browse Whole Foods Catalog", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                // List of items sitting on plate
                items(state.plateItems, key = { it.plateId }) { item ->
                    PlateItemCard(
                        item = item,
                        onEdit = { viewModel.openEditQuantity(item) },
                        onRemove = { viewModel.removeItem(item) }
                    )
                }
            }

            // Dynamic Recommended Recipes based on Plate Ingredients
            if (state.recommendedRecipes.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🍳 Recipes You Can Make With This Plate",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                items(state.recommendedRecipes, key = { it.title }) { recipe ->
                    RecipeCard(recipe = recipe)
                }
            }
        }
    }

    // Edit Quantity Dialog
    if (state.isEditingItem != null) {
        val item = state.isEditingItem!!
        AlertDialog(
            onDismissRequest = { viewModel.closeEditDialog() },
            title = { Text("Edit Portion: ${item.foodItem.name}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Adjust quantity in grams:", fontSize = 12.sp, color = Slate300)
                    Spacer(modifier = Modifier.height(12.dp))
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
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeEditDialog() }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // AI Vision Detected Items Modal Bottom Sheet
    if (state.showAiResultModal && state.aiResult != null) {
        val aiResult = state.aiResult!!
        var detectedItems by remember(aiResult) { mutableStateOf(aiResult.items) }

        ModalBottomSheet(
            onDismissRequest = { viewModel.closeAiResultModal() },
            containerColor = Slate900,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI Plate Recognition",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${detectedItems.size} items identified from photo",
                            fontSize = 12.sp,
                            color = Emerald400
                        )
                    }

                    IconButton(onClick = { viewModel.closeAiResultModal() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }

                // Photo preview if available
                state.scannedMealBitmap?.let { bmp ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, GlassBorderDark, RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Meal Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }

                // Prominent Accuracy Disclaimer Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TrafficYellow.copy(alpha = 0.15f))
                        .border(1.dp, TrafficYellow.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = aiResult.disclaimer,
                        fontSize = 11.sp,
                        color = TrafficYellow,
                        lineHeight = 15.sp
                    )
                }

                // Detected items list
                Text(text = "Detected Items & Portions:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(detectedItems) { item ->
                        GlassmorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = Slate800
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(
                                        text = "${String.format("%.0f", item.grams)}g • ${item.energy_kcal.toInt()} kcal • ${String.format("%.1f", item.protein)}g Protein",
                                        fontSize = 11.sp,
                                        color = Slate300
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        detectedItems = detectedItems.filter { it.name != item.name }
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Remove", tint = TrafficRed, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                // Confirm CTA Button
                Button(
                    onClick = {
                        viewModel.confirmAddAiItemsToPlate(detectedItems)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950),
                    enabled = detectedItems.isNotEmpty()
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add All Items to Plate", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
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
    val imageUrl = FoodImageHelper.getProduceImageUrl(item.foodItem.name, item.foodItem.category)
    val emoji = FoodImageHelper.getProduceEmoji(item.foodItem.name, item.foodItem.category)

    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Slate900
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FoodImageThumbnail(
                imageUrl = imageUrl,
                fallbackEmoji = emoji,
                size = 46.dp,
                contentDescription = item.foodItem.name
            )

            Spacer(modifier = Modifier.width(12.dp))

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
