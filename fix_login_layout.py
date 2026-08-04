import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "r") as f:
    content = f.read()

# Remove the incorrectly placed text
bad_text = """                Spacer(modifier = Modifier.height(16.dp))
                Text("generate by Yofi Dewo Prayogo", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = { Toast.makeText(context, "Daftar di menu manajemen pengguna", Toast.LENGTH_SHORT).show() }) {"""
content = content.replace(bad_text, '                    TextButton(onClick = { Toast.makeText(context, "Daftar di menu manajemen pengguna", Toast.LENGTH_SHORT).show() }) {')

# Add it below Sistem Kasir
sistem_kasir = 'Text("Sistem Kasir", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)'
generate_by = 'Text("Sistem Kasir", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)\n                Text("generate by Yofi Dewo Prayogo", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)'
content = content.replace(sistem_kasir, generate_by)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "w") as f:
    f.write(content)
