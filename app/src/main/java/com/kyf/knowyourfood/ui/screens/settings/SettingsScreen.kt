package com.kyf.knowyourfood.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.ui.components.GlassmorphicCard
import com.kyf.knowyourfood.ui.components.ScreenHeader
import com.kyf.knowyourfood.ui.theme.*

private val languages = listOf("English", "हिन्दी", "Español", "Français")

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfiles: () -> Unit
) {
    val context = LocalContext.current
    var isMetric by remember { mutableStateOf(true) }
    var isOfflineMode by remember { mutableStateOf(true) }
    var languageIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Screen Header
        ScreenHeader(
            title = "Settings",
            onBack = onNavigateBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Group 1: Health Preferences
            item {
                Text(
                    text = "HEALTH PREFERENCES",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.45f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        SettingLinkRow(
                            icon = Icons.Default.Shield,
                            label = "Allergens & Sensitivities",
                            tint = Emerald400,
                            onClick = onNavigateToProfiles
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        SettingLinkRow(
                            icon = Icons.Default.Favorite,
                            label = "Dietary Preferences",
                            tint = Color(0xFFF87171),
                            onClick = onNavigateToProfiles
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        SettingLinkRow(
                            icon = Icons.Default.TrackChanges,
                            label = "Health Goals",
                            value = "Maintain weight",
                            tint = Cyan400,
                            onClick = onNavigateToProfiles
                        )
                    }
                }
            }

            // Group 2: App Preferences
            item {
                Text(
                    text = "APP PREFERENCES",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.45f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        SettingToggleRow(
                            icon = Icons.Default.Straighten,
                            label = "Units",
                            value = if (isMetric) "Metric (g, kg)" else "Imperial (oz, lb)",
                            isChecked = isMetric,
                            onCheckedChange = { isMetric = it },
                            tint = Color(0xFFA78BFA)
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        SettingLinkRow(
                            icon = Icons.Default.Language,
                            label = "Language",
                            value = languages[languageIndex],
                            tint = Emerald400,
                            onClick = {
                                languageIndex = (languageIndex + 1) % languages.size
                                Toast.makeText(context, "Language set to ${languages[languageIndex]}", Toast.LENGTH_SHORT).show()
                            }
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        SettingLinkRow(
                            icon = Icons.Default.Palette,
                            label = "Theme",
                            value = "Dark Frosted · Dark",
                            tint = TrafficYellow,
                            onClick = {
                                Toast.makeText(context, "Dark Frosted theme active", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // Group 3: Data & Privacy
            item {
                Text(
                    text = "DATA & PRIVACY",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.45f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        SettingToggleRow(
                            icon = Icons.Default.WifiOff,
                            label = "Offline-First Mode",
                            value = if (isOfflineMode) "On · Fully private" else "Off",
                            isChecked = isOfflineMode,
                            onCheckedChange = { isOfflineMode = it },
                            tint = Emerald400
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        SettingLinkRow(
                            icon = Icons.Default.Storage,
                            label = "Manage Offline Data",
                            value = "248 MB",
                            tint = Cyan400,
                            onClick = {
                                Toast.makeText(context, "Offline database is up to date · 248 MB", Toast.LENGTH_SHORT).show()
                            }
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        SettingLinkRow(
                            icon = Icons.Default.Info,
                            label = "About KYF",
                            value = "v1.0.0",
                            tint = Color(0xFFA78BFA),
                            onClick = {
                                Toast.makeText(context, "KYF — Know Your Food · v1.0.0", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // 100% Offline Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Emerald500.copy(alpha = 0.1f))
                        .border(1.dp, Emerald500.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.WifiOff, contentDescription = null, tint = Emerald400, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("100% Offline", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Emerald300)
                            Text("Your data lives only on this device.", fontSize = 11.5.sp, color = Color.White.copy(alpha = 0.55f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingLinkRow(
    icon: ImageVector,
    label: String,
    value: String? = null,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(17.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        if (value != null) {
            Text(
                text = value,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.45f)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.35f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    label: String,
    value: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(17.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.45f)
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Emerald500,
                uncheckedThumbColor = Slate400,
                uncheckedTrackColor = Slate800
            )
        )
    }
}
