import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "r") as f:
    content = f.read()

activation_content = """@Composable
fun DatabaseActivationContent(viewModel: PosViewModel) {
    val context = LocalContext.current
    var licenseKey by remember { mutableStateOf("") }
    var useOnlineDb by remember { mutableStateOf(false) }
    val isActivated by viewModel.isActivated.collectAsState()
    var showCodes by remember { mutableStateOf(false) }

    val validCodes = listOf(
        "POS-2026-X7Y8-Z9A1", "POS-2026-B2C3-D4E5", "POS-2026-F6G7-H8I9",
        "POS-2026-J0K1-L2M3", "POS-2026-N4O5-P6Q7", "POS-2026-R8S9-T0U1",
        "POS-2026-V2W3-X4Y5", "POS-2026-Z6A7-B8C9", "POS-2026-D0E1-F2G3",
        "POS-2026-PRO"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Database & Aktivasi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Kelola mode penyimpanan dan lisensi", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(24.dp))

        // Activation Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Aktivasi Lisensi Offline", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (isActivated) {
                    Text("Status: AKTIF (Premium)", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Aplikasi sudah diaktivasi. Anda dapat menggunakan semua fitur tanpa batas.", fontSize = 12.sp)
                } else {
                    Text("Status: TRIAL", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    Text("Masukkan kunci aktivasi untuk menggunakan aplikasi secara penuh.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = licenseKey,
                        onValueChange = { licenseKey = it },
                        label = { Text("Kunci Aktivasi") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(onClick = {
                            val success = validCodes.contains(licenseKey)
                            if (success) {
                                viewModel.activateLicense(licenseKey)
                                Toast.makeText(context, "Aktivasi Berhasil!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Kunci Aktivasi Salah!", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text("Aktivasi")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = { showCodes = !showCodes }) {
                            Text(if (showCodes) "Sembunyikan Kode" else "Lihat Kode")
                        }
                    }
                    if (showCodes) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Daftar Kode Valid:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        validCodes.forEach { code ->
                            Text(code, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cloud Sync Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sinkronisasi Cloud", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text("Gunakan database online agar data bisa diakses dari berbagai perangkat secara real-time.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = useOnlineDb, onCheckedChange = { useOnlineDb = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gunakan Database Online (Membutuhkan Koneksi Internet)")
                }
                if (useOnlineDb) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { Toast.makeText(context, "Menghubungkan ke Cloud...", Toast.LENGTH_SHORT).show() }) {
                        Text("Hubungkan Sekarang")
                    }
                }
            }
        }
    }
}"""

content = re.sub(
    r"@Composable\nfun DatabaseActivationContent.*?fun AddCurrencyDialog",
    activation_content + "\n\n@Composable\nfun AddCurrencyDialog",
    content,
    flags=re.DOTALL
)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write(content)
