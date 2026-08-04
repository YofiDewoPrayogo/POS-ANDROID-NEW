import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "r") as f:
    content = f.read()

printer_content = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterSettingsContent(viewModel: PosViewModel, onBack: () -> Unit = {}) {
    var isSearching by remember { mutableStateOf(false) }
    var connectedPrinter by remember { mutableStateOf<String?>(null) }
    val mockPrinters = listOf("Thermal Printer POS-58", "Epson TM-T82II", "BlueTooth Printer 80mm", "POS-80C", "Zjiang ZJ-5802")
    var foundPrinters by remember { mutableStateOf(emptyList<String>()) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Printer", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)) {
            Text("Pengaturan Printer Kasir", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (connectedPrinter != null) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Terhubung ke:", fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                            Text(connectedPrinter!!, fontSize = 18.sp, color = Color(0xFF065F46))
                        }
                        Button(onClick = { 
                            connectedPrinter = null 
                            Toast.makeText(context, "Printer diputuskan", Toast.LENGTH_SHORT).show()
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))) {
                            Text("Putuskan")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { Toast.makeText(context, "Mencetak struk tes...", Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Print, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cetak Struk Tes (Test Print)")
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text("Belum ada printer yang terhubung", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { 
                            isSearching = true
                            foundPrinters = mockPrinters.shuffled().take(3)
                        }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.BluetoothSearch, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isSearching) "Cari Ulang Printer Bluetooth" else "Cari Printer Bluetooth")
                        }
                    }
                }
            }
            
            if (isSearching && connectedPrinter == null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Perangkat Ditemukan:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(foundPrinters) { printerName ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                                connectedPrinter = printerName
                                isSearching = false
                                Toast.makeText(context, "Terhubung ke $printerName", Toast.LENGTH_SHORT).show()
                            },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Print, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(printerName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}
"""

content = re.sub(r"fun PrinterSettingsContent.*?\}\n\}", printer_content, content, flags=re.DOTALL)
with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write(content)

