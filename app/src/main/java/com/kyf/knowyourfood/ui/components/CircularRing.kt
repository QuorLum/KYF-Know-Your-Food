package com.kyf.knowyourfood.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyf.knowyourfood.ui.theme.Emerald400

@Composable
fun CircularRing(
    percentage: Float, // 0.0f to 1.0f or percentage
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    strokeWidth: Dp = 5.dp,
    ringColor: Color = Emerald400,
    trackColor: Color = Color.White.copy(alpha = 0.1f),
    content: @Composable (() -> Unit)? = null
) {
    val normalizedPct = if (percentage > 1.0f) (percentage / 100f).coerceIn(0f, 1f) else percentage.coerceIn(0f, 1f)
    val sweepAngle = 360f * normalizedPct

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            // Track background
            drawCircle(
                color = trackColor,
                radius = (this.size.minDimension - strokeWidth.toPx()) / 2f,
                style = stroke
            )
            // Progress arc
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = stroke
            )
        }
        if (content != null) {
            content()
        }
    }
}
