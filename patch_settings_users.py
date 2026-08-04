import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "r") as f:
    content = f.read()

new_main = """
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsUsersScreen(viewModel: PosViewModel) {
    var currentSubScreen by remember { mutableStateOf("list") }
    
    when (currentSubScreen) {
        "list" -> {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Pengaturan", fontWeight = FontWeight.Bold) })
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        SettingsItem(
                            icon = Icons.Default.ManageAccounts,
                            title = "Kelola Kasir / Pengguna",
                            subtitle = "Tambah atau hapus akun pengguna",
                            onClick = { currentSubScreen = "users" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Discount,
                            title = "Kelola Diskon",
                            subtitle = "Atur jenis dan nilai diskon",
                            onClick = { currentSubScreen = "discounts" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Print,
                            title = "Kelola Printer",
                            subtitle = "Atur printer bluetooth & struk",
                            onClick = { currentSubScreen = "printer" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.CloudSync,
                            title = "Sinkronisasi Cloud",
                            subtitle = "Sinkronisasi data (Mock)",
                            onClick = {}
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Keluar (Logout)")
                        }
                    }
                }
            }
        }
        "users" -> UsersManagementContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
        "printer" -> PrinterSettingsContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
        "discounts" -> DiscountsManagementContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
    }
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ... original UsersManagementContent logic
"""

# Replace the original SettingsUsersScreen with our new one
content = re.sub(r'@OptIn\(ExperimentalMaterial3Api::class\)\n@Composable\nfun SettingsUsersScreen\([\s\S]*?\n\}\n\n@OptIn', new_main.strip() + "\n\n@OptIn", content, count=1)

# Modify UsersManagementContent to add back button and hide PIN
content = content.replace("fun UsersManagementContent(viewModel: PosViewModel)", "fun UsersManagementContent(viewModel: PosViewModel, onBack: () -> Unit = {})")

top_bar_users = """
        TopAppBar(
            title = { Text("Kelola Pengguna", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
            }
        )
"""
content = re.sub(r'TopAppBar\(\s*title = \{ Text\("Pengaturan.*?"\).*?\)', top_bar_users.strip(), content)
content = content.replace('Text(text = "PIN: ${user.pin}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)', 'Text(text = "PIN: ****", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)')

# Also need to make PrinterSettingsContent support onBack
content = content.replace("fun PrinterSettingsContent(viewModel: PosViewModel)", "fun PrinterSettingsContent(viewModel: PosViewModel, onBack: () -> Unit = {})")
content = re.sub(r'Text\(\s*text = "Printer.*?",\s*style = MaterialTheme\.typography\.titleLarge[\s\S]*?\n\s*\)', r"""Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                        Text(text = "Pengaturan Printer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }""", content, count=1)


with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write(content)

