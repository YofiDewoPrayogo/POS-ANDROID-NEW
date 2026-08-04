import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "r") as f:
    content = f.read()

bad_generate = 'Text("Sistem Kasir", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)\n                Text("generate by Yofi Dewo Prayogo", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)'
good_generate = 'Text("Sistem Kasir", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)\n                Spacer(modifier = Modifier.height(8.dp))\n                Text("generate by Yofi Dewo Prayogo", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)'

content = content.replace(bad_generate, good_generate)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "w") as f:
    f.write(content)
