package com.santalist.app.ui.budget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.data.model.PersonWithGifts
import com.santalist.app.domain.Formatters
import com.santalist.app.ui.theme.SantaListTheme
import kotlin.math.max

@Composable
fun BudgetComparisonChart(
    peopleWithGifts: List<PersonWithGifts>,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors

    if (peopleWithGifts.isEmpty()) return

    val maxAmount = max(
        1,
        peopleWithGifts.maxOfOrNull { max(it.person.budget, it.plannedAmount) } ?: 1
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.appCard)
            .padding(16.dp)
    ) {
        // Chart Header & Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Budget & Planned Comparison",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            // Legend
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(colors.pineGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Budget",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(colors.warmGold)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Planned",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Canvas drawing bars and axes
        val pineGreen = colors.pineGreen
        val warmGold = colors.warmGold
        val gridColor = colors.textSecondary.copy(alpha = 0.15f)
        val textColorHex = if (colors.textPrimary == Color(0xFF111827)) 0xFF4B5563.toInt() else 0xFF9CA3AF.toInt()

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val chartWidth = size.width
            val chartHeight = size.height - 24.dp.toPx() // Reserve 24dp for bottom labels
            val leftAxisWidth = 36.dp.toPx()
            val drawableWidth = chartWidth - leftAxisWidth

            // Draw horizontal grid lines (0%, 50%, 100%)
            val gridLevels = listOf(1.0f, 0.5f, 0.0f)
            gridLevels.forEach { level ->
                val y = chartHeight * (1f - level)
                drawLine(
                    color = gridColor,
                    start = Offset(leftAxisWidth, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1.dp.toPx()
                )

                val labelVal = (maxAmount * level).toInt()
                val labelText = Formatters.formatCompactVND(labelVal)

                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = textColorHex
                        textSize = 10.sp.toPx()
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                    drawText(labelText, leftAxisWidth - 6.dp.toPx(), y + 4.dp.toPx(), paint)
                }
            }

            // Draw grouped bars for each person
            val groupCount = peopleWithGifts.size
            val groupWidth = drawableWidth / groupCount
            val barWidth = (groupWidth * 0.32f).coerceAtMost(28.dp.toPx())
            val barSpacing = 4.dp.toPx()

            peopleWithGifts.forEachIndexed { index, pwg ->
                val groupCenterX = leftAxisWidth + (index * groupWidth) + (groupWidth / 2f)

                // Budget bar
                val budgetRatio = (pwg.person.budget.toFloat() / maxAmount.toFloat()).coerceIn(0f, 1f)
                val budgetBarHeight = chartHeight * budgetRatio
                val budgetX = groupCenterX - barWidth - (barSpacing / 2f)
                val budgetY = chartHeight - budgetBarHeight

                drawRoundRect(
                    color = pineGreen,
                    topLeft = Offset(budgetX, budgetY),
                    size = Size(barWidth, budgetBarHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Planned bar
                val plannedRatio = (pwg.plannedAmount.toFloat() / maxAmount.toFloat()).coerceIn(0f, 1f)
                val plannedBarHeight = chartHeight * plannedRatio
                val plannedX = groupCenterX + (barSpacing / 2f)
                val plannedY = chartHeight - plannedBarHeight

                drawRoundRect(
                    color = warmGold,
                    topLeft = Offset(plannedX, plannedY),
                    size = Size(barWidth, plannedBarHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Bottom Person Name Label
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = textColorHex
                        textSize = 11.sp.toPx()
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    val displayName = if (pwg.person.name.length > 8) {
                        pwg.person.name.take(7) + "…"
                    } else {
                        pwg.person.name
                    }
                    drawText(displayName, groupCenterX, size.height - 4.dp.toPx(), paint)
                }
            }
        }
    }
}
