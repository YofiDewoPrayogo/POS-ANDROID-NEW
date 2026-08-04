import re

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "r") as f:
    content = f.read()

nav_bar_old = "NavigationBar(tonalElevation = 8.dp)"
nav_bar_new = "NavigationBar(tonalElevation = 8.dp, containerColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary, contentColor = androidx.compose.ui.graphics.Color.White)"

content = content.replace(nav_bar_old, nav_bar_new)

# Also fix the unselected icon color
item_old = """                    NavigationBarItem(
                        selected = selected,"""
item_new = """                    NavigationBarItem(
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            unselectedIconColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                            indicatorColor = androidx.compose.ui.graphics.Color.White
                        ),
                        selected = selected,"""
content = content.replace(item_old, item_new)

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "w") as f:
    f.write(content)

