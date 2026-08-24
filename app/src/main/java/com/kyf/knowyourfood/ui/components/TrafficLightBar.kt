package com.kyf.knowyourfood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.model.TrafficLightLevel
import com.kyf.knowyourfood.data.model.TrafficLights
import com.kyf.knowyourfood.ui.theme.*

@Composable
fun TrafficLightBar(
    trafficLights: TrafficLights,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TrafficLightPill(
            label = "Fat",
            value = "${String.format("%.1f", trafficLights.totalFatValue)}g",
            level = trafficLights.totalFatLevel,
            modifier = Modifier.weight(1f)
        )
        TrafficLightPill(
            label = "Sat Fat",
            value = "${String.format("%.1f", trafficLights.satFatValue)}g",
            level = trafficLights.satFatLevel,
            modifier = Modifier.weight(1f)
        )
        TrafficLightPill(
            label = "Sugars",
            value = "${String.format("%.1f", trafficLights.sugarsValue)}g",
            level = trafficLights.sugarsLevel,
            modifier = Modifier.weight(1f)
        )
        TrafficLightPill(
            label = "Salt",
            value = "${String.format("%.2f", trafficLights.saltValue)}g",
            level = trafficLights.saltLevel,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TrafficLightPill(
    label: String,
    value: String,
    level: TrafficLightLevel,
    modifier: Modifier = Modifier
) {
    val (color, levelText) = when (level) {
        TrafficLightLevel.LOW -> Pair(TrafficGreen, "LOW")
        TrafficLightLevel.MEDIUM -> Pair(TrafficYellow, "MED")
        TrafficLightLevel.HIGH -> Pair(TrafficRed, "HIGH")
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Slate800)
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = Slate400
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(color)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = levelText,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = if (level == TrafficLightLevel.MEDIUM) Slate950 else Color.White
            )
        }
    }
}
