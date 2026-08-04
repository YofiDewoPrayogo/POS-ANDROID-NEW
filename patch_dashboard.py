import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/DashboardScreen.kt", "r") as f:
    content = f.read()

# Make sure we add necessary imports for state and icons
if "import androidx.compose.runtime.mutableStateOf" not in content:
    content = content.replace("import androidx.compose.runtime.remember", "import androidx.compose.runtime.remember\nimport androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.setValue\nimport androidx.compose.foundation.lazy.LazyRow")

if "import androidx.compose.foundation.lazy.items" not in content:
    content = content.replace("import androidx.compose.foundation.lazy.LazyRow", "import androidx.compose.foundation.lazy.LazyRow\nimport androidx.compose.foundation.lazy.items")

if "import androidx.compose.material3.FilterChip" not in content:
    content = content.replace("import androidx.compose.material3.*", "import androidx.compose.material3.*\nimport androidx.compose.material3.FilterChip")

if "import java.time.LocalDate" not in content:
    content = content.replace("import androidx.compose.runtime.getValue", "import androidx.compose.runtime.getValue\nimport java.time.LocalDate\nimport java.time.ZoneId\nimport java.util.Date")

dashboard_content = """@Composable
fun DashboardScreen(
    viewModel: PosViewModel,
    onNavigate: (String) -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val products by viewModel.products.collectAsState()
    
    var selectedFilter by remember { mutableStateOf("Hari Ini") }
    val filters = listOf("Hari Ini", "Minggu Ini", "Bulan Ini", "Semua")

    // Simple date filtering (assuming transaction.date is timestamp in ms)
    val filteredTransactions = transactions.filter { tx ->
        if (selectedFilter == "Semua") return@filter true
        
        val txDate = LocalDate.ofEpochDay(tx.date / (24 * 60 * 60 * 1000))
        val today = LocalDate.now()
        
        when (selectedFilter) {
            "Hari Ini" -> txDate.isEqual(today)
            "Minggu Ini" -> txDate.isAfter(today.minusDays(7)) || txDate.isEqual(today)
            "Bulan Ini" -> txDate.isAfter(today.minusDays(30)) || txDate.isEqual(today)
            else -> true
        }
    }

    val totalRevenueUsd = filteredTransactions.sumOf { it.totalAmount }
    val totalTransactions = filteredTransactions.size
    val lowStockCount = products.count { it.stock <= it.minStock }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth > 600.dp
        
        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column - Stats
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ringkasan Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters) { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DashboardCard(
                        title = "Total Omzet ($selectedFilter)",
                        value = viewModel.formatMoney(totalRevenueUsd),
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardCard(
                            title = "Total Transaksi",
                            value = totalTransactions.toString(),
                            icon = Icons.Default.Receipt,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardCard(
                            title = "Stok Menipis",
                            value = lowStockCount.toString(),
                            icon = Icons.Default.Inventory,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Right Column - Quick Actions
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Aksi Cepat",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    QuickActionItem("Mulai Kasir", "Buka layar POS untuk transaksi", onClick = { onNavigate(Screen.CashierPos.route) })
                    QuickActionItem("Kelola Produk", "Tambah atau edit daftar produk", onClick = { onNavigate(Screen.Products.route) })
                    QuickActionItem("Terima Barang", "Catat penerimaan stok (RN)", onClick = { onNavigate(Screen.ReceivingNotes.route) })
                    QuickActionItem("Kategori & Brand", "Kelola kategori produk", onClick = { onNavigate(Screen.Settings.route) }) // Or wherever it is
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = "Ringkasan Dashboard",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters) { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    DashboardCard(
                        title = "Total Omzet ($selectedFilter)",
                        value = viewModel.formatMoney(totalRevenueUsd),
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardCard(
                            title = "Total Transaksi",
                            value = totalTransactions.toString(),
                            icon = Icons.Default.Receipt,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardCard(
                            title = "Stok Menipis",
                            value = lowStockCount.toString(),
                            icon = Icons.Default.Inventory,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Text(
                        text = "Aksi Cepat",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item { QuickActionItem("Mulai Kasir", "Buka layar POS untuk transaksi", onClick = { onNavigate(Screen.CashierPos.route) }) }
                item { QuickActionItem("Kelola Produk", "Tambah atau edit daftar produk", onClick = { onNavigate(Screen.Products.route) }) }
                item { QuickActionItem("Terima Barang", "Catat penerimaan stok (RN)", onClick = { onNavigate(Screen.ReceivingNotes.route) }) }
                item { QuickActionItem("Kategori & Merek", "Kelola kategori dan merek produk", onClick = { onNavigate(Screen.Settings.route) }) }
            }
        }
    }
}"""

content = re.sub(
    r"@Composable\nfun DashboardScreen.*?fun DashboardCard",
    dashboard_content + "\n\n@Composable\nfun DashboardCard",
    content,
    flags=re.DOTALL
)

content = content.replace("Icons.Default.TrendingUp", "Icons.AutoMirrored.Filled.TrendingUp")

with open("app/src/main/java/com/yofidewo/pos/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(content)
