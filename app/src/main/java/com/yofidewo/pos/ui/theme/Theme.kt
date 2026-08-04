package com.yofidewo.pos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF6600), // Orange from logo/reference
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0D0),
    onPrimaryContainer = Color(0xFF4D1F00),
    secondary = Color(0xFF1E2B4D), // Dark Blue from reference
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7E2FF),
    onSecondaryContainer = Color(0xFF001A40),
    tertiary = Color(0xFF2563EB), // Blue from abacus
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC), 
    onBackground = Color(0xFF0F172A), 
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9), // Slate 100
    onSurfaceVariant = Color(0xFF475569) // Slate 600
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8833),
    onPrimary = Color(0xFF4C2700),
    primaryContainer = Color(0xFF703800),
    onPrimaryContainer = Color(0xFFFFDBCB),
    secondary = Color(0xFFAAC7FF),
    onSecondary = Color(0xFF00295F),
    secondaryContainer = Color(0xFF003E8A),
    onSecondaryContainer = Color(0xFFD7E2FF),
    background = Color(0xFF0F172A), 
    onBackground = Color(0xFFF8FAFC), 
    surface = Color(0xFF1E293B), 
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155), 
    onSurfaceVariant = Color(0xFFCBD5E1) 
)

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun PosTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        shapes = Shapes,
        content = content
    )
}
