package com.santalist.app.ui.money

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.R
import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.PersonWithGifts
import com.santalist.app.data.model.Season
import com.santalist.app.domain.BudgetCalculator
import com.santalist.app.domain.Formatters
import com.santalist.app.ui.budget.BudgetComparisonChart
import com.santalist.app.ui.components.BudgetProgressBar
import com.santalist.app.ui.components.MoneyTopBar
import com.santalist.app.ui.components.PipActionButton
import com.santalist.app.ui.components.PipMascot
import com.santalist.app.ui.components.PipPose
import com.santalist.app.ui.components.PipSpeechBubble
import com.santalist.app.ui.theme.SantaListTheme

@Composable
fun MoneyScreen(
    currentYear: Int,
    seasons: List<Season>,
    peopleWithGifts: List<PersonWithGifts>,
    allGifts: List<Gift>,
    currency: String,
    onFilterOrSettingsClick: () -> Unit,
    onSetBudgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors
    val occasion = seasons.firstOrNull { it.year == currentYear }?.occasion ?: "Christmas"
    val allPeople = peopleWithGifts.map { it.person }

    val totalBudget = BudgetCalculator.totalBudget(allPeople)
    val totalPlanned = BudgetCalculator.totalPlanned(allGifts)
    val totalSpent = BudgetCalculator.totalSpent(allGifts)
    val totalRemaining = BudgetCalculator.totalRemaining(allPeople, allGifts)
    val isOver = BudgetCalculator.isOverBudget(allPeople, allGifts)

    val hasData = totalBudget > 0 || totalPlanned > 0 || totalSpent > 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.appBackground)
    ) {
        // 1. Top Bar
        MoneyTopBar(onFilterOrSettingsClick = onFilterOrSettingsClick)

        // 2. Title & Dynamic Occasion Subtitle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.money_title),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.money_subtitle, occasion, currentYear),
                fontSize = 15.sp,
                color = colors.textSecondary,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasData) {
            // --- EMPTY STATE (Matches screenshot 3) ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Pip Mascot in Pose 3: HUGGING_GIFT
                PipMascot(
                    pose = PipPose.HUGGING_GIFT,
                    width = 145.dp,
                    height = 175.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Speech Bubble
                PipSpeechBubble(
                    text = stringResource(R.string.pip_bubble_money),
                    backgroundColor = colors.pipBubbleBg
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Action Button (Set a budget)
                PipActionButton(
                    text = stringResource(R.string.pip_action_set_budget),
                    icon = Icons.Default.AttachMoney,
                    onClick = onSetBudgetClick,
                    backgroundColor = colors.pipPrimaryButton
                )
            }
        } else {
            // --- POPULATED STATE ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // 4 KPI Cards (2x2 Grid)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            KPICard(
                                title = stringResource(R.string.budget_total),
                                amount = Formatters.formatCurrency(totalBudget, currency),
                                icon = Icons.Default.AccountBalanceWallet,
                                iconColor = colors.pineGreen,
                                modifier = Modifier.weight(1f)
                            )
                            KPICard(
                                title = stringResource(R.string.budget_planned),
                                amount = Formatters.formatCurrency(totalPlanned, currency),
                                icon = Icons.AutoMirrored.Filled.ListAlt,
                                iconColor = colors.warmGold,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            KPICard(
                                title = stringResource(R.string.budget_spent),
                                amount = Formatters.formatCurrency(totalSpent, currency),
                                icon = Icons.Default.CreditCard,
                                iconColor = colors.statusOrange,
                                modifier = Modifier.weight(1f)
                            )
                            KPICard(
                                title = stringResource(R.string.budget_remaining),
                                amount = Formatters.formatCurrency(totalRemaining, currency),
                                icon = if (isOver) Icons.Default.Warning else Icons.Default.CheckCircle,
                                iconColor = if (isOver) colors.cranberryRed else colors.pineGreen,
                                isWarning = isOver,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }

                // Overall Budget Progress Bar
                if (totalBudget > 0) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.appCard),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                val progressFraction = (totalPlanned.toFloat() / totalBudget.toFloat()).coerceIn(0f, 1f)
                                val pct = if (totalBudget > 0) (totalPlanned * 100 / totalBudget) else 0

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$pct% of budget planned",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isOver) colors.cranberryRed else colors.textPrimary
                                    )
                                    Text(
                                        text = if (isOver) "Over budget!" else "On track",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = if (isOver) colors.cranberryRed else colors.pineGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (isOver) colors.cranberryRed else colors.pineGreen,
                                    trackColor = colors.appBackground,
                                    strokeCap = StrokeCap.Round
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }

                // Budget Comparison Chart
                item {
                    BudgetComparisonChart(peopleWithGifts = peopleWithGifts)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Breakdown by Recipient Header
                item {
                    Text(
                        text = stringResource(R.string.budget_breakdown_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                // Per-Person Breakdown List
                items(peopleWithGifts, key = { "breakdown_${it.person.id}" }) { pwg ->
                    val personPlanned = BudgetCalculator.personPlanned(pwg.gifts)
                    val personBudget = pwg.person.budget

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.appCard),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pwg.person.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "${Formatters.formatCurrency(personPlanned, currency)} / ${Formatters.formatCurrency(personBudget, currency)}",
                                    fontSize = 14.sp,
                                    color = colors.textSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            BudgetProgressBar(
                                budget = personBudget,
                                planned = personPlanned
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }
        }
    }
}

@Composable
private fun KPICard(
    title: String,
    amount: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    isWarning: Boolean = false
) {
    val colors = SantaListTheme.colors

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.appCard),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = amount,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (isWarning) colors.cranberryRed else colors.textPrimary
            )
        }
    }
}
