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
import com.kyf.knowyourfood.data.model.ProductItem
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.NutriScoreBadge
import com.kyf.knowyourfood.ui.components.SafetyAlertBanner
import com.kyf.knowyourfood.ui.components.TrafficLightBar
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
        } else {
            // Simulated Viewfinder when camera permission is unavailable or in emulator
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
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = Emerald400,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Barcode Scanner Active",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Select a test product below or enter a barcode manually to test instant allergy evaluation.",
                        fontSize = 13.sp,
                        color = Slate400,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Viewfinder Target Overlay
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(260.dp)
                .border(2.dp, Emerald400, RoundedCornerShape(20.dp))
        )

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

        // Bottom Fast Test Barcode Picker (Ideal for quick emulator testing)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "⚡ Instant Test Barcodes:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Emerald400,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.testProducts.take(8)) { product ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate900.copy(alpha = 0.9f))
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.brand,
                                fontSize = 12.sp,
                                color = Slate400
                            )
                            Text(
                                text = product.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
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
                                focusedBorderColor = Emerald400
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
    }
}

@Composable
fun CameraPreviewView(onBarcodeDetected: (String) -> Unit) {
    val context = LocalContext.current
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
