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
    val textSecondary: Color
)

val LocalSantaColors = staticCompositionLocalOf {
    SantaColors(
        pineGreen = PineGreenLight,
        cranberryRed = CranberryRedLight,
        warmGold = WarmGoldLight,
        statusOrange = StatusOrangeLight,
        appBackground = AppBackgroundLight,
        appCard = AppCardLight,
        textPrimary = TextPrimaryLight,
        textSecondary = TextSecondaryLight
    )
}

private val LightSantaColors = SantaColors(
    pineGreen = PineGreenLight,
    cranberryRed = CranberryRedLight,
    warmGold = WarmGoldLight,
    statusOrange = StatusOrangeLight,
    appBackground = AppBackgroundLight,
    appCard = AppCardLight,
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight
)

private val DarkSantaColors = SantaColors(
    pineGreen = PineGreenDark,
    cranberryRed = CranberryRedDark,
    warmGold = WarmGoldDark,
    statusOrange = StatusOrangeDark,
    appBackground = AppBackgroundDark,
    appCard = AppCardDark,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = PineGreenLight,
    onPrimary = Color.White,
    primaryContainer = PineGreenLight.copy(alpha = 0.12f),
    onPrimaryContainer = PineGreenLight,
    secondary = WarmGoldLight,
    background = AppBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = AppCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = AppBackgroundLight,
    onSurfaceVariant = TextSecondaryLight,
    error = CranberryRedLight,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PineGreenDark,
    onPrimary = Color.Black,
    primaryContainer = PineGreenDark.copy(alpha = 0.2f),
    onPrimaryContainer = PineGreenDark,
    secondary = WarmGoldDark,
    background = AppBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = AppCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = AppBackgroundDark,
    onSurfaceVariant = TextSecondaryDark,
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
