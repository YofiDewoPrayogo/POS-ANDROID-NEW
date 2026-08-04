import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ReceivingNotesScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.graphics.Color", "import androidx.compose.ui.graphics.Color\nimport androidx.compose.foundation.background")

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ReceivingNotesScreen.kt", "w") as f:
    f.write(content)

