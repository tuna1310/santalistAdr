package com.santalist.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.santalist.app.ui.budget.BudgetScreen
import com.santalist.app.ui.budget.BudgetViewModel
import com.santalist.app.ui.people.PeopleScreen
import com.santalist.app.ui.people.PeopleViewModel
import com.santalist.app.ui.people.PersonDetailScreen
import com.santalist.app.ui.people.PersonDetailViewModel
import com.santalist.app.ui.theme.SantaListTheme
import com.santalist.app.ui.timeline.TimelineScreen
import com.santalist.app.ui.timeline.TimelineViewModel

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val navController = rememberNavController()
    val colors = SantaListTheme.colors

    val peopleViewModel: PeopleViewModel = viewModel()
    val timelineViewModel: TimelineViewModel = viewModel()
    val budgetViewModel: BudgetViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = colors.appCard,
                contentColor = colors.pineGreen
            ) {
                // Tab 0: Recipients
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        navController.navigate("people_list") {
                            popUpTo("people_list") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.People, contentDescription = "Recipients") },
                    label = {
                        Text(
                            text = "Recipients",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.pineGreen,
                        selectedTextColor = colors.pineGreen,
                        indicatorColor = colors.pineGreen.copy(alpha = 0.15f),
                        unselectedIconColor = colors.textSecondary,
                        unselectedTextColor = colors.textSecondary
                    )
                )

                // Tab 1: Timeline
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate("timeline") {
                            popUpTo("timeline") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.Schedule, contentDescription = "Timeline") },
                    label = {
                        Text(
                            text = "Timeline",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.pineGreen,
                        selectedTextColor = colors.pineGreen,
                        indicatorColor = colors.pineGreen.copy(alpha = 0.15f),
                        unselectedIconColor = colors.textSecondary,
                        unselectedTextColor = colors.textSecondary
                    )
                )

                // Tab 2: Budget
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        navController.navigate("budget") {
                            popUpTo("budget") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.PieChart, contentDescription = "Budget") },
                    label = {
                        Text(
                            text = "Budget",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.pineGreen,
                        selectedTextColor = colors.pineGreen,
                        indicatorColor = colors.pineGreen.copy(alpha = 0.15f),
                        unselectedIconColor = colors.textSecondary,
                        unselectedTextColor = colors.textSecondary
                    )
                )
            }
        },
        containerColor = colors.appBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "people_list",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("people_list") {
                PeopleScreen(
                    viewModel = peopleViewModel,
                    onNavigateToPerson = { personId ->
                        navController.navigate("person_detail/$personId")
                    }
                )
            }

            composable(
                route = "person_detail/{personId}",
                arguments = listOf(navArgument("personId") { type = NavType.StringType })
            ) { backStackEntry ->
                val personId = backStackEntry.arguments?.getString("personId") ?: ""
                val detailViewModel: PersonDetailViewModel = viewModel(
                    key = "detail_$personId",
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return PersonDetailViewModel(
                                application = peopleViewModel.getApplication(),
                                personId = personId
                            ) as T
                        }
                    }
                )
                PersonDetailScreen(
                    viewModel = detailViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("timeline") {
                TimelineScreen(viewModel = timelineViewModel)
            }

            composable("budget") {
                BudgetScreen(viewModel = budgetViewModel)
            }
        }
    }
}
