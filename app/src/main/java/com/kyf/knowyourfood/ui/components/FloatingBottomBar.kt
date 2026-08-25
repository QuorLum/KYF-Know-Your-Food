package com.kyf.knowyourfood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.ui.navigation.Screen
import com.kyf.knowyourfood.ui.theme.*

@Composable
fun FloatingBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 1. Floating Pill Glassmorphic Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF0D1424).copy(alpha = 0.85f))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left 2 items: Home & History
                BottomNavItem(
                    label = "Home",
                    icon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    isSelected = currentRoute == Screen.Home.route,
                    onClick = { onNavigate(Screen.Home.route) }
                )

                BottomNavItem(
                    label = "History",
                    icon = Icons.Filled.History,
                    unselectedIcon = Icons.Outlined.History,
                    isSelected = currentRoute == Screen.History.route,
                    onClick = { onNavigate(Screen.History.route) }
                )

                // Spacer for the center floating button
                Spacer(modifier = Modifier.width(56.dp))

                // Right 2 items: Plate & Profile
                BottomNavItem(
                    label = "Plate",
                    icon = Icons.Filled.Restaurant,
                    unselectedIcon = Icons.Outlined.Restaurant,
                    isSelected = currentRoute == Screen.Plate.route,
                    onClick = { onNavigate(Screen.Plate.route) }
                )

                BottomNavItem(
                    label = "Profile",
                    icon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    isSelected = currentRoute == Screen.Profiles.route,
                    onClick = { onNavigate(Screen.Profiles.route) }
                )
            }
        }

        // 2. Central Elevated Floating Scan FAB
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-4).dp)
                .size(58.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = Emerald500.copy(alpha = 0.8f),
                    spotColor = Emerald500
                )
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Emerald400, Emerald500)
                    )
                )
                .border(2.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onNavigate(Screen.Scanner.route) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Scan",
                tint = Color(0xFF04220F),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isSelected) Emerald400 else Color.White.copy(alpha = 0.45f)

    Column(
        modifier = Modifier
            .width(56.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) icon else unselectedIcon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = tint
        )
    }
}
