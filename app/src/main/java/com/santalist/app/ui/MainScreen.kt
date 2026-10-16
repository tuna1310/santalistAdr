package com.santalist.app.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.santalist.app.R
import com.santalist.app.data.model.Person
import com.santalist.app.ui.components.AddEditGiftSheet
import com.santalist.app.ui.components.AddEditPersonSheet
import com.santalist.app.ui.components.AddSeasonDialog
import com.santalist.app.ui.components.FloatingNavBar
import com.santalist.app.ui.components.NavTab
import com.santalist.app.ui.components.SettingsSheet
import com.santalist.app.ui.home.HomeScreen
import com.santalist.app.ui.messages.MessagesScreen
import com.santalist.app.ui.money.MoneyScreen
import com.santalist.app.ui.people.PeopleScreen
import com.santalist.app.ui.people.PersonDetailScreen
import com.santalist.app.ui.people.PersonDetailViewModel
import com.santalist.app.ui.theme.SantaListTheme

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors

    var selectedTab by rememberSaveable { mutableStateOf(NavTab.HOME) }
    var selectedPersonDetailId by rememberSaveable { mutableStateOf<String?>(null) }

    // Dialog & Sheet States
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showAddSeasonDialog by remember { mutableStateOf(false) }
    var showQuickAddSheet by remember { mutableStateOf(false) }
    var showAddPersonSheet by remember { mutableStateOf(false) }
    var showAddGiftSheet by remember { mutableStateOf(false) }

    // ViewModel State Observation
    val currentYear by viewModel.currentYear.collectAsState()
    val seasons by viewModel.seasons.collectAsState()
    val peopleWithGifts by viewModel.peopleWithGifts.collectAsState()
    val giftsForYear by viewModel.giftsForYear.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val pipReminders by viewModel.pipReminders.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortOrder by viewModel.peopleSortOrder.collectAsState()

    val availablePeople = remember(peopleWithGifts) { peopleWithGifts.map { it.person } }

    if (selectedPersonDetailId != null) {
        val personId = selectedPersonDetailId!!
        val detailViewModel: PersonDetailViewModel = viewModel(
            key = "detail_$personId",
            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return PersonDetailViewModel(
                        application = viewModel.getApplication(),
                        personId = personId
                    ) as T
                }
            }
        )
        PersonDetailScreen(
            viewModel = detailViewModel,
            onNavigateBack = { selectedPersonDetailId = null }
        )
        return
    }

    Scaffold(
        bottomBar = {
            FloatingNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = colors.appBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding() / 3f)
        ) {
            when (selectedTab) {
                NavTab.HOME -> {
                    HomeScreen(
                        currentYear = currentYear,
                        seasons = seasons,
                        peopleWithGifts = peopleWithGifts,
                        allGifts = giftsForYear,
                        settings = settings,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onSelectYear = { viewModel.setYear(it) },
                        onAddNewSeason = { showAddSeasonDialog = true },
                        onSettingsClick = { showSettingsSheet = true },
                        onQuickAddClick = { showQuickAddSheet = true },
                        onAddFirstPersonClick = { showAddPersonSheet = true },
                        onGiftStatusCycle = { viewModel.cycleGiftStatus(it) },
                        onNavigateToPerson = { selectedPersonDetailId = it }
                    )
                }

                NavTab.PEOPLE -> {
                    PeopleScreen(
                        currentYear = currentYear,
                        peopleWithGifts = peopleWithGifts,
                        sortOrder = sortOrder,
                        currency = settings.currency,
                        onSortChange = { viewModel.setPeopleSortOrder(it) },
                        onAddPersonClick = { showAddPersonSheet = true },
                        onNavigateToPerson = { selectedPersonDetailId = it }
                    )
                }

                NavTab.MONEY -> {
                    MoneyScreen(
                        currentYear = currentYear,
                        seasons = seasons,
                        peopleWithGifts = peopleWithGifts,
                        allGifts = giftsForYear,
                        currency = settings.currency,
                        onFilterOrSettingsClick = { showSettingsSheet = true },
                        onSetBudgetClick = { showAddPersonSheet = true }
                    )
                }

                NavTab.MESSAGES -> {
                    MessagesScreen(
                        reminders = pipReminders,
                        activities = activities,
                        onReminderAction = { reminder ->
                            viewModel.handlePipReminderAction(reminder)
                        },
                        onNavigateToPerson = { personId ->
                            selectedPersonDetailId = personId
                        }
                    )
                }
            }
        }
    }

    // --- Bottom Sheets & Dialogs ---

    // Quick Add Sheet (Choice between Add Person and Add Gift)
    if (showQuickAddSheet) {
        QuickAddBottomSheet(
            onDismiss = { showQuickAddSheet = false },
            onAddPerson = {
                showQuickAddSheet = false
                showAddPersonSheet = true
            },
            onAddGift = {
                showQuickAddSheet = false
                if (availablePeople.isEmpty()) {
                    showAddPersonSheet = true
                } else {
                    showAddGiftSheet = true
                }
            }
        )
    }

    // Add Person Sheet
    if (showAddPersonSheet) {
        AddEditPersonSheet(
            personToEdit = null,
            currentYear = currentYear,
            currency = settings.currency,
            onDismiss = { showAddPersonSheet = false },
            onSave = { name, budget, note, year ->
                viewModel.insertPerson(name, budget, note, year)
            }
        )
    }

    // Add Gift Sheet
    if (showAddGiftSheet) {
        AddEditGiftSheet(
            giftToEdit = null,
            preselectedPersonId = availablePeople.firstOrNull()?.id,
            availablePeople = availablePeople,
            currentYear = currentYear,
            currency = settings.currency,
            onDismiss = { showAddGiftSheet = false },
            onSave = { title, price, status, dueDate, occasion, note, url, personId, year ->
                viewModel.insertGift(title, price, status, dueDate, occasion, note, url, personId, year)
            }
        )
    }

    // Settings Sheet
    if (showSettingsSheet) {
        SettingsSheet(
            settings = settings,
            onCurrencyChange = { viewModel.setCurrency(it) },
            onLanguageChange = { viewModel.setLanguage(it) },
            onNotificationsToggle = { viewModel.setNotificationsEnabled(it) },
            onThemeChange = { viewModel.setThemeMode(it) },
            onExportJson = { viewModel.exportDataAsJson() },
            onLoadSampleData = { viewModel.loadSampleData() },
            onClearAllData = { viewModel.clearAllData() },
            onDismiss = { showSettingsSheet = false }
        )
    }

    // Add Season Dialog
    if (showAddSeasonDialog) {
        AddSeasonDialog(
            onDismiss = { showAddSeasonDialog = false },
            onConfirm = { year, occasion ->
                viewModel.addSeason(year, occasion)
                showAddSeasonDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickAddBottomSheet(
    onDismiss: () -> Unit,
    onAddPerson: () -> Unit,
    onAddGift: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val colors = SantaListTheme.colors

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.appCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.action_quick_add),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Add Person Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddPerson() }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = colors.pipNavSelectedChip,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = colors.textPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = stringResource(R.string.action_add_person),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Add someone you buy gifts for",
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Gift Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddGift() }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = colors.pipNavSelectedChip,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = colors.textPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = stringResource(R.string.action_add_gift),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Add a gift idea or planned present",
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
