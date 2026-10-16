package com.santalist.app.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.Occasion
import com.santalist.app.domain.Formatters
import com.santalist.app.ui.components.AddEditGiftSheet
import com.santalist.app.ui.components.AppHeader
import com.santalist.app.ui.components.EmptyStateView
import com.santalist.app.ui.components.StatusChip
import com.santalist.app.ui.theme.SantaListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.timelineItems.collectAsState()
    val filter by viewModel.selectedFilter.collectAsState()
    val peopleWithGifts by viewModel.peopleWithGifts.collectAsState()
    val colors = SantaListTheme.colors

    var showAddGiftSheet by remember { mutableStateOf(false) }
    var giftToEdit by remember { mutableStateOf<Gift?>(null) }
    var giftToDelete by remember { mutableStateOf<Gift?>(null) }

    val allPeople = peopleWithGifts.map { it.person }
    val totalGiftsCount = peopleWithGifts.sumOf { it.gifts.size }

    Scaffold(
        topBar = {
            Column {
                AppHeader()

                // Filter Tabs (Segmented control)
                TabRow(
                    selectedTabIndex = filter.ordinal,
                    containerColor = colors.appBackground,
                    contentColor = colors.pineGreen,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[filter.ordinal]),
                            color = colors.pineGreen
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    TimelineFilter.entries.forEach { f ->
                        val selected = filter == f
                        Tab(
                            selected = selected,
                            onClick = { viewModel.setFilter(f) },
                            text = {
                                Text(
                                    text = f.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) colors.pineGreen else colors.textSecondary
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (allPeople.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .shadow(elevation = 6.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(colors.pineGreen)
                        .clickable { showAddGiftSheet = true }
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Gift",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Gift",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = colors.appBackground,
        modifier = modifier
    ) { innerPadding ->
        if (totalGiftsCount == 0) {
            EmptyStateView(
                icon = Icons.Default.Schedule,
                title = "No gifts yet",
                description = "The timeline organizes gifts by deadline so you never miss a moment.",
                actionLabel = if (allPeople.isNotEmpty()) "Add Gift Now" else null,
                onActionClick = { showAddGiftSheet = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else if (items.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Schedule,
                title = "No matching gifts",
                description = "No gifts found in \"${filter.title}\".",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                items(items, key = { it.gift.id }) { item ->
                    val gift = item.gift
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                giftToDelete = gift
                                false
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(colors.cranberryRed)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        }
                    ) {
                        TimelineGiftCard(
                            item = item,
                            onClick = { giftToEdit = gift },
                            onNextStatus = { viewModel.advanceStatus(gift, item.recipientName) },
                            onSelectStatus = { viewModel.setStatus(gift, it, item.recipientName) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp)) // FAB padding
                }
            }
        }
    }

    // Sheets & Dialogs
    if (showAddGiftSheet && allPeople.isNotEmpty()) {
        AddEditGiftSheet(
            giftToEdit = null,
            preselectedPersonId = null,
            availablePeople = allPeople,
            onDismiss = { showAddGiftSheet = false },
            onSave = { title, price, status, dueDate, occasion, note, url, personId, _ ->
                viewModel.addGift(title, price, status, dueDate, occasion, note, url, personId)
            }
        )
    }

    if (giftToEdit != null && allPeople.isNotEmpty()) {
        AddEditGiftSheet(
            giftToEdit = giftToEdit,
            preselectedPersonId = giftToEdit?.personId,
            availablePeople = allPeople,
            onDismiss = { giftToEdit = null },
            onSave = { title, price, status, dueDate, occasion, note, url, personId, _ ->
                viewModel.updateGift(giftToEdit!!, title, price, status, dueDate, occasion, note, url, personId)
                giftToEdit = null
            }
        )
    }

    if (giftToDelete != null) {
        AlertDialog(
            onDismissRequest = { giftToDelete = null },
            title = { Text("Delete Gift") },
            text = { Text("Are you sure you want to delete gift \"${giftToDelete?.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        giftToDelete?.let { viewModel.deleteGift(it) }
                        giftToDelete = null
                    }
                ) {
                    Text("Delete", color = colors.cranberryRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { giftToDelete = null }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }
}

@Composable
private fun TimelineGiftCard(
    item: TimelineItem,
    onClick: () -> Unit,
    onNextStatus: () -> Unit,
    onSelectStatus: (com.santalist.app.data.model.GiftStatus) -> Unit
) {
    val gift = item.gift
    val colors = SantaListTheme.colors
    val isOverdue = gift.isOverdue()
    val isCompleted = gift.status.isCompleted

    val badgeColor = when {
        isOverdue -> colors.cranberryRed
        isCompleted -> colors.pineGreen
        else -> colors.warmGold
    }

    val badgeIcon = when {
        isOverdue -> Icons.Default.Warning
        isCompleted -> Icons.Default.CheckCircle
        gift.dueDate != null -> Icons.Default.CalendarMonth
        else -> Icons.Default.EventBusy
    }

    val badgeText = when {
        gift.dueDate != null && isOverdue -> "Overdue • ${Formatters.formatDueDate(gift.dueDate)}"
        gift.dueDate != null && isCompleted -> "Completed • ${Formatters.formatDueDate(gift.dueDate)}"
        gift.dueDate != null -> "Due: ${Formatters.formatDueDate(gift.dueDate)}"
        else -> "No due date"
    }

    val occasionIcon: ImageVector = when (gift.occasion) {
        Occasion.CHRISTMAS -> Icons.Default.Park
        Occasion.BIRTHDAY -> Icons.Default.Cake
        Occasion.ANNIVERSARY -> Icons.Default.Favorite
        Occasion.OTHER -> Icons.Default.AutoAwesome
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(colors.appCard)
            .then(
                if (isOverdue) Modifier.border(1.5.dp, colors.cranberryRed.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Date Pill & Recipient row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = colors.pineGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.recipientName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Gift Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.pineGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = occasionIcon,
                    contentDescription = null,
                    tint = colors.pineGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = gift.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = Formatters.formatVND(gift.price),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.pineGreen
                    )
                    Text(text = " • ", color = colors.textSecondary)
                    Text(
                        text = gift.occasion.title,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            StatusChip(
                status = gift.status,
                onNext = onNextStatus,
                onSelect = onSelectStatus
            )
        }

        if (gift.note.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = gift.note,
                fontSize = 12.sp,
                color = colors.textSecondary,
                maxLines = 2
            )
        }
    }
}
