import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/DashboardScreen.kt", "r") as f:
    content = f.read()

# Replace Screen.Settings.route with Screen.CategoriesBrands.route for the Kategori button
content = content.replace('onClick = { onNavigate(Screen.Settings.route) }', 'onClick = { onNavigate(Screen.CategoriesBrands.route) }')

with open("app/src/main/java/com/yofidewo/pos/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(content)

