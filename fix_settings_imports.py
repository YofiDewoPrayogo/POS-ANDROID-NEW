import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.material.icons.filled.CloudSync", "import androidx.compose.material.icons.filled.CloudSync\nimport androidx.compose.material.icons.filled.VpnKey\nimport android.widget.Toast\nimport androidx.compose.ui.platform.LocalContext")

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write(content)

