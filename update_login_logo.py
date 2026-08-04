import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "r") as f:
    content = f.read()

# Add import
content = content.replace("import com.yofidewo.pos.ui.PosViewModel", "import com.yofidewo.pos.ui.PosViewModel\nimport com.yofidewo.pos.ui.components.CustomLogo")

old_logo_box = """                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = "Logo",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }"""

new_logo_box = """                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CustomLogo(modifier = Modifier.fillMaxSize())
                }"""

content = content.replace(old_logo_box, new_logo_box)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "w") as f:
    f.write(content)
