import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "r") as f:
    content = f.read()

content = content.replace("    var currentSubScreen by remember { mutableStateOf(\"list\") }", "    val context = LocalContext.current\n    var currentSubScreen by remember { mutableStateOf(\"list\") }")

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write(content)

