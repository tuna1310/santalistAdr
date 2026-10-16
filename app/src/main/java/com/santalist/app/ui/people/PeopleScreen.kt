package com.santalist.app.ui.people

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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.santalist.app.data.model.PersonWithGifts
import com.santalist.app.domain.BudgetCalculator
import com.santalist.app.domain.Formatters
import com.santalist.app.ui.components.PeopleTopBar
import com.santalist.app.ui.components.PipActionButton
import com.santalist.app.ui.components.PipMascot
import com.santalist.app.ui.components.PipPose
import com.santalist.app.ui.components.PipSpeechBubble
import com.santalist.app.ui.theme.SantaListTheme

@Composable
fun PeopleScreen(
    currentYear: Int,
    peopleWithGifts: List<PersonWithGifts>,
    sortOrder: String,
    currency: String,
    onSortChange: (String) -> Unit,
    onAddPersonClick: () -> Unit,
    onNavigateToPerson: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors
    val hasData = peopleWithGifts.isNotEmpty()

    val sortedPeople = remember(peopleWithGifts, sortOrder) {
        when (sortOrder) {
            "BUDGET" -> peopleWithGifts.sortedByDescending { it.person.budget }
            "DUE_DATE" -> peopleWithGifts.sortedBy { pwg ->
                pwg.gifts.mapNotNull { it.dueDate }.minOrNull() ?: Long.MAX_VALUE
            }
            else -> peopleWithGifts.sortedBy { it.person.name.lowercase() }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.appBackground)
    ) {
        // 1. Top Bar with Sort & Add Person
        PeopleTopBar(
            onSortChange = onSortChange,
            onAddPersonClick = onAddPersonClick
        )

        // 2. Title & Dynamic Subtitle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.people_title),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.people_subtitle, peopleWithGifts.size, currentYear),
                fontSize = 15.sp,
                color = colors.textSecondary,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasData) {
            // --- EMPTY STATE (Matches screenshot 2) ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Pip Mascot in Pose 2: CHEEK_REST
                PipMascot(
                    pose = PipPose.CHEEK_REST,
                    width = 145.dp,
                    height = 175.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Speech Bubble
                PipSpeechBubble(
                    text = stringResource(R.string.pip_bubble_people),
                    backgroundColor = colors.pipBubbleBg
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Action Button
                PipActionButton(
                    text = stringResource(R.string.pip_action_add_first_person),
                    icon = Icons.Outlined.PersonAdd,
                    onClick = onAddPersonClick,
                    backgroundColor = colors.pipPrimaryButton
                )
            }
        } else {
            // --- POPULATED STATE (List of Person Cards) ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                items(sortedPeople, key = { it.person.id }) { pwg ->
                    PersonCard(
                        personWithGifts = pwg,
                        currency = currency,
                        onClick = { onNavigateToPerson(pwg.person.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }
        }
    }
}

@Composable
private fun PersonCard(
    personWithGifts: PersonWithGifts,
    currency: String,
    onClick: () -> Unit
) {
    val colors = SantaListTheme.colors
    val person = personWithGifts.person
    val gifts = personWithGifts.gifts

    val initial = person.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val giftCount = gifts.size
    val planned = BudgetCalculator.personPlanned(gifts)
    val budget = person.budget
    val isOver = budget > 0 && planned > budget
    val progressFraction = if (budget > 0) (planned.toFloat() / budget.toFloat()).coerceIn(0f, 1f) else 0f

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = colors.appCard),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar initial
                Surface(
                    shape = CircleShape,
                    color = colors.pipNavSelectedChip,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initial,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Name and details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = person.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (giftCount == 1) "1 gift" else "$giftCount gifts",
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )

                        if (person.note.isNotBlank()) {
                            Text(
                                text = " • ${person.note}",
                                fontSize = 13.sp,
                                color = colors.textSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Spent / Budget
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = Formatters.formatCurrency(planned, currency),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOver) colors.cranberryRed else colors.textPrimary
                    )
                    if (budget > 0) {
                        Text(
                            text = "/ ${Formatters.formatCurrency(budget, currency)}",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            if (budget > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isOver) colors.cranberryRed else colors.pineGreen,
                    trackColor = colors.appBackground,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}
