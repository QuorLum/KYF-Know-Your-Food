package com.kyf.knowyourfood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kyf.knowyourfood.ui.theme.*

@Composable
fun KYFAppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .drawBehind {
                // 1. Top-Left Emerald Ambient Radial Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Emerald500.copy(alpha = 0.30f), Color.Transparent),
                        center = Offset(size.width * 0.10f, size.height * -0.05f),
                        radius = size.width * 0.85f
                    )
                )

                // 2. Top-Right Cyan Accent Ambient Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Cyan400.copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(size.width * 1.05f, size.height * 0.08f),
                        radius = size.width * 0.80f
                    )
                )

                // 3. Bottom Indigo/Violet Subtle Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF6366F1).copy(alpha = 0.16f), Color.Transparent),
                        center = Offset(size.width * 0.50f, size.height * 1.05f),
                        radius = size.width * 0.75f
                    )
                )
            }
    ) {
        content()
    }
}
