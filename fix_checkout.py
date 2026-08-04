import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CashierPosScreen.kt", "r") as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if line.strip() == "fun CheckoutDialog(":
        start_idx = i - 1 # Include @Composable
        break

new_content = "".join(lines[:start_idx]) + """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutDialog(
    viewModel: PosViewModel,
    totalUsd: Double,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val curr by viewModel.selectedCurrency.collectAsState()
    val totalInCurr = totalUsd * curr.exchangeRate

    var customerName by remember { mutableStateOf("") }
    var paidAmountInput by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Tunai (Cash)") }
    var notes by remember { mutableStateOf("") }
    var isPiutang by remember { mutableStateOf(false) }

    val paidDouble = if (isPiutang) 0.0 else (paidAmountInput.toDoubleOrNull() ?: 0.0)
    val changeDouble = if (isPiutang) 0.0 else (paidDouble - totalInCurr).coerceAtLeast(0.0)
    
    val quickAmounts = listOf(
        totalInCurr, 
        ((totalInCurr / 10000).toInt() + 1) * 10000.0,
        50000.0, 100000.0
    ).filter { it >= totalInCurr }.distinct().sorted()

    Dialog(onDismissRequest = onDismiss, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Sistem Pembayaran", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Left Column (Total & Payment Input)
                    Column(modifier = Modifier.weight(1.2f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(16.dp)
                        ) {
                            Column {
                                Text("Total Tagihan", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(
                                    text = viewModel.formatMoney(totalUsd),
                                    fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Metode Pembayaran", fontWeight = FontWeight.SemiBold)
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Tunai (Cash)", "QRIS / Transfer", "Piutang").forEach { method ->
                                val isSelected = if (method == "Piutang") isPiutang else (!isPiutang && paymentMethod == method)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    onClick = {
                                        if (method == "Piutang") {
                                            isPiutang = true
                                            paymentMethod = "Piutang"
                                            paidAmountInput = "0"
                                        } else {
                                            isPiutang = false
                                            paymentMethod = method
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) { 
                                        Text(method, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center) 
                                    }
                                }
                            }
                        }
                        
                        if (!isPiutang && paymentMethod == "Tunai (Cash)") {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Uang Diterima", fontWeight = FontWeight.SemiBold)
                            OutlinedTextField(
                                value = paidAmountInput,
                                onValueChange = { paidAmountInput = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                quickAmounts.take(4).forEach { amount ->
                                    OutlinedButton(onClick = { 
                                        paidAmountInput = if (curr.code == "IDR") String.format(java.util.Locale("id", "ID"), "%.0f", amount) else String.format(java.util.Locale.US, "%.2f", amount)
                                    }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(0.dp)) {
                                        Text(if (amount == totalInCurr) "Uang Pas" else viewModel.formatMoney(amount / curr.exchangeRate), fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isPiutang) Color(0xFFFFF3E0) else if (paidDouble >= totalInCurr) Color(0xFFD1FAE5) else Color(0xFFFEE2E2))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isPiutang) "Status:" else "Kembalian:", fontWeight = FontWeight.Bold)
                            Text(
                                text = if (isPiutang) "Belum Lunas (Piutang)" else if (paidDouble >= totalInCurr) viewModel.formatMoney(changeDouble / curr.exchangeRate) else "Uang Kurang",
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isPiutang) Color(0xFFE65100) else if (paidDouble >= totalInCurr) Color(0xFF065F46) else Color(0xFF991B1B)
                            )
                        }
                    }
                    
                    // Right Column (Details & Action)
                    Column(modifier = Modifier.weight(0.8f)) {
                        Text("Info Pelanggan & Catatan", fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = customerName, onValueChange = { customerName = it },
                            label = { Text("Nama Pelanggan (Wajib utk Piutang)") },
                            singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                        OutlinedTextField(
                            value = notes, onValueChange = { notes = it },
                            label = { Text("Catatan Transaksi") },
                            modifier = Modifier.fillMaxWidth().height(100.dp).padding(top = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal") }
                            Button(
                                onClick = {
                                    if (!isPiutang && paidDouble < totalInCurr) {
                                        Toast.makeText(context, "Uang pembayaran kurang!", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (isPiutang && customerName.isBlank()) {
                                        Toast.makeText(context, "Nama pelanggan wajib diisi untuk piutang", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    viewModel.processCheckout(
                                        customerName = customerName.ifBlank { "Guest" },
                                        paymentMethod = paymentMethod,
                                        paidAmount = paidDouble,
                                        notes = notes,
                                        onSuccess = { onDismiss() }
                                    )
                                    Toast.makeText(context, "Transaksi Berhasil. Mencetak struk...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1.5f).height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Proses Bayar")
                            }
                        }
                    }
                }
            }
        }
    }
}
"""

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CashierPosScreen.kt", "w") as f:
    f.write(new_content)

