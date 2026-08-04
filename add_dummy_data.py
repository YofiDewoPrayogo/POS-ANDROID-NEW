import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "r") as f:
    content = f.read()

hint_card = """                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Contoh Akun Login:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Admin: admin@pos.com (PIN: 1234)", style = MaterialTheme.typography.bodySmall)
                        Text("Kasir: kasir@pos.com (PIN: 1111)", style = MaterialTheme.typography.bodySmall)
                    }
                }"""

content = content.replace('                    TextButton(onClick = { Toast.makeText(context, "Daftar di menu manajemen pengguna", Toast.LENGTH_SHORT).show() }) {\n                        Text("Register", fontSize = 12.sp)\n                    }\n                }', '                    TextButton(onClick = { Toast.makeText(context, "Daftar di menu manajemen pengguna", Toast.LENGTH_SHORT).show() }) {\n                        Text("Register", fontSize = 12.sp)\n                    }\n                }\n' + hint_card)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/LoginScreen.kt", "w") as f:
    f.write(content)
