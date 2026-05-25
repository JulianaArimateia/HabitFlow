package com.habitflow.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.ui.theme.HeatmapEmpty
import com.habitflow.ui.theme.OnSurfaceVar
import com.habitflow.ui.theme.Primary
import java.util.*

private val WEEK_LABELS: List<String>
    get() {
        val labels = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val dayNames = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
        for (i in 6 downTo 0) {
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            labels.add(dayNames[c.get(Calendar.DAY_OF_WEEK) - 1])
        }
        return labels
    }

@Composable
fun WeeklyBarChart(data: List<Float>, modifier: Modifier = Modifier) {
    val labels = WEEK_LABELS
    val barColor = MaterialTheme.colorScheme.primary
    val trackColor = HeatmapEmpty

    Column(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val barWidth = size.width / (data.size * 2f - 1f) * 1.4f
            val spacing = (size.width - barWidth * data.size) / (data.size - 1).coerceAtLeast(1)
            data.forEachIndexed { i, value ->
                val x = i * (barWidth + spacing)
                val barHeight = size.height * value.coerceIn(0f, 1f)
                // Track
                drawRoundRect(
                    color = trackColor,
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, size.height),
                    cornerRadius = CornerRadius(barWidth / 2)
                )
                // Bar
                if (barHeight > 0f) {
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, size.height - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(barWidth / 2)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            labels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
