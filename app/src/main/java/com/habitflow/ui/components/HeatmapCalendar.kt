package com.habitflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.ui.theme.*
import com.habitflow.util.DateUtils

private val DAY_HEADERS = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")

@Composable
fun HeatmapCalendar(data: Map<Long, Int>, modifier: Modifier = Modifier) {
    val days = DateUtils.currentMonthDays()
    val offset = DateUtils.firstDayOfMonthDayOfWeek()
    val maxVal = data.values.maxOrNull()?.coerceAtLeast(1) ?: 1

    Column(modifier = modifier) {
        // Day of week headers
        Row(Modifier.fillMaxWidth()) {
            DAY_HEADERS.forEach { label ->
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(label, fontSize = 9.sp, color = OnSurfaceVar)
                }
            }
        }
        Spacer(Modifier.height(4.dp))

        // Grid
        val totalCells = offset + days.size
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayIndex = cellIndex - offset
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (dayIndex < 0 || dayIndex >= days.size) Color.Transparent
                                else {
                                    val count = data[days[dayIndex]] ?: 0
                                    intensityColor(count, maxVal)
                                }
                            )
                    )
                }
            }
        }
    }
}

private fun intensityColor(count: Int, max: Int): Color {
    if (count == 0) return HeatmapEmpty
    val ratio = count.toFloat() / max
    return when {
        ratio <= 0.33f -> HeatmapLow
        ratio <= 0.66f -> HeatmapMid
        else           -> HeatmapHigh
    }
}
