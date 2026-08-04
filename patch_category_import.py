import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CategoriesBrandsScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "import androidx.compose.material.icons.filled.Delete",
    "import androidx.compose.material.icons.filled.Delete\nimport androidx.compose.material.icons.filled.Edit"
)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CategoriesBrandsScreen.kt", "w") as f:
    f.write(content)
