import re

with open("app/src/main/java/com/yofidewo/pos/ui/theme/Theme.kt", "r") as f:
    content = f.read()

content = content.replace("darkTheme: Boolean = isSystemInDarkTheme()", "darkTheme: Boolean = false")

with open("app/src/main/java/com/yofidewo/pos/ui/theme/Theme.kt", "w") as f:
    f.write(content)

