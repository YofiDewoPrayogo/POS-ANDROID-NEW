import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "r") as f:
    content = f.read()

old_sync = """                    item {
                        SettingsItem(
                            icon = Icons.Default.CloudSync,
                            title = "Sinkronisasi Cloud",
                            subtitle = "Sinkronisasi data (Mock)",
                            onClick = {}
                        )
                    }"""

new_sync = """                    item {
                        SettingsItem(
                            icon = Icons.Default.CloudSync,
                            title = "Sinkronisasi Cloud",
                            subtitle = "Sinkronisasi data (Mock)",
                            onClick = {}
                        )
                    }
                    item {
                        var showTrialDialog by remember { mutableStateOf(false) }
                        SettingsItem(
                            icon = Icons.Default.VpnKey,
                            title = "Aktivasi Lisensi / Trial",
                            subtitle = "Masukkan kode aktivasi",
                            onClick = { showTrialDialog = true }
                        )
                        if (showTrialDialog) {
                            var activationCode by remember { mutableStateOf("") }
                            Dialog(onDismissRequest = { showTrialDialog = false }) {
                                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
                                    Column(modifier = Modifier.padding(24.dp)) {
                                        Text("Aktivasi Lisensi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedTextField(
                                            value = activationCode,
                                            onValueChange = { activationCode = it },
                                            label = { Text("Kode Aktivasi") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                            TextButton(onClick = { showTrialDialog = false }) { Text("Batal") }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(onClick = { 
                                                Toast.makeText(context, "Kode lisensi tidak valid", Toast.LENGTH_SHORT).show()
                                                showTrialDialog = false 
                                            }) { Text("Aktivasi") }
                                        }
                                    }
                                }
                            }
                        }
                    }"""

content = content.replace(old_sync, new_sync)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write(content)

