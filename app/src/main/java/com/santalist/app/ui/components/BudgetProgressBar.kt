package com.santalist.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.domain.Formatters
import com.santalist.app.ui.theme.SantaListTheme
import kotlin.math.min

@Composable
fun BudgetProgressBar(
    planned: Int,
    budget: Int,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors

    val ratio = if (budget > 0) planned.toFloat() / budget.toFloat() else if (planned > 0) 1f else 0f
    val isOver = planned > budget

    val animatedProgress by animateFloatAsState(
        targetValue = min(ratio, 1f),
        label = "budget_progress"
    )

    val progressColor = if (isOver) colors.cranberryRed else colors.pineGreen
    val pct = (ratio * 100).toInt().coerceAtMost(999)

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.18f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(progressColor)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$pct% of budget",
                color = if (isOver) colors.cranberryRed else colors.textSecondary,
                fontSize = 11.sp,
                fontWeight = if (isOver) FontWeight.Bold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isOver) {
                Text(
                    text = "Over ${Formatters.formatVND(planned - budget)}",
                    color = colors.cranberryRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "Remaining ${Formatters.formatVND(budget - planned)}",
                    color = colors.pineGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
