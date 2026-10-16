package com.santalist.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.domain.CountdownCalculator
import com.santalist.app.ui.theme.SantaListTheme
import kotlinx.coroutines.delay

@Composable
fun AppHeader(modifier: Modifier = Modifier) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)
            currentTime = System.currentTimeMillis()
        }
    }

    val colors = SantaListTheme.colors
    val countdownText = CountdownCalculator.countdownText(currentTime)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.appBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🎅",
                        fontSize = 22.sp
                    )
                    Text(
                        text = "SantaList",
                        color = colors.pineGreen,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Gifts for the right people, on time, on budget",
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Christmas Countdown Badge
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.pineGreen.copy(alpha = 0.12f))
                    .border(1.dp, colors.pineGreen.copy(alpha = 0.25f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .semantics { contentDescription = countdownText },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = colors.warmGold,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = countdownText,
                    color = colors.pineGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        HorizontalDivider(
            color = colors.textSecondary.copy(alpha = 0.15f),
            thickness = 0.8.dp
        )
    }
}
