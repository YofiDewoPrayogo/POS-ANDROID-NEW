import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "r") as f:
    content = f.read()

# Replace "Warung POS" with "WarungKu"
content = content.replace('"Warung POS"', '"WarungKu"')

# Replace "Sistem Kasir Pintar" with "Sistem Kasir"
content = content.replace('"Sistem Kasir Pintar"', '"Sistem Kasir"')

# Add generate by Yofi Dewo Prayogo below the login button
generate_text = """                Spacer(modifier = Modifier.height(16.dp))
                Text("generate by Yofi Dewo Prayogo", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)"""

content = content.replace('                    TextButton(onClick = { Toast.makeText(context, "Daftar di menu manajemen pengguna", Toast.LENGTH_SHORT).show() }) {', generate_text + '\n                    TextButton(onClick = { Toast.makeText(context, "Daftar di menu manajemen pengguna", Toast.LENGTH_SHORT).show() }) {')

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "w") as f:
    f.write(content)

