import re

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace(
    "ProductsScreen(viewModel = viewModel)",
    "ProductsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })"
)

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "w") as f:
    f.write(content)
