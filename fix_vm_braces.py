with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    content = f.read()

# I appended at the very end. Let's fix this.
# I will remove the original last '}' and my added code, then re-insert properly.
import re
# Find the last two closing braces and fix them.
# A better way is to just find the newly appended block and insert it before the class closing brace.
content = content.replace("}\n\n    val customers = repository.customers", "\n    val customers = repository.customers")
with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(content)
