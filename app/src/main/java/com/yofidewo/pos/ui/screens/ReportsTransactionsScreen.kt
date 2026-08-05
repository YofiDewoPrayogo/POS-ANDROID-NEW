package com.yofidewo.pos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yofidewo.pos.data.DiscountEntity
import com.yofidewo.pos.data.TransactionEntity
import com.yofidewo.pos.data.TransactionItemEntity
import com.yofidewo.pos.ui.PosViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsTransactionsScreen(
    viewModel: PosViewModel,
    onNavigate: (String) -> Unit = {}
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(selectedTabIndex = selectedSubTab, edgePadding = 12.dp) {
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
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("Retur Penjualan", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.AssignmentReturn, contentDescription = null) }
            )
            Tab(
                selected = selectedSubTab == 3,
                onClick = { selectedSubTab = 3 },
                text = { Text("Hutang Supplier & Jatuh Tempo", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.LocalShipping, contentDescription = null) }
            )
            Tab(
                selected = selectedSubTab == 4,
                onClick = { selectedSubTab = 4 },
                text = { Text("Jurnal Umum Akuntansi", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.DateRange, contentDescription = null) }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedSubTab) {
                0 -> TransactionsHistoryContent(viewModel = viewModel)
                1 -> PiutangContent(viewModel = viewModel)
                2 -> ReturnsContent(viewModel = viewModel)
                3 -> HutangSupplierContent(viewModel = viewModel)
                4 -> JournalEntriesContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsHistoryContent(viewModel: PosViewModel) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCashier by remember { mutableStateOf("Semua") }
    var selectedMethod by remember { mutableStateOf("Semua") }
    var showFilters by remember { mutableStateOf(false) }

    var selectedTxForReturn by remember { mutableStateOf<TransactionEntity?>(null) }

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf<Long?>(null) }
    var selectedEndDate by remember { mutableStateOf<Long?>(null) }
    var selectedReceiptTx by remember { mutableStateOf<TransactionEntity?>(null) }

    // Filter valid sales (exclude returned)
    val validSales = transactions.filter { it.status != "RETURNED" }

    val filtered = validSales.filter { tx ->
        val matchSearch = tx.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                tx.customerName.contains(searchQuery, ignoreCase = true) ||
                tx.cashierName.contains(searchQuery, ignoreCase = true)
        val matchCashier = if (selectedCashier == "Semua") true else tx.cashierName == selectedCashier
        val matchMethod = if (selectedMethod == "Semua") true else tx.paymentMethod.contains(selectedMethod, ignoreCase = true)
        
        val matchDate = if (selectedStartDate != null && selectedEndDate != null) {
            tx.timestamp in selectedStartDate!!..selectedEndDate!!
        } else true

        matchSearch && matchCashier && matchMethod && matchDate
    }

    val totalRevenueUsd = filtered.sumOf { it.totalAmount }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Responsive Layout for Header
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isTablet = maxWidth > 600.dp
            
            if (isTablet) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val deviceRole by viewModel.deviceRole.collectAsState()
                    var isOmsetUnlocked by remember { mutableStateOf(false) }

                    Column {
                        Text(text = "Riwayat Penjualan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (deviceRole == "KASIR" && !isOmsetUnlocked) "Total Omzet: Rp ••••••••" else "Total Omzet: ${viewModel.formatMoney(totalRevenueUsd)}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            if (deviceRole == "KASIR" && !isOmsetUnlocked) {
                                IconButton(onClick = { isOmsetUnlocked = true }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Lock, contentDescription = "Unlock Omset", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showDatePickerDialog = true }, modifier = Modifier.height(40.dp), shape = RoundedCornerShape(10.dp)) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (selectedStartDate != null) "Filter Active ✓" else "Pilih Tanggal", fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                val periodText = if (selectedStartDate != null && selectedEndDate != null) "Filter Tanggal" else "Semua Data"
                                viewModel.printSalesReportThermal(
                                    reportTitle = "Laporan Penjualan & Omzet",
                                    periodText = periodText,
                                    transactionsList = filtered,
                                    onSuccess = { Toast.makeText(context, "Laporan berhasil dicetak ke Printer Thermal!", Toast.LENGTH_SHORT).show() },
                                    onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                                )
                            },
                            modifier = Modifier.height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cetak Thermal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(onClick = { viewModel.exportTransactionsToPdf(context) }, modifier = Modifier.height(40.dp), shape = RoundedCornerShape(10.dp)) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ekspor PDF", fontSize = 12.sp)
                        }
                        Button(onClick = { viewModel.exportTransactionsToCsv(context) }, modifier = Modifier.height(40.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                            Icon(imageVector = Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ekspor Excel", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Riwayat Penjualan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { showFilters = !showFilters }) {
                            Text(if (showFilters) "Tutup Filter" else "Filter Data")
                        }
                    }
                    Text(text = "Total Omzet: ${viewModel.formatMoney(totalRevenueUsd)}", fontSize = 15.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showDatePickerDialog = true },
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (selectedStartDate != null) "Filter ✓" else "Tanggal", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { viewModel.exportTransactionsToPdf(context) },
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ekspor PDF", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { viewModel.exportTransactionsToCsv(context) },
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            Icon(imageVector = Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Excel / CSV", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari no faktur, pelanggan, kasir...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Collapsible Filters
        if (showFilters) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Filter Kasir & Pembayaran:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val methods = listOf("Semua", "Tunai", "QRIS / Transfer", "Kartu Debit/Kredit", "Piutang")
                        var expandedMethod by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedMethod,
                            onExpandedChange = { expandedMethod = !expandedMethod },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedMethod,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Metode") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMethod) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedMethod,
                                onDismissRequest = { expandedMethod = false }
                            ) {
                                methods.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m) },
                                        onClick = {
                                            selectedMethod = m
                                            expandedMethod = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tidak ada riwayat transaksi penjualan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { tx ->
                    Card(
                        onClick = { viewModel.selectedTransactionForInvoice.value = tx },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.invoiceNumber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Pelanggan: ${tx.customerName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${dateFormatter.format(Date(tx.timestamp))} • ${tx.paymentMethod}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Kasir: ${tx.cashierName} • Catatan: ${tx.notes.ifBlank { "-" }}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                val isLunas = tx.paymentStatus == "LUNAS" || (!tx.paymentMethod.contains("Piutang", ignoreCase = true) && tx.paymentStatus != "BELUM LUNAS")
                                val statusText = if (isLunas) "LUNAS" else if (tx.paymentStatus == "DIBAYAR SEBAGIAN") "SEBAGIAN" else "BELUM LUNAS (PIUTANG)"
                                val statusBg = if (isLunas) Color(0xFFD1FAE5) else Color(0xFFFFEDD5)
                                val statusTextColor = if (isLunas) Color(0xFF065F46) else Color(0xFFC2410C)

                                Surface(
                                    color = statusBg,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = statusText,
                                        color = statusTextColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = viewModel.formatMoney(tx.totalAmount),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                OutlinedButton(
                                    onClick = { viewModel.selectedTransactionForInvoice.value = tx },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cetak Ulang Struk", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                if (currentRole?.name == "Administrator") {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    TextButton(
                                        onClick = { selectedTxForReturn = tx },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) {
                                        Icon(Icons.Default.AssignmentReturn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Retur Penjualan", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Detail Invoice & Proses Retur Penjualan
    selectedTxForReturn?.let { tx ->
        ReturnInvoiceDetailDialog(
            transaction = tx,
            viewModel = viewModel,
            onDismiss = { selectedTxForReturn = null },
            onConfirmReturn = { reason ->
                viewModel.returnTransaction(tx, reason)
                Toast.makeText(context, "Retur Penjualan ${tx.invoiceNumber} Berhasil Diproses!", Toast.LENGTH_SHORT).show()
                selectedTxForReturn = null
            }
        )
    }

    // DateRangePicker Dialog
    if (showDatePickerDialog) {
        DateRangePickerDialog(
            onDismiss = { showDatePickerDialog = false },
            onConfirm = { startMs, endMs ->
                selectedStartDate = startMs
                // Set endMs to end of day
                selectedEndDate = endMs + (24 * 60 * 60 * 1000 - 1)
                showDatePickerDialog = false
            },
            onReset = {
                selectedStartDate = null
                selectedEndDate = null
                showDatePickerDialog = false
            }
        )
    }
}

@Composable
fun ReturnInvoiceDetailDialog(
    transaction: TransactionEntity,
    viewModel: PosViewModel,
    onDismiss: () -> Unit,
    onConfirmReturn: (String) -> Unit
) {
    var reasonInput by remember { mutableStateOf("") }
    var itemsList by remember { mutableStateOf<List<TransactionItemEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(transaction.id) {
        itemsList = viewModel.getTransactionItems(transaction.id)
        isLoading = false
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Detail Transaksi", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        Text("Faktur: ${transaction.invoiceNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Metadata
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Pelanggan: ${transaction.customerName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Kasir: ${transaction.cashierName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Metode: ${transaction.paymentMethod}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Status: ${transaction.paymentStatus}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Daftar Item Transaksi:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            itemsList.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.productName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text("${item.quantity}x @ ${viewModel.formatMoney(item.price)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(viewModel.formatMoney(item.subtotal), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(viewModel.formatMoney(transaction.subTotalAmount), fontSize = 12.sp)
                }
                if (transaction.discountAmount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Diskon:", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                        Text("-${viewModel.formatMoney(transaction.discountAmount)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Akhir:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(viewModel.formatMoney(transaction.totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input Reason for Return
                OutlinedTextField(
                    value = reasonInput,
                    onValueChange = { reasonInput = it },
                    label = { Text("Alasan Retur Penjualan") },
                    placeholder = { Text("Misal: Barang cacat, pembeli batal...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirmReturn(reasonInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AssignmentReturn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Proses Retur", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PiutangContent(viewModel: PosViewModel) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    
    val piutangList = transactions.filter {
        (it.paymentMethod.contains("Piutang", ignoreCase = true) || it.paymentStatus == "BELUM LUNAS") &&
                it.paymentStatus != "LUNAS" &&
                it.status != "RETURNED"
    }.filter {
        it.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                it.customerName.contains(searchQuery, ignoreCase = true)
    }

    val totalPiutang = piutangList.sumOf { it.totalAmount - it.paidAmount }
    var selectedTxForPay by remember { mutableStateOf<TransactionEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Summary Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Piutang (Belum Lunas)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                    Text(
                        viewModel.formatMoney(totalPiutang),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        "${piutangList.size} Transaksi",
                        color = MaterialTheme.colorScheme.onError,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari pelanggan atau no faktur piutang...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (piutangList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Tidak ada data piutang pelanggan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(piutangList) { trx ->
                    val sisaSatuTrx = trx.totalAmount - trx.paidAmount
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(trx.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(
                                        "Pelanggan: ${trx.customerName}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    viewModel.formatMoney(sisaSatuTrx),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tanggal: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(trx.timestamp))}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Kasir: ${trx.cashierName} • Catatan: ${trx.notes.ifBlank { "-" }}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "BELUM LUNAS",
                                        color = Color(0xFFE65100),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                
                                Button(
                                    onClick = { selectedTxForPay = trx },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Bayar / Pelunasan")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Pelunasan Piutang
    selectedTxForPay?.let { trx ->
        PelunasanPiutangDialog(
            transaction = trx,
            viewModel = viewModel,
            onDismiss = { selectedTxForPay = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PelunasanPiutangDialog(
    transaction: TransactionEntity,
    viewModel: PosViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sisaSatuTrx = transaction.totalAmount - transaction.paidAmount

    val discounts by viewModel.discounts.collectAsState()
    val customMethods by viewModel.customPaymentMethods.collectAsState()

    var selectedMethod by remember { mutableStateOf("Tunai") }
    var expandedMethod by remember { mutableStateOf(false) }

    var selectedDiscount by remember { mutableStateOf<DiscountEntity?>(null) }
    var expandedDiscount by remember { mutableStateOf(false) }

    val calculatedDiscountAmount = when {
        selectedDiscount == null -> 0.0
        selectedDiscount?.type == "PERCENT" -> sisaSatuTrx * ((selectedDiscount?.value ?: 0.0) / 100.0)
        selectedDiscount?.type == "FIXED" -> (selectedDiscount?.value ?: 0.0)
        else -> 0.0
    }

    val sisaHarusDibayar = (sisaSatuTrx - calculatedDiscountAmount).coerceAtLeast(0.0)
    var payInput by remember { mutableStateOf(sisaHarusDibayar.toString()) }

    LaunchedEffect(calculatedDiscountAmount) {
        payInput = sisaHarusDibayar.toString()
    }

    val allMethods = remember(customMethods) {
        val base = listOf("Tunai", "QRIS / Transfer", "Kartu Debit/Kredit")
        (base + customMethods).distinct()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                Text("Pelunasan Piutang", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("No Faktur: ${transaction.invoiceNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Pelanggan: ${transaction.customerName}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Total Sisa Piutang: ${viewModel.formatMoney(sisaSatuTrx)}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        if (calculatedDiscountAmount > 0) {
                            Text("Diskon Pelunasan: -${viewModel.formatMoney(calculatedDiscountAmount)}", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Harus Dibayar: ${viewModel.formatMoney(sisaHarusDibayar)}", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Select Discount Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedDiscount,
                    onExpandedChange = { expandedDiscount = !expandedDiscount },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedDiscount?.let { disc ->
                            if (disc.type == "PERCENT") "${disc.name} (${disc.value.toInt()}%)"
                            else "${disc.name} (${viewModel.formatMoney(disc.value)})"
                        } ?: "Tanpa Diskon",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Diskon Pelunasan") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDiscount) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDiscount,
                        onDismissRequest = { expandedDiscount = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tanpa Diskon (0%)") },
                            onClick = {
                                selectedDiscount = null
                                expandedDiscount = false
                            }
                        )
                        discounts.forEach { disc ->
                            val label = if (disc.type == "PERCENT") "${disc.name} (${disc.value.toInt()}%)" else "${disc.name} (${viewModel.formatMoney(disc.value)})"
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedDiscount = disc
                                    expandedDiscount = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Select Payment Method Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedMethod,
                    onExpandedChange = { expandedMethod = !expandedMethod },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedMethod,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Metode Pembayaran") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMethod) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMethod,
                        onDismissRequest = { expandedMethod = false }
                    ) {
                        allMethods.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method) },
                                onClick = {
                                    selectedMethod = method
                                    expandedMethod = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Pay Amount Field
                OutlinedTextField(
                    value = payInput,
                    onValueChange = { payInput = it },
                    label = { Text("Jumlah Pembayaran") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val payVal = payInput.toDoubleOrNull() ?: 0.0
                            val totalEffective = payVal + calculatedDiscountAmount
                            val isLunas = totalEffective >= sisaSatuTrx

                            val updatedTrx = transaction.copy(
                                paidAmount = transaction.paidAmount + payVal,
                                discountAmount = transaction.discountAmount + calculatedDiscountAmount,
                                paymentStatus = if (isLunas) "LUNAS" else "DIBAYAR SEBAGIAN",
                                changeAmount = if (payVal > sisaHarusDibayar) payVal - sisaHarusDibayar else transaction.changeAmount,
                                paymentMethod = "$selectedMethod (Pelunasan Piutang)"
                            )
                            viewModel.updateTransaction(updatedTrx)
                            Toast.makeText(context, if (isLunas) "Piutang LUNAS!" else "Pembayaran sebagian dicatat!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Proses Pelunasan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnsContent(viewModel: PosViewModel) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

    var selectedTxForItems by remember { mutableStateOf<TransactionEntity?>(null) }

    val returnedList = transactions.filter { it.status == "RETURNED" }.filter {
        it.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                it.customerName.contains(searchQuery, ignoreCase = true) ||
                it.cashierName.contains(searchQuery, ignoreCase = true)
    }

    val totalRefundValue = returnedList.sumOf { it.totalAmount }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Summary Header Card for Returns
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Laporan Retur Penjualan", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text(
                        viewModel.formatMoney(totalRefundValue),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        "${returnedList.size} Transaksi Retur",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari no faktur atau pelanggan retur...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (returnedList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Belum ada data laporan retur penjualan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(returnedList) { trx ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        trx.invoiceNumber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        "Pelanggan: ${trx.customerName}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    viewModel.formatMoney(trx.totalAmount),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tanggal: ${dateFormatter.format(Date(trx.timestamp))}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Kasir: ${trx.cashierName} • Catatan: ${trx.notes.ifBlank { "-" }}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { selectedTxForItems = trx },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Lihat Items Retur", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Item List Retur
    selectedTxForItems?.let { trx ->
        var itemsList by remember { mutableStateOf<List<TransactionItemEntity>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(trx.id) {
            itemsList = viewModel.getTransactionItems(trx.id)
            isLoading = false
        }

        Dialog(onDismissRequest = { selectedTxForItems = null }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                    Text("Detail Items Retur Penjualan", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.error)
                    Text("Faktur: ${trx.invoiceNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Alasan / Catatan: ${trx.notes.ifBlank { "Tidak ada catatan" }}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                itemsList.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.productName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            Text("${item.quantity}x @ ${viewModel.formatMoney(item.price)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(viewModel.formatMoney(item.subtotal), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(onClick = { selectedTxForItems = null }) {
                            Text("Tutup")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long, Long) -> Unit,
    onReset: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    // Phase 0 = pilih tanggal mulai, Phase 1 = pilih tanggal akhir
    var phase by remember { mutableIntStateOf(0) }
    var startDateMs by remember { mutableStateOf<Long?>(null) }

    val startState = rememberDatePickerState(
        initialSelectedDateMillis = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
    )
    val endState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (phase == 0) "Pilih Tanggal Mulai" else "Pilih Tanggal Akhir",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (phase == 1 && startDateMs != null) {
                            Text(
                                text = "Dari: ${dateFormatter.format(Date(startDateMs!!))}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    // Step indicator
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(
                                    if (phase >= 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(
                                    if (phase >= 1) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))

                // DatePicker
                if (phase == 0) {
                    DatePicker(
                        state = startState,
                        showModeToggle = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    DatePicker(
                        state = endState,
                        showModeToggle = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReset,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset Filter")
                    }
                    if (phase == 0) {
                        Button(
                            onClick = {
                                startDateMs = startState.selectedDateMillis
                                phase = 1
                            },
                            modifier = Modifier.weight(1f),
                            enabled = startState.selectedDateMillis != null
                        ) {
                            Text("Lanjut →")
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedButton(
                                onClick = { phase = 0 },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("← Kembali")
                            }
                            Button(
                                onClick = {
                                    val s = startDateMs
                                    val e = endState.selectedDateMillis
                                    if (s != null && e != null) {
                                        if (e >= s) {
                                            onConfirm(s, e)
                                        } else {
                                            // Jika akhir < mulai, tukar urutan
                                            onConfirm(e, s)
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = endState.selectedDateMillis != null
                            ) {
                                Text("Terapkan")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HutangSupplierContent(viewModel: PosViewModel) {
    val receivingNotes by viewModel.receivingNotes.collectAsState()
    val debtNotes = receivingNotes.filter { it.goodsPaymentMethod.contains("HUTANG", ignoreCase = true) || it.paymentStatus == "BELUM LUNAS" }
    val totalHutang = debtNotes.sumOf { (it.quantityReceived * it.unitCost) + it.shippingCost }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Hutang Usaha Supplier (Accounts Payable)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(viewModel.formatMoney(totalHutang), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Daftar Tagihan & Jatuh Tempo Supplier:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        if (debtNotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada catatan hutang supplier.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(debtNotes) { rn ->
                    val totalTagihan = (rn.quantityReceived * rn.unitCost) + rn.shippingCost
                    val dueDateStr = rn.dueDate?.let { dateFormatter.format(Date(it)) } ?: "Tidak Diatur"
                    val isOverdue = rn.dueDate != null && rn.dueDate < System.currentTimeMillis()

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(rn.supplierName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("No. Surat Jalan: ${rn.referenceNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Barang: ${rn.productName} (${rn.quantityReceived} pcs)", fontSize = 12.sp)
                                Text(
                                    "Jatuh Tempo: $dueDateStr",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(viewModel.formatMoney(totalTagihan), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFC2410C))
                                Surface(
                                    color = if (isOverdue) Color(0xFFFEE2E2) else Color(0xFFFFEDD5),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        if (isOverdue) "JATUH TEMPO!" else "BELUM LUNAS",
                                        color = if (isOverdue) Color.Red else Color(0xFFC2410C),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JournalEntriesContent(viewModel: PosViewModel) {
    val journalEntries by viewModel.journalEntries.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Jurnal Umum Akuntansi (General Journal)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Catatan Otomatis Pembukuan Debit / Kredit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (journalEntries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada entri jurnal akuntansi.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(journalEntries) { jrn ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${jrn.journalNumber} • ${jrn.transactionRef}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(jrn.accountName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                Text(jrn.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(dateFormatter.format(Date(jrn.timestamp)), fontSize = 10.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                if (jrn.debitAmount > 0) {
                                    Text("Debit: ${viewModel.formatMoney(jrn.debitAmount)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF047857))
                                }
                                if (jrn.creditAmount > 0) {
                                    Text("Kredit: ${viewModel.formatMoney(jrn.creditAmount)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFB91C1C))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
