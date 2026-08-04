import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    """            Tab(
                selected = selectedSubTab == 4,
                onClick = { selectedSubTab = 4 },
                text = { Text("Database & Aktivasi", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Key, contentDescription = null) }
            )
        }""",
    """            Tab(
                selected = selectedSubTab == 4,
                onClick = { selectedSubTab = 4 },
                text = { Text("Database & Aktivasi", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Key, contentDescription = null) }
            )
            Tab(
                selected = selectedSubTab == 5,
                onClick = { selectedSubTab = 5 },
                text = { Text("Pembayaran", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Money, contentDescription = null) }
            )
        }"""
)

content = content.replace(
    """                3 -> PrinterSettingsContent()
                4 -> DatabaseActivationContent(viewModel)
            }""",
    """                3 -> PrinterSettingsContent()
                4 -> DatabaseActivationContent(viewModel)
                5 -> PaymentMethodsContent()
            }"""
)

payment_content = """
@Composable
fun PaymentMethodsContent() {
    val context = LocalContext.current
    val paymentMethods = listOf("Tunai (Cash)", "Kartu Debit/Kredit", "QRIS", "Transfer Bank")
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Metode Pembayaran", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Kelola jenis metode pembayaran untuk kasir", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = { Toast.makeText(context, "Fitur Tambah Pembayaran belum tersedia", Toast.LENGTH_SHORT).show() }) {
                Text("Tambah Baru")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(paymentMethods) { method ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(method, fontWeight = FontWeight.Bold)
                        Row {
                            IconButton(onClick = { Toast.makeText(context, "Edit $method", Toast.LENGTH_SHORT).show() }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { Toast.makeText(context, "Hapus $method", Toast.LENGTH_SHORT).show() }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
"""

content += payment_content

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write(content)
