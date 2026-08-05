package com.yofidewo.pos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yofidewo.pos.ui.PosViewModel
import com.yofidewo.pos.ui.navigation.Screen

@Composable
fun DashboardScreen(
    viewModel: PosViewModel,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsState()
    val products by viewModel.products.collectAsState()
    val receivingNotes by viewModel.receivingNotes.collectAsState()
    val selectedPrinterName by viewModel.selectedPrinterName.collectAsState()

    var selectedFilter by remember { mutableStateOf("Hari Ini") }
    var showBackupDialog by remember { mutableStateOf(false) }

    val filters = listOf("Hari Ini", "Minggu Ini", "Bulan Ini", "Semua")

    // Filtered transactions by period
    val filteredTransactions = transactions.filter { tx ->
        if (tx.status == "RETURNED") return@filter false
        if (selectedFilter == "Semua") return@filter true

        val txCal = java.util.Calendar.getInstance().apply { timeInMillis = tx.timestamp }
        val todayCal = java.util.Calendar.getInstance()

        when (selectedFilter) {
            "Hari Ini" -> {
                txCal.get(java.util.Calendar.YEAR) == todayCal.get(java.util.Calendar.YEAR) &&
                txCal.get(java.util.Calendar.DAY_OF_YEAR) == todayCal.get(java.util.Calendar.DAY_OF_YEAR)
            }
            "Minggu Ini" -> {
                val diffDays = (todayCal.timeInMillis - txCal.timeInMillis) / (24 * 60 * 60 * 1000)
                diffDays in 0..7
            }
            "Bulan Ini" -> {
                val diffDays = (todayCal.timeInMillis - txCal.timeInMillis) / (24 * 60 * 60 * 1000)
                diffDays in 0..30
            }
            else -> true
        }
    }

    val totalRevenue = filteredTransactions.sumOf { it.totalAmount }
    // Estimated HPP / Modal ~ 70% of revenue or derived from product buy price
    val totalEstimatedHpp = totalRevenue * 0.72
    val totalNetProfit = (totalRevenue - totalEstimatedHpp).coerceAtLeast(0.0)
    val profitMargin = if (totalRevenue > 0) (totalNetProfit / totalRevenue) * 100 else 0.0

    val totalTransactions = filteredTransactions.size
    val lowStockCount = products.count { it.stock <= it.minStock }
    val totalStockInVal = receivingNotes.sumOf { it.quantityReceived * it.unitCost }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth > 600.dp

        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column - Stats & Financials
                Column(modifier = Modifier.weight(1.2f)) {
                    Text(
                        text = "Ringkasan Dashboard & Laba Rugi",
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

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardCard(
                            title = "Total Omzet ($selectedFilter)",
                            value = viewModel.formatMoney(totalRevenue),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardCard(
                            title = "Laba Bersih (Profit)",
                            value = "${viewModel.formatMoney(totalNetProfit)} (${String.format(java.util.Locale.US, "%.1f", profitMargin)}%)",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardCard(
                            title = "Total Transaksi",
                            value = totalTransactions.toString(),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardCard(
                            title = "Stok Menipis",
                            value = "$lowStockCount Produk",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Right Column - Quick Actions
                Column(modifier = Modifier.weight(0.8f)) {
                    Text(
                        text = "Aksi Cepat & Utilitas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    QuickActionItem("Mulai Kasir POS", "Buka layar kasir transaksi cepat", icon = Icons.Default.ShoppingCart, onClick = { onNavigate(Screen.CashierPos.route) })
                    QuickActionItem("Kelola Produk & Stok", "Tambah / edit daftar barang", icon = Icons.Default.Inventory2, onClick = { onNavigate(Screen.Products.route) })
                    QuickActionItem("Pembelian & Stok Opname (RN)", "Catat barang masuk dari pemasok", icon = Icons.Default.LocalShipping, onClick = { onNavigate(Screen.ReceivingNotes.route) })
                    QuickActionItem("Backup & Ekspor Data (Excel/File)", "Ekspor CSV & Cadangkan Database", icon = Icons.Default.CloudDownload, onClick = { showBackupDialog = true })
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
                        text = "Ringkasan Laba Rugi & Toko",
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
                        value = viewModel.formatMoney(totalRevenue),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    DashboardCard(
                        title = "Estimasi Laba Bersih (Margin ${String.format(java.util.Locale.US, "%.1f", profitMargin)}%)",
                        value = viewModel.formatMoney(totalNetProfit),
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
                            title = "Jumlah Transaksi",
                            value = totalTransactions.toString(),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardCard(
                            title = "Stok Menipis",
                            value = "$lowStockCount Produk",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Text(
                        text = "Aksi Cepat & Fitur Utilitas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item { QuickActionItem("Mulai Kasir POS", "Buka layar kasir transaksi cepat", icon = Icons.Default.ShoppingCart, onClick = { onNavigate(Screen.CashierPos.route) }) }
                item { QuickActionItem("Kelola Produk & Stok", "Tambah / edit daftar barang", icon = Icons.Default.Inventory2, onClick = { onNavigate(Screen.Products.route) }) }
                item { QuickActionItem("Layout Denah Meja F&B", "Atur denah meja, lokasi lantai & kapasitas", icon = Icons.Default.EventSeat, onClick = { onNavigate(Screen.TableLayout.route) }) }
                item { QuickActionItem("Pembelian & Stok Opname (RN)", "Catat barang masuk dari distributor", icon = Icons.Default.LocalShipping, onClick = { onNavigate(Screen.ReceivingNotes.route) }) }
                item { QuickActionItem("Backup & Ekspor Data (Excel/CSV)", "Ekspor laporan & file cadangan", icon = Icons.Default.CloudDownload, onClick = { showBackupDialog = true }) }
            }
        }
    }

    if (showBackupDialog) {
        BackupRestoreModalDialog(
            viewModel = viewModel,
            onDismiss = { showBackupDialog = false }
        )
    }
}

@Composable
fun BackupRestoreModalDialog(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Backup & Ekspor Data Toko", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Pilih format ekspor data atau cadangkan seluruh database toko Anda.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.exportProductsToCsv(context)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ekspor Produk ke Excel (CSV)")
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.exportTransactionsToCsv(context)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ekspor Laporan Penjualan (CSV)")
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.exportFullBackup(context)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Backup, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cadangkan Seluruh Database (JSON)")
                }
                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Tutup")
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    icon: ImageVector? = null,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null, tint = color)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = title, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun QuickActionItem(title: String, subtitle: String, icon: ImageVector = Icons.Default.ArrowForward, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

