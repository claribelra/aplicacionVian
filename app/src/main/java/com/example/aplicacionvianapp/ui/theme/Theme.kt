package com.example.aplicacionvianapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de colores para el modo oscuro (personalizada según el diseño)
private val DarkColorScheme = darkColorScheme(
    primary = VianappBlue,
    secondary = Green,
    background = Black,
    surface = Black, // En el diseño, el fondo de las tarjetas también es negro.
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White,
    onSurfaceVariant = LightGray,
    outline = CardOutline,
    primaryContainer = VianappBlue, // Contenedor de íconos
    surfaceVariant = Color(0xFF1A1A1A) // Un gris muy oscuro para variantes de superficie
)

// Paleta de colores para el modo claro (mantenemos la que tenías)
private val LightColorScheme = lightColorScheme(
    primary = Green,
    secondary = Green,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun AplicacionvianappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Aseguramos que el color de la barra de estado coincida con el fondo
            window.statusBarColor = colorScheme.background.toArgb()
            // Los iconos de la barra de estado deben ser claros en el tema oscuro
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
