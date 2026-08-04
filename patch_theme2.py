import re

with open("app/src/main/java/com/yofidewo/pos/ui/theme/Theme.kt", "r") as f:
    content = f.read()

# Define Orange/Jingga and Navy colors
colors_def = """import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFFFF8F00) // Amber 800 - Orange Jingga
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFFFD18C)
val md_theme_light_onPrimaryContainer = Color(0xFF421C00)

val md_theme_light_secondary = Color(0xFF001B3A) // Navy Blue
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFD4E3FF)
val md_theme_light_onSecondaryContainer = Color(0xFF001B3A)

val md_theme_light_tertiary = Color(0xFF0D47A1) // Navy Blue variation
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFD4E3FF)
val md_theme_light_onTertiaryContainer = Color(0xFF001C3A)

val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)

val md_theme_light_background = Color(0xFFFAFCFF)
val md_theme_light_onBackground = Color(0xFF001F2A)
val md_theme_light_surface = Color(0xFFFAFCFF)
val md_theme_light_onSurface = Color(0xFF001F2A)
val md_theme_light_surfaceVariant = Color(0xFFEBE0D3)
val md_theme_light_onSurfaceVariant = Color(0xFF4E4539)
val md_theme_light_outline = Color(0xFF807567)

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB86C),
    onPrimary = Color(0xFF4D2700),
    primaryContainer = Color(0xFF6E3900),
    onPrimaryContainer = Color(0xFFFFDCC0),
    secondary = Color(0xFFA5C8FF),
    onSecondary = Color(0xFF00315F),
    secondaryContainer = Color(0xFF004786),
    onSecondaryContainer = Color(0xFFD4E3FF),
    background = Color(0xFF001F2A),
    onBackground = Color(0xFFBFE9FF),
    surface = Color(0xFF001F2A),
    onSurface = Color(0xFFBFE9FF),
    surfaceVariant = Color(0xFF4E4539),
    onSurfaceVariant = Color(0xFFD2C4B4)
)
"""

content = re.sub(r'private val LightColors[\s\S]*?onSurfaceVariant = Color\(0xFFD2C4B4\)\n\)', colors_def, content)

with open("app/src/main/java/com/yofidewo/pos/ui/theme/Theme.kt", "w") as f:
    f.write(content)

