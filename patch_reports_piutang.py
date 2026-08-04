import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ReportsTransactionsScreen.kt", "r") as f:
    content = f.read()

# Replace Tab contents
new_tabs = """        SecondaryTabRow(selectedTabIndex = selectedSubTab) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Riwayat Penjualan", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Receipt, contentDescription = null) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Piutang (Kasbon)", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.TableChart, contentDescription = null) }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedSubTab) {
                0 -> TransactionsHistoryContent(viewModel = viewModel)
                1 -> PiutangContent(viewModel = viewModel)
            }
        }"""

content = re.sub(r'SecondaryTabRow\([\s\S]*?\}\n        \}', new_tabs, content)

# Remove DashboardScreen import if it exists, wait, it's in the same package probably so no import, but just to be sure.

# Add PiutangContent at the end
piutang_ui = """
@Composable
fun PiutangContent(viewModel: PosViewModel) {
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val piutangList = transactions.filter { it.paymentMethod == "Piutang / Kasbon" && it.paymentStatus != "LUNAS" }
    
    if (piutangList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tidak ada piutang.", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(piutangList) { trx ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(trx.id, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(viewModel.formatMoney(trx.totalAmount), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tanggal: ${SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(trx.timestamp))}")
                        Text("Status: Belum Lunas")
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = {
                            // Tandai Lunas (update paymentStatus ke LUNAS)
                            val updatedTrx = trx.copy(paymentStatus = "LUNAS", paymentMethod = "Tunai (Pelunasan Piutang)")
                            viewModel.updateTransaction(updatedTrx)
                        }, modifier = Modifier.align(Alignment.End)) {
                            Text("Tandai Lunas")
                        }
                    }
                }
            }
        }
    }
}
"""

content = content + piutang_ui

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ReportsTransactionsScreen.kt", "w") as f:
    f.write(content)

