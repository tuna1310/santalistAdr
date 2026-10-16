package com.santalist.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.R
import com.santalist.app.data.model.Season
import com.santalist.app.ui.theme.SantaListTheme

@Composable
fun HomeTopBar(
    currentYear: Int,
    seasons: List<Season>,
    onSelectYear: (Int) -> Unit,
    onAddNewSeason: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onQuickAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors
    var yearMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Top Left: Circular Cream "+" Button
        Surface(
            shape = CircleShape,
            color = colors.appCard,
            shadowElevation = 3.dp,
            modifier = Modifier.size(44.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onQuickAddClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_quick_add),
                    tint = colors.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Top Right: Floating Pill with Year dropdown, Search, Settings
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = colors.appCard,
            shadowElevation = 3.dp,
            modifier = Modifier.height(44.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp)
            ) {
                // Year Selector
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { yearMenuExpanded = true }
                            .padding(vertical = 6.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = "$currentYear",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Year",
                            tint = colors.textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = yearMenuExpanded,
                        onDismissRequest = { yearMenuExpanded = false }
                    ) {
                        seasons.forEach { season ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${season.year} (${season.occasion})",
                                        fontWeight = if (season.year == currentYear) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    yearMenuExpanded = false
                                    onSelectYear(season.year)
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.year_add_option),
                                    color = colors.pineGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = {
                                yearMenuExpanded = false
                                onAddNewSeason()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Search Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onSearchClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_hint),
                        tint = colors.textPrimary,
                        modifier = Modifier.size(21.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Settings Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onSettingsClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings_title),
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PeopleTopBar(
    onSortChange: (String) -> Unit,
    onAddPersonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = colors.appCard,
            shadowElevation = 3.dp,
            modifier = Modifier.height(44.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                // Sort Icon
                Box {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { sortMenuExpanded = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = stringResource(R.string.sort_by),
                            tint = colors.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.sort_name)) },
                            onClick = {
                                sortMenuExpanded = false
                                onSortChange("NAME")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.sort_budget)) },
                            onClick = {
                                sortMenuExpanded = false
                                onSortChange("BUDGET")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.sort_due_date)) },
                            onClick = {
                                sortMenuExpanded = false
                                onSortChange("DUE_DATE")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Add Person "+"
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onAddPersonClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.action_add_person),
                        tint = colors.textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MoneyTopBar(
    onFilterOrSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Sliders Button
        Surface(
            shape = CircleShape,
            color = colors.appCard,
            shadowElevation = 3.dp,
            modifier = Modifier.size(44.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFilterOrSettingsClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Budget Settings",
                    tint = colors.textPrimary,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@Composable
fun MessagesSegmentedChips(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // Chip 0: From Pip
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (selectedTabIndex == 0) colors.pipPrimaryButton else colors.appCard,
            shadowElevation = if (selectedTabIndex == 0) 2.dp else 1.dp,
            modifier = Modifier
                .height(40.dp)
                .clickable { onTabSelected(0) }
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.chip_from_pip),
                    color = if (selectedTabIndex == 0) Color.White else colors.textPrimary,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Chip 1: What you did
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (selectedTabIndex == 1) colors.pipPrimaryButton else colors.appCard,
            shadowElevation = if (selectedTabIndex == 1) 2.dp else 1.dp,
            modifier = Modifier
                .height(40.dp)
                .clickable { onTabSelected(1) }
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.chip_what_you_did),
                    color = if (selectedTabIndex == 1) Color.White else colors.textPrimary,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
