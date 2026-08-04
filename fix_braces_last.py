import re

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    content = f.read()

content = re.sub(r'\}\s*\}\s*\}\s*\}\s*val customers', '    }\n    val customers', content)
content = re.sub(r'\}\s*\}\s*val customers', '    }\n    val customers', content)

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(content)
