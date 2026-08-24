package com.kyf.knowyourfood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.ui.theme.*

@Composable
fun ServingSlider(
    currentGrams: Double,
    onGramsChanged: (Double) -> Unit,
    modifier: Modifier = Modifier,
    minGrams: Float = 25f,
    maxGrams: Float = 500f
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Adjust Portion Size",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Slate400
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Emerald500.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${String.format("%.0f", currentGrams)} grams",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald400
                )
            }
        }

        Slider(
            value = currentGrams.toFloat(),
            onValueChange = { onGramsChanged(it.toDouble()) },
            valueRange = minGrams..maxGrams,
            steps = 18,
            colors = SliderDefaults.colors(
                thumbColor = Emerald400,
                activeTrackColor = Emerald500,
                inactiveTrackColor = Slate800
            )
        )

        // Quick Preset Chips (+25g, +50g, 100g base, +100g, 250g)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickPortionChip(text = "50g", onClick = { onGramsChanged(50.0) })
            QuickPortionChip(text = "100g (Std)", isSelected = currentGrams.toInt() == 100, onClick = { onGramsChanged(100.0) })
            QuickPortionChip(text = "150g", onClick = { onGramsChanged(150.0) })
            QuickPortionChip(text = "200g", onClick = { onGramsChanged(200.0) })
            QuickPortionChip(text = "+50g", onClick = { onGramsChanged((currentGrams + 50.0).coerceAtMost(500.0)) })
        }
    }
}

@Composable
fun QuickPortionChip(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) Emerald500 else Slate800)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Slate950 else Slate200
        )
    }
}
