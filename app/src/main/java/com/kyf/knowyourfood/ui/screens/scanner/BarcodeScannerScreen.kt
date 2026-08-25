package com.kyf.knowyourfood.ui.screens.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.kyf.knowyourfood.ui.components.*
import com.kyf.knowyourfood.ui.theme.*
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScannerScreen(
    viewModel: ScannerViewModel,
    onNavigateToProductDetail: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showTestBarcodes by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Show error snackbar
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
    ) {
        if (hasCameraPermission && state.isScanning) {
            CameraPreviewView(
                onBarcodeDetected = { barcode ->
                    viewModel.onBarcodeDetected(barcode)
                }
            )

            // Gradient overlay at top and bottom for readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Slate950.copy(alpha = 0.8f), Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Slate950.copy(alpha = 0.9f))
                        )
                    )
            )
        } else if (state.isScanning) {
            // Fallback when camera permission is unavailable
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Slate950),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Emerald500.copy(alpha = 0.15f))
                            .border(2.dp, Emerald400, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Emerald400,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Barcode Scanner",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Point your camera at a food product barcode for instant allergen safety analysis.",
                        fontSize = 13.sp,
                        color = Slate400,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Camera permission required. Tap below to enter a barcode manually or use a test product.",
                        fontSize = 12.sp,
                        color = Slate500,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Viewfinder Target Overlay — only when scanning with camera
        if (hasCameraPermission && state.isScanning) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(260.dp)
                    .border(2.dp, Emerald400.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            )
        }

        // Top Bar Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Slate900.copy(alpha = 0.8f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Profile: ${state.activeProfile?.name ?: "Default"}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { viewModel.openManualInputDialog() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Slate900.copy(alpha = 0.8f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Manual Entry",
                        tint = Emerald400
                    )
                }
            }
        }

        // Bottom section — collapsible test barcode picker
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            // Toggle for test barcodes section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Slate900.copy(alpha = 0.9f))
                    .clickable { showTestBarcodes = !showTestBarcodes }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = Emerald400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Quick Lookup (${state.testProducts.size} products in database)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Emerald400
                    )
                }
                Icon(
                    imageVector = if (showTestBarcodes) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Slate400,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(
                visible = showTestBarcodes,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 6.dp)) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.testProducts.take(8)) { product ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Slate900.copy(alpha = 0.95f))
                                    .border(1.dp, Slate700, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.onBarcodeDetected(product.barcode) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Column {
                                    Text(
                                        text = product.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = product.barcode,
                                        fontSize = 9.sp,
                                        color = Slate400
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Scanned Result Bottom Sheet Modal
        if (state.scannedProduct != null) {
            val product = state.scannedProduct!!
            ModalBottomSheet(
                onDismissRequest = { viewModel.resumeScanning() },
                containerColor = Slate900,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FoodImageThumbnail(
                            imageUrl = FoodImageHelper.getProductImageUrl(
                                barcode = product.barcode,
                                name = product.name,
                                brand = product.brand,
                                category = product.category
                            ),
                            fallbackEmoji = FoodImageHelper.getProductEmoji(product.category, product.name),
                            size = 54.dp,
                            contentDescription = product.name
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.brand.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald400
                            )
                            Text(
                                text = product.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        NutriScoreBadge(grade = product.nutriScore, compact = true)
                    }

                    // Safety Assessment Banner
                    if (state.safetyAssessment != null) {
                        SafetyAlertBanner(assessment = state.safetyAssessment!!)
                    }

                    // Front of Pack Traffic Lights
                    TrafficLightBar(trafficLights = product.trafficLights)

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.resumeScanning() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("Scan Next")
                        }

                        Button(
                            onClick = {
                                viewModel.resumeScanning()
                                onNavigateToProductDetail(product.barcode)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950)
                        ) {
                            Text("Full Analysis", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Manual Barcode Input Dialog
        if (state.showManualInputDialog) {
            var inputBarcode by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { viewModel.closeManualInputDialog() },
                containerColor = Slate900,
                title = { Text("Manual Barcode Lookup", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Enter a 13-digit EAN or 12-digit UPC barcode:", color = Slate300, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = inputBarcode,
                            onValueChange = { inputBarcode = it },
                            placeholder = { Text("e.g. 5000159461122", color = Slate500) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate800,
                                unfocusedContainerColor = Slate800,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Emerald400,
                                cursorColor = Emerald400
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.closeManualInputDialog()
                            viewModel.onBarcodeDetected(inputBarcode)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate950)
                    ) {
                        Text("Search")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closeManualInputDialog() }) {
                        Text("Cancel", color = Slate400)
                    }
                }
            )
        }

        // Snackbar for errors
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = TrafficRed.copy(alpha = 0.9f),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        )
    }
}

@Composable
fun CameraPreviewView(onBarcodeDetected: (String) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val barcodeScanner = BarcodeScanning.getClient()

                @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                barcodeScanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            barcode.rawValue?.let { raw ->
                                                onBarcodeDetected(raw)
                                            }
                                        }
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                } catch (e: Exception) {
                    // Ignored on emulators
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}
