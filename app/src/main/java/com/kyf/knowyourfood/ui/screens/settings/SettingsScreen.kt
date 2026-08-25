package com.kyf.knowyourfood.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfiles: () -> Unit
) {
    var isMetric by remember { mutableStateOf(true) }
    var isOfflineFirst by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("English") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings & Preferences", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Health Preferences Group
            item {
                Text(text = "HEALTH PREFERENCES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate400)
                Spacer(modifier = Modifier.height(8.dp))
                GlassmorphicCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Slate900) {
                    Column {
                        SettingLinkRow(
                            icon = Icons.Default.Shield,
                            tint = Emerald400,
                            title = "Allergens & Sensitivities",
                            subtitle = "Guarded ingredients & strict trace mode",
                            onClick = onNavigateToProfiles
                        )
                        Divider(color = Slate800)
                        SettingLinkRow(
                            icon = Icons.Default.Favorite,
                            tint = Color(0xFFF472B6),
                            title = "Family Dietary Profiles",
                            subtitle = "Manage family profiles & limits",
                            onClick = onNavigateToProfiles
                        )
                    }
                }
            }

            // App Preferences Group
            item {
                Text(text = "APP PREFERENCES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate400)
                Spacer(modifier = Modifier.height(8.dp))
                GlassmorphicCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Slate900) {
                    Column {
                        SettingToggleRow(
                            icon = Icons.Default.Straighten,
                            tint = Cyan400,
                            title = "Units of Measurement",
                            subtitle = if (isMetric) "Metric (g, kg, kcal)" else "Imperial (oz, lb, cal)",
                            isChecked = isMetric,
                            onCheckedChange = { isMetric = it }
                        )
                        Divider(color = Slate800)
                        SettingToggleRow(
                            icon = Icons.Default.WifiOff,
                            tint = Emerald400,
                            title = "Offline-First Sync & Cache",
                            subtitle = if (isOfflineFirst) "On · Instant offline lookup" else "Off",
                            isChecked = isOfflineFirst,
                            onCheckedChange = { isOfflineFirst = it }
                        )
                    }
                }
            }

            // Data & Privacy
            item {
                Text(text = "DATA & PRIVACY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate400)
                Spacer(modifier = Modifier.height(8.dp))
                GlassmorphicCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Slate900) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🛡️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("100% Private On-Device Execution", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Your health profile and scanned data are stored securely on this phone.", fontSize = 11.sp, color = Slate400)
                            }
                        }
                    }
                }
            }

            // About Card
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Slate900) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Know Your Food (KYF)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Version 1.0.0 · Gemini AI & OpenFoodFacts", fontSize = 11.sp, color = Slate400)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Emerald500.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("v1.0.0", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald400)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingLinkRow(
    icon: ImageVector,
    tint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(tint.copy(alpha = 0.15f))
                    .padding(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = subtitle, fontSize = 11.sp, color = Slate400)
            }
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Slate400, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun SettingToggleRow(
    icon: ImageVector,
    tint: Color,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
                    .clip(RoundedCornerShape(8.dp))
                    .background(tint.copy(alpha = 0.15f))
                    .padding(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = subtitle, fontSize = 11.sp, color = Slate400)
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Slate950,
                checkedTrackColor = Emerald400,
                uncheckedThumbColor = Slate400,
                uncheckedTrackColor = Slate800
            )
        )
    }
}
