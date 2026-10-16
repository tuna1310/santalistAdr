package com.santalist.app.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.R
import com.santalist.app.data.model.ActivityRecord
import com.santalist.app.domain.Formatters
import com.santalist.app.domain.PipReminder
import com.santalist.app.domain.PipReminderType
import com.santalist.app.ui.components.MessagesSegmentedChips
import com.santalist.app.ui.components.PipMascot
import com.santalist.app.ui.components.PipPose
import com.santalist.app.ui.components.PipSpeechBubble
import com.santalist.app.ui.theme.SantaListTheme

@Composable
fun MessagesScreen(
    reminders: List<PipReminder>,
    activities: List<ActivityRecord>,
    onReminderAction: (PipReminder) -> Unit,
    onNavigateToPerson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors
    var selectedChipIndex by rememberSaveable { mutableIntStateOf(0) }

    val subtitle = when {
        reminders.isEmpty() -> stringResource(R.string.messages_empty_subtitle)
        reminders.size == 1 -> stringResource(R.string.messages_active_subtitle_one)
        else -> stringResource(R.string.messages_active_subtitle_many, reminders.size)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.appBackground)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Title Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.messages_title),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 15.sp,
                color = colors.textSecondary,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Segmented Chips: "From Pip" & "What you did"
        MessagesSegmentedChips(
            selectedTabIndex = selectedChipIndex,
            onTabSelected = { selectedChipIndex = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedChipIndex == 0) {
            // --- TAB 1: FROM PIP ---
            if (reminders.isEmpty()) {
                // EMPTY STATE (Matches screenshot 4)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(48.dp))

                    // Pip Mascot in Pose 4: SLEEPING
                    PipMascot(
                        pose = PipPose.SLEEPING,
                        width = 145.dp,
                        height = 175.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Speech Bubble
                    PipSpeechBubble(
                        text = stringResource(R.string.pip_bubble_messages),
                        backgroundColor = colors.pipBubbleBg
                    )
                }
            } else {
                // Populated Reminders List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    items(reminders, key = { it.id }) { reminder ->
                        PipReminderCard(
                            reminder = reminder,
                            onActionClick = {
                                if (reminder.targetPersonId != null &&
                                    (reminder.type == PipReminderType.PERSON_OVER_BUDGET ||
                                            reminder.type == PipReminderType.PERSON_NO_GIFTS ||
                                            reminder.type == PipReminderType.NO_BUDGET_SET)
                                ) {
                                    onNavigateToPerson(reminder.targetPersonId)
                                } else {
                                    onReminderAction(reminder)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(84.dp))
                    }
                }
            }
        } else {
            // --- TAB 2: WHAT YOU DID (Activity Log) ---
            if (activities.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = colors.textSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.activity_empty),
                        fontSize = 16.sp,
                        color = colors.textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    items(activities, key = { it.id }) { activity ->
                        ActivityCard(activity = activity)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(84.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PipReminderCard(
    reminder: PipReminder,
    onActionClick: () -> Unit
) {
    val colors = SantaListTheme.colors

    val (icon, iconTint, iconBg) = when (reminder.type) {
        PipReminderType.OVERDUE_GIFT -> Triple(Icons.Default.Warning, colors.cranberryRed, colors.cranberryRed.copy(alpha = 0.12f))
        PipReminderType.DUE_SOON -> Triple(Icons.Default.HourglassTop, colors.statusOrange, colors.statusOrange.copy(alpha = 0.12f))
        PipReminderType.PERSON_OVER_BUDGET, PipReminderType.TOTAL_OVER_BUDGET -> Triple(Icons.Default.AttachMoney, colors.cranberryRed, colors.cranberryRed.copy(alpha = 0.12f))
        PipReminderType.PERSON_NO_GIFTS -> Triple(Icons.Default.CardGiftcard, colors.pineGreen, colors.pineGreen.copy(alpha = 0.12f))
        PipReminderType.NO_BUDGET_SET -> Triple(Icons.Default.AttachMoney, colors.warmGold, colors.warmGold.copy(alpha = 0.12f))
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = colors.appCard),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconBg,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = reminder.subtitle,
                        fontSize = 13.5.sp,
                        color = colors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.pipPrimaryButton,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(
                    text = reminder.actionText,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ActivityCard(activity: ActivityRecord) {
    val colors = SantaListTheme.colors

    val icon: ImageVector = when {
        activity.actionType.startsWith("ADD_PERSON") -> Icons.Default.Person
        activity.actionType.startsWith("ADD_GIFT") -> Icons.Default.CardGiftcard
        activity.actionType.startsWith("UPDATE_GIFT_STATUS") -> Icons.Default.Check
        activity.actionType.startsWith("DELETE") -> Icons.Default.Delete
        else -> Icons.Default.Edit
    }

    val timeFormatted = remember(activity.timestamp) {
        val diff = System.currentTimeMillis() - activity.timestamp
        when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> "${diff / 3600_000}h ago"
            else -> Formatters.formatDate(activity.timestamp)
        }
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.appCard),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = colors.pipNavSelectedChip,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colors.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                if (activity.details.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = activity.details,
                        fontSize = 12.5.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = timeFormatted,
                fontSize = 12.sp,
                color = colors.textSecondary
            )
        }
    }
}
