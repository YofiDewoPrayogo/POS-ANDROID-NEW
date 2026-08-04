import re

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.material.icons.Icons", "import androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.filled.Dashboard")

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "w") as f:
    f.write(content)
