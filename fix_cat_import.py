import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CategoriesBrandsScreen.kt", "r") as f:
    content = f.read()

content = content.replace("package com.yofidewo.pos.ui.screens", "package com.yofidewo.pos.ui.screens\n\nimport com.yofidewo.pos.data.CategoryEntity\n")

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CategoriesBrandsScreen.kt", "w") as f:
    f.write(content)
