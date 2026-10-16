package com.santalist.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class SantaColors(
    val pineGreen: Color,
    val cranberryRed: Color,
    val warmGold: Color,
    val statusOrange: Color,
    val appBackground: Color,
    val appCard: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val pipPrimaryButton: Color = PipPrimaryButtonLight,
    val pipBubbleBg: Color = PipBubbleBgLight,
    val pipNavSelectedChip: Color = PipNavSelectedChipLight
)

val LocalSantaColors = staticCompositionLocalOf {
    SantaColors(
        pineGreen = PineGreenLight,
        cranberryRed = CranberryRedLight,
        warmGold = WarmGoldLight,
        statusOrange = StatusOrangeLight,
        appBackground = PipWarmBackgroundLight,
        appCard = PipCardLight,
        textPrimary = PipTextPrimaryLight,
        textSecondary = PipTextSecondaryLight,
        pipPrimaryButton = PipPrimaryButtonLight,
        pipBubbleBg = PipBubbleBgLight,
        pipNavSelectedChip = PipNavSelectedChipLight
    )
}

private val LightSantaColors = SantaColors(
    pineGreen = PineGreenLight,
    cranberryRed = CranberryRedLight,
    warmGold = WarmGoldLight,
    statusOrange = StatusOrangeLight,
    appBackground = PipWarmBackgroundLight,
    appCard = PipCardLight,
    textPrimary = PipTextPrimaryLight,
    textSecondary = PipTextSecondaryLight,
    pipPrimaryButton = PipPrimaryButtonLight,
    pipBubbleBg = PipBubbleBgLight,
    pipNavSelectedChip = PipNavSelectedChipLight
)

private val DarkSantaColors = SantaColors(
    pineGreen = PineGreenDark,
    cranberryRed = CranberryRedDark,
    warmGold = WarmGoldDark,
    statusOrange = StatusOrangeDark,
    appBackground = PipWarmBackgroundDark,
    appCard = PipCardDark,
    textPrimary = PipTextPrimaryDark,
    textSecondary = PipTextSecondaryDark,
    pipPrimaryButton = PipPrimaryButtonDark,
    pipBubbleBg = PipBubbleBgDark,
    pipNavSelectedChip = PipNavSelectedChipDark
)

private val LightColorScheme = lightColorScheme(
    primary = PipPrimaryButtonLight,
    onPrimary = Color.White,
    primaryContainer = PipNavSelectedChipLight,
    onPrimaryContainer = PipTextPrimaryLight,
    secondary = WarmGoldLight,
    background = PipWarmBackgroundLight,
    onBackground = PipTextPrimaryLight,
    surface = PipCardLight,
    onSurface = PipTextPrimaryLight,
    surfaceVariant = PipWarmBackgroundLight,
    onSurfaceVariant = PipTextSecondaryLight,
    error = CranberryRedLight,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PipPrimaryButtonDark,
    onPrimary = PipTextPrimaryDark,
    primaryContainer = PipNavSelectedChipDark,
    onPrimaryContainer = PipTextPrimaryDark,
    secondary = WarmGoldDark,
    background = PipWarmBackgroundDark,
    onBackground = PipTextPrimaryDark,
    surface = PipCardDark,
    onSurface = PipTextPrimaryDark,
    surfaceVariant = PipWarmBackgroundDark,
    onSurfaceVariant = PipTextSecondaryDark,
    error = CranberryRedDark,
    onError = Color.Black
)

object SantaListTheme {
    val colors: SantaColors
        @Composable
        get() = LocalSantaColors.current
}

@Composable
fun SantaListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val santaColors = if (darkTheme) DarkSantaColors else LightSantaColors

    CompositionLocalProvider(LocalSantaColors provides santaColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
