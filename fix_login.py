import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "viewModel.login(user)\n                            onLoginSuccess()",
    "viewModel.login(email, pin, onSuccess = { onLoginSuccess() }, onError = { Toast.makeText(context, \"Login failed\", Toast.LENGTH_SHORT).show() })"
)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "w") as f:
    f.write(content)

