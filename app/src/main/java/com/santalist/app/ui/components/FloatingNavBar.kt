package com.santalist.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santalist.app.R
import com.santalist.app.ui.theme.PipCardLight
import com.santalist.app.ui.theme.PipNavSelectedChipLight
import com.santalist.app.ui.theme.PipTextPrimaryLight
import com.santalist.app.ui.theme.PipTextSecondaryLight
import com.santalist.app.ui.theme.SantaListTheme

enum class NavTab(val titleRes: Int, val icon: ImageVector) {
    HOME(R.string.nav_home, Icons.Default.CardGiftcard),
    PEOPLE(R.string.nav_people, Icons.Default.People),
    MONEY(R.string.nav_money, Icons.Default.PieChart),
    MESSAGES(R.string.nav_messages, Icons.AutoMirrored.Filled.Chat)
}

@Composable
fun FloatingNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SantaListTheme.colors

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(36.dp),
                spotColor = Color(0x332B2420),
                ambientColor = Color(0x1A2B2420)
            ),
        shape = RoundedCornerShape(36.dp),
        color = colors.appCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val label = stringResource(id = tab.titleRes)

                val pillBgColor by animateColorAsState(
                    targetValue = if (isSelected) colors.pipNavSelectedChip else Color.Transparent,
                    animationSpec = tween(durationMillis = 200),
                    label = "pill_bg"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) colors.textPrimary else colors.textSecondary,
                    animationSpec = tween(durationMillis = 200),
                    label = "nav_content_color"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .padding(horizontal = 4.dp)
                        .background(pillBgColor, shape = RoundedCornerShape(27.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = label,
                            tint = contentColor,
                            modifier = Modifier.size(23.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            color = contentColor,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
