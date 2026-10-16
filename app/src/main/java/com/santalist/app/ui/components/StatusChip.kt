package com.santalist.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.data.model.GiftStatus
import com.santalist.app.ui.theme.SantaListTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatusChip(
    status: GiftStatus,
    onNext: () -> Unit = {},
    onSelect: (GiftStatus) -> Unit = {},
    modifier: Modifier = Modifier,
    onStatusClick: (() -> Unit)? = null,
    onStatusSelected: ((GiftStatus) -> Unit)? = null
) {
    val handleNext = onStatusClick ?: onNext
    val handleSelect = onStatusSelected ?: onSelect
    var expanded by remember { mutableStateOf(false) }
    val colors = SantaListTheme.colors

    val statusColor = when (status) {
        GiftStatus.IDEA -> colors.warmGold
        GiftStatus.ORDERED -> colors.statusOrange
        GiftStatus.RECEIVED -> colors.pineGreen
        GiftStatus.WRAPPED -> colors.pineGreen
    }

    val icon: ImageVector = when (status) {
        GiftStatus.IDEA -> Icons.Default.Lightbulb
        GiftStatus.ORDERED -> Icons.Default.Inventory2
        GiftStatus.RECEIVED -> Icons.Default.CheckCircle
        GiftStatus.WRAPPED -> Icons.Default.CardGiftcard
    }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(statusColor.copy(alpha = 0.15f))
                .border(1.dp, statusColor.copy(alpha = 0.35f), CircleShape)
                .combinedClickable(
                    onClick = handleNext,
                    onLongClick = { expanded = true }
                )
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .semantics {
                    contentDescription = "Status: ${status.title}. Tap to cycle to next status, long press to select."
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = status.title,
                color = statusColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colors.appCard)
        ) {
            GiftStatus.entries.forEach { option ->
                val optColor = when (option) {
                    GiftStatus.IDEA -> colors.warmGold
                    GiftStatus.ORDERED -> colors.statusOrange
                    GiftStatus.RECEIVED -> colors.pineGreen
                    GiftStatus.WRAPPED -> colors.pineGreen
                }
                val optIcon = when (option) {
                    GiftStatus.IDEA -> Icons.Default.Lightbulb
                    GiftStatus.ORDERED -> Icons.Default.Inventory2
                    GiftStatus.RECEIVED -> Icons.Default.CheckCircle
                    GiftStatus.WRAPPED -> Icons.Default.CardGiftcard
                }

                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = optIcon,
                                contentDescription = null,
                                tint = optColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option.title,
                                color = colors.textPrimary,
                                fontWeight = if (option == status) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        handleSelect(option)
                    }
                )
            }
        }
    }
}
