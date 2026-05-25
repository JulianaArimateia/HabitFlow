package com.habitflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary            = PrimaryDarkTheme,
    onPrimary          = OnPrimaryDarkTheme,
    primaryContainer   = PrimaryLightDarkTheme,
    onPrimaryContainer = PrimaryDarkDarkTheme,
    secondary          = SecondaryDarkTheme,
    background         = BackgroundDarkTheme,
    surface            = SurfaceDarkTheme,
    onBackground       = OnBackgroundDarkTheme,
    onSurface          = OnSurfaceDarkTheme,
    error              = ErrorDarkTheme,
    surfaceVariant     = SurfaceDarkTheme,
    onSurfaceVariant   = OnSurfaceVarDarkTheme,
)

private val LightColorScheme = lightColorScheme(
    primary            = Primary,
    onPrimary          = OnPrimary,
    primaryContainer   = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary          = Secondary,
    background         = Background,
    surface            = Surface,
    onBackground       = OnBackground,
    onSurface          = OnSurface,
    error              = Error,
    surfaceVariant     = Background,
    onSurfaceVariant   = OnSurfaceVar,
)

@Composable
fun HabitFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
