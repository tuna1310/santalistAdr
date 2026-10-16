package com.santalist.app.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.data.model.PersonWithGifts
import com.santalist.app.domain.Formatters
import com.santalist.app.ui.components.AppHeader
import com.santalist.app.ui.components.BudgetProgressBar
import com.santalist.app.ui.components.EmptyStateView
import com.santalist.app.ui.theme.SantaListTheme

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = SantaListTheme.colors

    Scaffold(
        topBar = {
            AppHeader()
        },
        containerColor = colors.appBackground,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
            }

            // 4 KPI Cards (2x2 Grid)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        KpiCard(
                            title = "Total Budget",
                            amount = Formatters.formatVND(state.totalBudget),
                            icon = Icons.Default.AccountBalanceWallet,
                            color = colors.pineGreen,
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = "Planned",
                            amount = Formatters.formatVND(state.totalPlanned),
                            icon = Icons.Default.Assignment,
                            color = colors.warmGold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        KpiCard(
                            title = "Spent",
                            amount = Formatters.formatVND(state.totalSpent),
                            icon = Icons.Default.CreditCard,
                            color = colors.statusOrange,
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = "Remaining",
                            amount = Formatters.formatVND(state.totalRemaining),
                            icon = if (state.isOverallOverBudget) Icons.Default.Warning else Icons.Default.CheckCircle,
                            color = if (state.isOverallOverBudget) colors.cranberryRed else colors.pineGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Budget Comparison Chart
            if (state.peopleWithGifts.isNotEmpty()) {
                item {
                    BudgetComparisonChart(
                        peopleWithGifts = state.peopleWithGifts,
                        modifier = Modifier.shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                    )
                }
            }

            // Breakdown by Recipient
            item {
                Text(
                    text = "Breakdown by Recipient",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (state.peopleWithGifts.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.BarChart,
                        title = "No budget data yet",
                        description = "Add recipients and set budgets to track your gift spending."
                    )
                }
            } else {
                items(state.peopleWithGifts, key = { it.person.id }) { pwg ->
                    PersonBudgetRow(pwg = pwg)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    amount: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors

    Column(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(colors.appCard)
            .padding(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = amount,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun PersonBudgetRow(pwg: PersonWithGifts) {
    val colors = SantaListTheme.colors
    val person = pwg.person

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(colors.appCard)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = person.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = Formatters.formatVND(pwg.plannedAmount),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (pwg.isOverBudget) colors.cranberryRed else colors.pineGreen
            )
            Text(
                text = " / ${Formatters.formatVND(person.budget)}",
                fontSize = 12.sp,
                color = colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        BudgetProgressBar(planned = pwg.plannedAmount, budget = person.budget)
    }
}
