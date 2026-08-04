import re

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("var isLoggedIn by remember { mutableStateOf(false) }", "val currentUser by viewModel.currentUser.collectAsState()")
content = content.replace("if (isLoggedIn) {", "if (currentUser != null) {")
content = content.replace("LoginScreen(viewModel = viewModel, onLoginSuccess = { isLoggedIn = true })", "LoginScreen(viewModel = viewModel, onLoginSuccess = { /* Automatically handled by state */ })")

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "w") as f:
    f.write(content)

