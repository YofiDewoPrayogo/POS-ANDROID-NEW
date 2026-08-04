import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "r") as f:
    content = f.read()

printer_content = """@Composable
fun PrinterSettingsContent() {
    val context = LocalContext.current
    var connectionType by remember { mutableStateOf("Bluetooth") }
    var paperSize by remember { mutableStateOf("58mm") }
    var headerText by remember { mutableStateOf("Toko POS Kasir") }
    var footerText by remember { mutableStateOf("Terima Kasih") }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Pengaturan Printer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Konfigurasi koneksi dan format struk printer termal", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Koneksi Printer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = connectionType == "Bluetooth", onClick = { connectionType = "Bluetooth" })
                        Text("Bluetooth")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = connectionType == "LAN", onClick = { connectionType = "LAN" })
                        Text("LAN (WiFi)")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    if (connectionType == "Bluetooth") {
                        Button(onClick = { Toast.makeText(context, "Mencari perangkat Bluetooth...", Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Bluetooth, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cari Printer Bluetooth")
                        }
                    } else {
                        var ipAddress by remember { mutableStateOf("") }
                        OutlinedTextField(value = ipAddress, onValueChange = { ipAddress = it }, label = { Text("IP Address Printer") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { Toast.makeText(context, "Menghubungkan ke LAN...", Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Hubungkan")
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pengaturan Struk (Receipt)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Ukuran Kertas", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = paperSize == "58mm", onClick = { paperSize = "58mm" })
                        Text("58mm")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = paperSize == "80mm", onClick = { paperSize = "80mm" })
                        Text("80mm")
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = headerText, onValueChange = { headerText = it }, label = { Text("Teks Header Struk") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = footerText, onValueChange = { footerText = it }, label = { Text("Teks Footer Struk") }, modifier = Modifier.fillMaxWidth())
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { Toast.makeText(context, "Mencetak tes struk...", Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Test Print")
                    }
                }
            }
        }
    }
}"""

content = re.sub(
    r"@Composable\nfun PrinterSettingsContent\(\) \{.*?(?=@Composable\nfun DatabaseActivationContent)",
    printer_content + "\n\n",
    content,
    flags=re.DOTALL
)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write(content)
