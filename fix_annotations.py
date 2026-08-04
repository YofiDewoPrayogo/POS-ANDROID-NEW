import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CashierPosScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "@OptIn(ExperimentalMaterial3Api::class)\n@OptIn(ExperimentalMaterial3Api::class)\n@OptIn(ExperimentalMaterial3Api::class)\n@Composable",
    "@OptIn(ExperimentalMaterial3Api::class)\n@Composable"
)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CashierPosScreen.kt", "w") as f:
    f.write(content)

