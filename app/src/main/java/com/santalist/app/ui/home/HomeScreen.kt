package com.santalist.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.R
import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.Person
import com.santalist.app.data.model.PersonWithGifts
import com.santalist.app.data.model.Season
import com.santalist.app.domain.CountdownCalculator
import com.santalist.app.domain.Formatters
import com.santalist.app.domain.SettingsState
import com.santalist.app.ui.components.HomeTopBar
import com.santalist.app.ui.components.PipActionButton
import com.santalist.app.ui.components.PipMascot
import com.santalist.app.ui.components.PipPose
import com.santalist.app.ui.components.PipSpeechBubble
import com.santalist.app.ui.components.StatusChip
import com.santalist.app.ui.theme.SantaListTheme
import java.util.Calendar

@Composable
fun HomeScreen(
    currentYear: Int,
    seasons: List<Season>,
    peopleWithGifts: List<PersonWithGifts>,
    allGifts: List<Gift>,
    settings: SettingsState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectYear: (Int) -> Unit,
    onAddNewSeason: () -> Unit,
    onSettingsClick: () -> Unit,
    onQuickAddClick: () -> Unit,
    onAddFirstPersonClick: () -> Unit,
    onGiftStatusCycle: (Gift) -> Unit,
    onNavigateToPerson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors
    var searchBarVisible by remember { mutableStateOf(false) }

    val hasData = peopleWithGifts.isNotEmpty()
    val todayHeader = remember { Formatters.formatHeaderDate() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.appBackground)
    ) {
        // 1. Top Bar
        HomeTopBar(
            currentYear = currentYear,
            seasons = seasons,
            onSelectYear = onSelectYear,
            onAddNewSeason = onAddNewSeason,
            onSearchClick = {
                searchBarVisible = !searchBarVisible
                if (!searchBarVisible) onSearchQueryChange("")
            },
            onSettingsClick = onSettingsClick,
            onQuickAddClick = onQuickAddClick
        )

        // Search Bar (Toggleable)
        AnimatedVisibility(
            visible = searchBarVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.pineGreen,
                        unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.3f),
                        focusedContainerColor = colors.appCard,
                        unfocusedContainerColor = colors.appCard
                    )
                )
            }
        }

        // Screen Title Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.home_title),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = todayHeader,
                fontSize = 15.sp,
                color = colors.textSecondary,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasData) {
            // --- EMPTY STATE (Matches screenshot 1) ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Pip Mascot in Pose 1: WAVING
                PipMascot(
                    pose = PipPose.WAVING,
                    width = 145.dp,
                    height = 175.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Speech Bubble
                PipSpeechBubble(
                    text = stringResource(R.string.pip_bubble_home),
                    backgroundColor = colors.pipBubbleBg
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Primary Action Button (Add the first person)
                PipActionButton(
                    text = stringResource(R.string.pip_action_add_first_person),
                    icon = Icons.Outlined.PersonAdd,
                    onClick = onAddFirstPersonClick,
                    backgroundColor = colors.pipPrimaryButton
                )
            }
        } else {
            // --- POPULATED STATE ---
            PopulatedHomeContent(
                peopleWithGifts = peopleWithGifts,
                allGifts = allGifts,
                currency = settings.currency,
                searchQuery = searchQuery,
                onGiftStatusCycle = onGiftStatusCycle,
                onNavigateToPerson = onNavigateToPerson
            )
        }
    }
}

@Composable
private fun PopulatedHomeContent(
    peopleWithGifts: List<PersonWithGifts>,
    allGifts: List<Gift>,
    currency: String,
    searchQuery: String,
    onGiftStatusCycle: (Gift) -> Unit,
    onNavigateToPerson: (String) -> Unit
) {
    val colors = SantaListTheme.colors
    val now = remember { System.currentTimeMillis() }

    // Map gifts to person
    val personMap = remember(peopleWithGifts) {
        peopleWithGifts.associate { it.person.id to it.person }
    }

    // Filter gifts if searching
    val filteredGifts = remember(allGifts, searchQuery) {
        if (searchQuery.isBlank()) allGifts else {
            allGifts.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.note.contains(searchQuery, ignoreCase = true) ||
                        (personMap[it.personId]?.name?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    // Categorization
    val todayCal = Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val overdueGifts = filteredGifts.filter { it.isOverdue(now) }

    val dueTodayGifts = filteredGifts.filter { gift ->
        if (gift.status.isCompleted || gift.dueDate == null) false
        else {
            val dueCal = Calendar.getInstance().apply {
                timeInMillis = gift.dueDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            dueCal.timeInMillis == todayCal.timeInMillis
        }
    }

    val comingUpGifts = filteredGifts.filter { gift ->
        if (gift.status.isCompleted || gift.dueDate == null) false
        else {
            val dueCal = Calendar.getInstance().apply {
                timeInMillis = gift.dueDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val diffDays = ((dueCal.timeInMillis - todayCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
            diffDays in 1..14
        }
    }

    val completedCount = allGifts.count { it.status.isCompleted }
    val totalCount = allGifts.size
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Countdown & Overall Progress Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Countdown Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.appCard),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = CountdownCalculator.countdownText(),
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                }

                // Progress Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.appCard),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.overall_progress, completedCount, totalCount),
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = colors.pineGreen,
                            trackColor = colors.appBackground,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        // Section: Overdue
        if (overdueGifts.isNotEmpty()) {
            item {
                SectionHeader(title = stringResource(R.string.section_overdue), count = overdueGifts.size, color = colors.cranberryRed)
            }
            items(overdueGifts, key = { "overdue_${it.id}" }) { gift ->
                HomeGiftCard(
                    gift = gift,
                    person = personMap[gift.personId],
                    currency = currency,
                    onStatusCycle = { onGiftStatusCycle(gift) },
                    onNavigateToPerson = { onNavigateToPerson(gift.personId) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Section: Due today
        if (dueTodayGifts.isNotEmpty()) {
            item {
                SectionHeader(title = stringResource(R.string.section_due_today), count = dueTodayGifts.size, color = colors.statusOrange)
            }
            items(dueTodayGifts, key = { "today_${it.id}" }) { gift ->
                HomeGiftCard(
                    gift = gift,
                    person = personMap[gift.personId],
                    currency = currency,
                    onStatusCycle = { onGiftStatusCycle(gift) },
                    onNavigateToPerson = { onNavigateToPerson(gift.personId) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Section: Coming up
        if (comingUpGifts.isNotEmpty()) {
            item {
                SectionHeader(title = stringResource(R.string.section_coming_up), count = comingUpGifts.size, color = colors.pineGreen)
            }
            items(comingUpGifts, key = { "coming_${it.id}" }) { gift ->
                HomeGiftCard(
                    gift = gift,
                    person = personMap[gift.personId],
                    currency = currency,
                    onStatusCycle = { onGiftStatusCycle(gift) },
                    onNavigateToPerson = { onNavigateToPerson(gift.personId) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // If no urgent categories, show all gifts
        if (overdueGifts.isEmpty() && dueTodayGifts.isEmpty() && comingUpGifts.isEmpty()) {
            item {
                SectionHeader(title = stringResource(R.string.section_all_gifts), count = filteredGifts.size, color = colors.textPrimary)
            }
            items(filteredGifts, key = { "all_${it.id}" }) { gift ->
                HomeGiftCard(
                    gift = gift,
                    person = personMap[gift.personId],
                    currency = currency,
                    onStatusCycle = { onGiftStatusCycle(gift) },
                    onNavigateToPerson = { onNavigateToPerson(gift.personId) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Padding at bottom for floating navigation bar
        item {
            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String, count: Int, color: Color) {
    val colors = SantaListTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.width(6.dp))
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.12f),
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$count",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun HomeGiftCard(
    gift: Gift,
    person: Person?,
    currency: String,
    onStatusCycle: () -> Unit,
    onNavigateToPerson: () -> Unit
) {
    val colors = SantaListTheme.colors

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.appCard),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToPerson() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (person != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = person.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = gift.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = Formatters.formatCurrency(gift.price, currency),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )

                    if (gift.dueDate != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${Formatters.formatDueDate(gift.dueDate)}",
                            fontSize = 13.sp,
                            color = if (gift.isOverdue()) colors.cranberryRed else colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Status Chip (clickable to advance status)
            StatusChip(
                status = gift.status,
                onStatusClick = onStatusCycle,
                onStatusSelected = { /* handled by cycle */ }
            )
        }
    }
}
