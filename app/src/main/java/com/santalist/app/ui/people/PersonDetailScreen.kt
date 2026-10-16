package com.santalist.app.ui.people

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.santalist.app.data.model.Person
import com.santalist.app.domain.Formatters
import com.santalist.app.ui.components.AddEditGiftSheet
import com.santalist.app.ui.components.AddEditPersonSheet
import com.santalist.app.ui.components.BudgetProgressBar
import com.santalist.app.ui.components.StatusChip
import com.santalist.app.ui.theme.SantaListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    viewModel: PersonDetailViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val personWithGifts by viewModel.personWithGifts.collectAsState()
    val colors = SantaListTheme.colors

    var showEditPersonSheet by remember { mutableStateOf(false) }
    var showAddGiftSheet by remember { mutableStateOf(false) }
    var giftToEdit by remember { mutableStateOf<Gift?>(null) }

    var giftToDelete by remember { mutableStateOf<Gift?>(null) }
    var showDeletePersonDialog by remember { mutableStateOf(false) }

    val pwg = personWithGifts

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = pwg?.person?.name ?: "",
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.pineGreen
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeletePersonDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Recipient",
                            tint = colors.cranberryRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.appBackground)
            )
        },
        containerColor = colors.appBackground,
        modifier = modifier
    ) { innerPadding ->
        if (pwg == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            return@Scaffold
        }

        val person = pwg.person
        val gifts = pwg.gifts.sortedBy { it.dueDate ?: Long.MAX_VALUE }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Budget Summary Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.appCard)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = person.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            if (person.note.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = person.note,
                                    fontSize = 13.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }

                        IconButton(onClick = { showEditPersonSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Recipient",
                                tint = colors.pineGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = colors.textSecondary.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // 3 Mini KPI cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MiniKpi(
                            title = "Budget",
                            amount = Formatters.formatVND(person.budget),
                            color = colors.pineGreen,
                            modifier = Modifier.weight(1f)
                        )
                        MiniKpi(
                            title = "Planned",
                            amount = Formatters.formatVND(pwg.plannedAmount),
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        MiniKpi(
                            title = "Remaining",
                            amount = Formatters.formatVND(pwg.remainingAmount),
                            color = if (pwg.isOverBudget) colors.cranberryRed else colors.pineGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    BudgetProgressBar(planned = pwg.plannedAmount, budget = person.budget)
                }
            }

            // Gifts section header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Gifts (${gifts.size})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    TextButton(onClick = { showAddGiftSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = colors.pineGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Gift",
                            color = colors.pineGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (gifts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.appCard)
                            .padding(vertical = 36.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = colors.pineGreen.copy(alpha = 0.4f),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No gifts yet for ${person.name}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap Add Gift to start building the holiday gift list!",
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddGiftSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.pineGreen)
                        ) {
                            Text("Add Gift Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(gifts, key = { it.id }) { gift ->
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
                                    .clip(RoundedCornerShape(14.dp))
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
                        GiftDetailRow(
                            gift = gift,
                            onClick = { giftToEdit = gift },
                            onNextStatus = { viewModel.advanceStatus(gift) },
                            onSelectStatus = { viewModel.setStatus(gift, it) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Sheets & Dialogs
    if (showEditPersonSheet && pwg != null) {
        AddEditPersonSheet(
            personToEdit = pwg.person,
            onDismiss = { showEditPersonSheet = false },
            onSave = { name, budget, note, _ ->
                viewModel.updatePerson(name, budget, note)
            }
        )
    }

    if (showAddGiftSheet && pwg != null) {
        AddEditGiftSheet(
            giftToEdit = null,
            preselectedPersonId = pwg.person.id,
            availablePeople = listOf(pwg.person),
            onDismiss = { showAddGiftSheet = false },
            onSave = { title, price, status, dueDate, occasion, note, url, _, _ ->
                viewModel.addGift(title, price, status, dueDate, occasion, note, url)
            }
        )
    }

    if (giftToEdit != null && pwg != null) {
        AddEditGiftSheet(
            giftToEdit = giftToEdit,
            preselectedPersonId = pwg.person.id,
            availablePeople = listOf(pwg.person),
            onDismiss = { giftToEdit = null },
            onSave = { title, price, status, dueDate, occasion, note, url, pId, _ ->
                viewModel.updateGift(giftToEdit!!, title, price, status, dueDate, occasion, note, url, pId)
                giftToEdit = null
            }
        )
    }

    if (showDeletePersonDialog && pwg != null) {
        AlertDialog(
            onDismissRequest = { showDeletePersonDialog = false },
            title = { Text("Delete Recipient") },
            text = { Text("Deleting \"${pwg.person.name}\" will remove all associated gifts. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeletePersonDialog = false
                        viewModel.deletePerson()
                        onNavigateBack()
                    }
                ) {
                    Text("Delete Recipient & All Gifts", color = colors.cranberryRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePersonDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
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
private fun MiniKpi(
    title: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            color = SantaListTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = amount,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun GiftDetailRow(
    gift: Gift,
    onClick: () -> Unit,
    onNextStatus: () -> Unit,
    onSelectStatus: (com.santalist.app.data.model.GiftStatus) -> Unit
) {
    val colors = SantaListTheme.colors
    val isOverdue = gift.isOverdue()

    val occasionIcon: ImageVector = when (gift.occasion) {
        Occasion.CHRISTMAS -> Icons.Default.Park
        Occasion.BIRTHDAY -> Icons.Default.Cake
        Occasion.ANNIVERSARY -> Icons.Default.Favorite
        Occasion.OTHER -> Icons.Default.AutoAwesome
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(colors.appCard)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Occasion icon circle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(colors.pineGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = occasionIcon,
                contentDescription = null,
                tint = colors.pineGreen,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = gift.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = Formatters.formatVND(gift.price),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.pineGreen
                )

                if (gift.dueDate != null) {
                    Text(text = " • ", color = colors.textSecondary)
                    if (isOverdue) {
                        Text(
                            text = "Overdue (${Formatters.formatDueDate(gift.dueDate)})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.cranberryRed
                        )
                    } else {
                        Text(
                            text = Formatters.formatDueDate(gift.dueDate),
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        StatusChip(
            status = gift.status,
            onNext = onNextStatus,
            onSelect = onSelectStatus
        )
    }
}
