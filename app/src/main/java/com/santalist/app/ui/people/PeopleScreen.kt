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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.data.model.Person
import com.santalist.app.data.model.PersonWithGifts
import com.santalist.app.domain.Formatters
import com.santalist.app.ui.components.AddEditGiftSheet
import com.santalist.app.ui.components.AddEditPersonSheet
import com.santalist.app.ui.components.AppHeader
import com.santalist.app.ui.components.BudgetProgressBar
import com.santalist.app.ui.components.EmptyStateView
import com.santalist.app.ui.theme.SantaListTheme

@Composable
fun PeopleScreen(
    viewModel: PeopleViewModel,
    onNavigateToPerson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val peopleWithGifts by viewModel.peopleWithGifts.collectAsState()
    val colors = SantaListTheme.colors

    var showAddPersonSheet by remember { mutableStateOf(false) }
    var personToEdit by remember { mutableStateOf<Person?>(null) }
    var showAddGiftSheet by remember { mutableStateOf(false) }

    var personToDelete by remember { mutableStateOf<PersonWithGifts?>(null) }

    val allPeople = peopleWithGifts.map { it.person }

    Scaffold(
        topBar = {
            Column {
                AppHeader()
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.appBackground)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { showAddPersonSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Person",
                            tint = colors.pineGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Person",
                            color = colors.pineGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (peopleWithGifts.isNotEmpty()) {
                        TextButton(onClick = { showAddGiftSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Add Gift",
                                tint = colors.pineGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add Gift",
                                color = colors.pineGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (peopleWithGifts.isNotEmpty()) {
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
        if (peopleWithGifts.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.PersonAdd,
                title = "No recipients yet",
                description = "Add your loved ones to start tracking gifts and managing budgets.",
                actionLabel = "Add First Recipient",
                onActionClick = { showAddPersonSheet = true },
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

                items(peopleWithGifts, key = { it.person.id }) { pwg ->
                    PersonCard(
                        personWithGifts = pwg,
                        onClick = { onNavigateToPerson(pwg.person.id) },
                        onEdit = { personToEdit = pwg.person },
                        onDelete = { personToDelete = pwg }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp)) // Padding for FAB
                }
            }
        }
    }

    // Sheets and Dialogs
    if (showAddPersonSheet) {
        AddEditPersonSheet(
            personToEdit = null,
            onDismiss = { showAddPersonSheet = false },
            onSave = { name, budget, note ->
                viewModel.addPerson(name, budget, note)
            }
        )
    }

    if (personToEdit != null) {
        AddEditPersonSheet(
            personToEdit = personToEdit,
            onDismiss = { personToEdit = null },
            onSave = { name, budget, note ->
                viewModel.updatePerson(personToEdit!!, name, budget, note)
                personToEdit = null
            }
        )
    }

    if (showAddGiftSheet && allPeople.isNotEmpty()) {
        AddEditGiftSheet(
            giftToEdit = null,
            preselectedPersonId = null,
            availablePeople = allPeople,
            onDismiss = { showAddGiftSheet = false },
            onSave = { title, price, status, dueDate, occasion, note, url, personId ->
                viewModel.addGift(title, price, status, dueDate, occasion, note, url, personId)
            }
        )
    }

    if (personToDelete != null) {
        AlertDialog(
            onDismissRequest = { personToDelete = null },
            title = { Text("Delete Recipient") },
            text = { Text("Deleting \"${personToDelete?.person?.name}\" will remove all associated gifts. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        personToDelete?.let { viewModel.deletePerson(it) }
                        personToDelete = null
                    }
                ) {
                    Text("Delete Recipient & All Gifts", color = colors.cranberryRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { personToDelete = null }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }
}

@Composable
private fun PersonCard(
    personWithGifts: PersonWithGifts,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val person = personWithGifts.person
    val colors = SantaListTheme.colors
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(colors.appCard)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = person.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                if (person.note.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = person.note,
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        maxLines = 1
                    )
                }
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = colors.textSecondary
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(colors.appCard)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Recipient") },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Recipient", color = colors.cranberryRed) },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = colors.pineGreen,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${personWithGifts.gifts.size} gifts",
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Planned: ",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
                Text(
                    text = Formatters.formatVND(personWithGifts.plannedAmount),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (personWithGifts.isOverBudget) colors.cranberryRed else colors.pineGreen
                )
                Text(
                    text = " / ${Formatters.formatVND(person.budget)}",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        BudgetProgressBar(planned = personWithGifts.plannedAmount, budget = person.budget)
    }
}
