package com.kyf.knowyourfood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyf.knowyourfood.data.model.NutriScoreGrade
import com.kyf.knowyourfood.ui.theme.*

@Composable
fun NutriScoreBadge(
    grade: NutriScoreGrade,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val grades = listOf(
        Pair("A", NutriScoreA),
        Pair("B", NutriScoreB),
        Pair("C", NutriScoreC),
        Pair("D", NutriScoreD),
        Pair("E", NutriScoreE)
    )

    if (compact) {
        // Compact single badge pill
        val color = when (grade) {
            NutriScoreGrade.A -> NutriScoreA
            NutriScoreGrade.B -> NutriScoreB
            NutriScoreGrade.C -> NutriScoreC
            NutriScoreGrade.D -> NutriScoreD
            NutriScoreGrade.E -> NutriScoreE
        }
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .padding(horizontal = 8.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "NUTRI-SCORE ",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = grade.letter,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    } else {
        // Full 5-tab visual bar
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Slate800)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            grades.forEach { (letter, color) ->
                val isSelected = letter.equals(grade.letter, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) color else color.copy(alpha = 0.25f))
                        .padding(horizontal = if (isSelected) 10.dp else 6.dp, vertical = if (isSelected) 6.dp else 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letter,
                        fontSize = if (isSelected) 14.sp else 11.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
