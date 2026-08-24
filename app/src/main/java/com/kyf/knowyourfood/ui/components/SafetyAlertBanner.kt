package com.kyf.knowyourfood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.model.SafetyAssessment
import com.kyf.knowyourfood.data.model.SafetyStatus
import com.kyf.knowyourfood.ui.theme.*

@Composable
fun SafetyAlertBanner(
    assessment: SafetyAssessment,
    modifier: Modifier = Modifier
) {
    val (bgColor, borderColor, icon, statusText) = when (assessment.status) {
        SafetyStatus.SAFE -> Quadruple(
            Color(0x2210B981),
            TrafficGreen,
            Icons.Default.CheckCircle,
            "SAFE FOR YOUR PROFILE"
        )
        SafetyStatus.CAUTION -> Quadruple(
            Color(0x22F59E0B),
            TrafficYellow,
            Icons.Default.WarningAmber,
            "USE CAUTION (TRACES / POLLEN)"
        )
        SafetyStatus.UNSAFE -> Quadruple(
            Color(0x22EF4444),
            TrafficRed,
            Icons.Default.Warning,
            "NOT RECOMMENDED (ALLERGEN / AGE)"
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = borderColor,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = statusText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = assessment.overallScoreText,
                    fontSize = 12.sp,
                    color = Slate200
                )
            }
        }

        // Direct Allergen Conflict Tags (RED)
        if (assessment.directAllergenMatches.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Direct Allergen Conflicts:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TrafficRed
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                assessment.directAllergenMatches.forEach { match ->
                    RiskChip(text = "${match.triggerName} (${match.matchedTerm})", color = TrafficRed)
                }
            }
        }

        // Non-IgE Special Condition Tags (RED)
        if (assessment.nonIgEMatches.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Special Dietary Condition Alerts:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TrafficRed
            )
            Spacer(modifier = Modifier.height(4.dp))
            assessment.nonIgEMatches.forEach { match ->
                Text(
                    text = "• ${match.description}",
                    fontSize = 11.sp,
                    color = Slate200
                )
            }
        }

        // Age / Pediatric Guardrail Alerts (RED / AMBER)
        if (assessment.ageAlerts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            assessment.ageAlerts.forEach { alert ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x33EF4444))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👶 ${alert.title}: ${alert.reason}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }

        // Precautionary Traces (AMBER)
        if (assessment.traceAllergenMatches.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Precautionary Facility Trace Warnings:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TrafficYellow
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                assessment.traceAllergenMatches.forEach { match ->
                    RiskChip(text = "May contain ${match.triggerName}", color = TrafficYellow)
                }
            }
        }

        // Pollen-Food Cross-Reactivity (AMBER)
        if (assessment.pollenCrossMatches.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pollen Cross-Reactivity (OAS Triggers):",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Cyan400
            )
            Spacer(modifier = Modifier.height(4.dp))
            assessment.pollenCrossMatches.forEach { match ->
                Text(
                    text = "• ${match.description}",
                    fontSize = 11.sp,
                    color = Slate200
                )
            }
        }
    }
}

@Composable
fun RiskChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
