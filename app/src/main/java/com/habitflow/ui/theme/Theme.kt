package com.habitflow.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

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
fun HabitFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography,
        content     = content
    )
}
