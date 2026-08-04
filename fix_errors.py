import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CategoriesBrandsScreen.kt", "r") as f:
    cat_content = f.read()
if "import com.yofidewo.pos.data.CategoryEntity" not in cat_content:
    cat_content = cat_content.replace("import com.yofidewo.pos.ui.PosViewModel", "import com.yofidewo.pos.ui.PosViewModel\nimport com.yofidewo.pos.data.CategoryEntity")
with open("app/src/main/java/com/yofidewo/pos/ui/screens/CategoriesBrandsScreen.kt", "w") as f:
    f.write(cat_content)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/DashboardScreen.kt", "r") as f:
    dash_content = f.read()

# Fix imports
dash_content = "import androidx.compose.runtime.remember\nimport androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.setValue\nimport androidx.compose.foundation.lazy.LazyRow\nimport androidx.compose.foundation.lazy.items\nimport androidx.compose.material3.FilterChip\nimport java.time.LocalDate\nimport androidx.compose.material.icons.automirrored.filled.TrendingUp\n" + dash_content

# Fix tx.date to tx.timestamp
dash_content = dash_content.replace("tx.date", "tx.timestamp")

with open("app/src/main/java/com/yofidewo/pos/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(dash_content)

