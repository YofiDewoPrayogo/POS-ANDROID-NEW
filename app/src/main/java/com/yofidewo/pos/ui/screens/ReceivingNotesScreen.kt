package com.yofidewo.pos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import com.yofidewo.pos.data.ProductEntity
import com.yofidewo.pos.ui.PosViewModel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceivingNotesScreen(viewModel: PosViewModel) {
    val context = LocalContext.current
    val notes by viewModel.receivingNotes.collectAsState()
    val products by viewModel.products.collectAsState()
    val warehouses by viewModel.warehouses.collectAsState()

    var showAddNoteDialog by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddNoteDialog = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add Receiving Note", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isTablet = maxWidth > 600.dp
                if (isTablet) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = "Penerimaan Stok (RN)", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Log penerimaan barang & penambahan stok", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { viewModel.exportProductsToCsv(context) }) {
                                Icon(imageVector = Icons.Default.TableChart, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ekspor Excel / CSV")
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Penerimaan Stok (RN)", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Log penerimaan barang & penambahan stok", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { viewModel.exportProductsToCsv(context) }, modifier = Modifier.fillMaxWidth()) {
                                Icon(imageVector = Icons.Default.TableChart, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ekspor Data Stok (Excel)")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            var selectedRnRef by remember { mutableStateOf<String?>(null) }
            val groupedNotes = remember(notes) { notes.groupBy { it.referenceNumber } }

            if (notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Belum ada log penerimaan stok.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    items(groupedNotes.keys.toList()) { ref ->
                        val list = groupedNotes[ref] ?: emptyList()
                        val firstNote = list.firstOrNull() ?: return@items
                        val totalQty = list.sumOf { it.quantityReceived }
                        val totalCostSum = list.sumOf { it.unitCost * it.quantityReceived }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRnRef = ref },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = ref, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                    Text(text = dateFormatter.format(Date(firstNote.timestamp)), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    Text(text = "Pemasok: ${firstNote.supplierName}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                    Text(text = "Ringkasan: ${list.size} Jenis Barang ($totalQty Pcs)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (firstNote.notes.isNotBlank()) {
                                        Text(text = "Catatan: ${firstNote.notes}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Total Faktur RN", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = viewModel.formatMoney(totalCostSum), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    OutlinedButton(
                                        onClick = { selectedRnRef = ref },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Detail Invoice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Modal Detail Invoice RN
            selectedRnRef?.let { ref ->
                val list = groupedNotes[ref] ?: emptyList()
                val firstNote = list.firstOrNull()
                Dialog(onDismissRequest = { selectedRnRef = null }) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Detail Faktur Penerimaan Stok", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("No Ref: $ref", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { selectedRnRef = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Pemasok: ${firstNote?.supplierName ?: "-"}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Gudang: ${firstNote?.warehouseName ?: "Gudang Utama"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Daftar Item Barang Diterima:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))

                            LazyColumn(modifier = Modifier.heightIn(max = 280.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(list) { item ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("${item.quantityReceived} Pcs x ${viewModel.formatMoney(item.unitCost)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text(viewModel.formatMoney(item.quantityReceived * item.unitCost), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Total ${list.sumOf { it.quantityReceived }} Item", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(viewModel.formatMoney(list.sumOf { it.quantityReceived * it.unitCost }), fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddNoteDialog) {
        AddReceivingNoteDialog(
            products = products,
            warehouses = warehouses,
            viewModel = viewModel,
            onDismiss = { showAddNoteDialog = false }
        )
    }
}

class ReceivingRowState(
    val id: Long = System.nanoTime(),
    initialProduct: ProductEntity? = null,
    initialQty: String = "1",
    initialCost: String = "0"
) {
    var selectedProduct by mutableStateOf(initialProduct)
    var qtyStr by mutableStateOf(initialQty)
    var costStr by mutableStateOf(initialCost)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReceivingNoteDialog(
    products: List<ProductEntity>,
    warehouses: List<com.yofidewo.pos.data.WarehouseEntity>,
    viewModel: PosViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedWarehouse by remember { mutableStateOf(warehouses.firstOrNull()) }
    var supplierName by remember { mutableStateOf("") }
    var refNumber by remember { mutableStateOf("RN-" + System.currentTimeMillis().toString().takeLast(8)) }
    var notes by remember { mutableStateOf("") }
    var shippingCostStr by remember { mutableStateOf("") }

    val rows = remember {
        mutableStateListOf(
            ReceivingRowState(
                initialProduct = products.firstOrNull(),
                initialQty = "1",
                initialCost = products.firstOrNull()?.buyPrice?.let { if (it > 0) it.toInt().toString() else "0" } ?: "0"
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.90f)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Log Penerimaan Stok Baru",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Input sekaligus beberapa item / multi-item",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            // Header Supplier & Ref Info Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Informasi Pemasok & Dokumen", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                                    OutlinedTextField(
                                        value = supplierName,
                                        onValueChange = { supplierName = it },
                                        label = { Text("Nama Pemasok / Supplier") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = refNumber,
                                        onValueChange = { refNumber = it },
                                        label = { Text("No. Referensi / Surat Jalan") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = notes,
                                        onValueChange = { notes = it },
                                        label = { Text("Catatan / Batch") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = shippingCostStr,
                                        onValueChange = { shippingCostStr = it },
                                        label = { Text("Biaya Ongkir Pembelian (Masuk HPP)") },
                                        placeholder = { Text("0") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Daftar Barang Diterima (${rows.size} Item)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp).clickable {
                                         val defaultProd = products.firstOrNull()
                                         rows.add(
                                             ReceivingRowState(
                                                 initialProduct = defaultProd,
                                                 initialQty = "1",
                                                 initialCost = defaultProd?.buyPrice?.let { if (it > 0) it.toInt().toString() else "0" } ?: "0"
                                             )
                                         )
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Add, contentDescription = "Tambah Item", tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }

                        itemsIndexed(rows, key = { index, row -> row.id }) { index, row ->
                            var dropdownExpanded by remember { mutableStateOf(false) }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Item #${index + 1}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (rows.size > 1) {
                                            IconButton(
                                                onClick = { rows.removeAt(index) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Hapus Baris",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Product Selector
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedButton(
                                            onClick = { dropdownExpanded = true },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = row.selectedProduct?.let { "${it.name} (Stok: ${it.stock})" } ?: "Pilih Produk",
                                                    fontSize = 13.sp
                                                )
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = dropdownExpanded,
                                            onDismissRequest = { dropdownExpanded = false },
                                            modifier = Modifier.fillMaxWidth(0.85f)
                                        ) {
                                            products.forEach { p ->
                                                DropdownMenuItem(
                                                    text = { Text("${p.name} (Stok: ${p.stock}) - ${viewModel.formatMoney(p.buyPrice)}") },
                                                    onClick = {
                                                        row.selectedProduct = p
                                                        row.costStr = if (p.buyPrice > 0) p.buyPrice.toInt().toString() else "0"
                                                        dropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // Qty & Unit Cost
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = row.qtyStr,
                                            onValueChange = { row.qtyStr = it },
                                            label = { Text("Jumlah (Qty)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = row.costStr,
                                            onValueChange = { row.costStr = it },
                                            label = { Text("Harga Modal Satuan") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1.2f)
                                        )
                                    }

                                    val q = row.qtyStr.toIntOrNull() ?: 0
                                    val c = row.costStr.toDoubleOrNull() ?: 0.0
                                    val subtotal = q * c
                                    if (subtotal > 0) {
                                        Text(
                                            text = "Subtotal: ${viewModel.formatMoney(subtotal)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action & Total Summary
                val totalQty = rows.sumOf { it.qtyStr.toIntOrNull() ?: 0 }
                val totalCostSum = rows.sumOf { (it.qtyStr.toIntOrNull() ?: 0) * (it.costStr.toDoubleOrNull() ?: 0.0) }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Total $totalQty Barang Diterima", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = viewModel.formatMoney(totalCostSum), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal") }
                            Button(
                                onClick = {
                                    if (supplierName.isBlank()) {
                                        Toast.makeText(context, "Masukkan nama pemasok/supplier", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                     val totalQtySum = rows.sumOf { it.qtyStr.toIntOrNull() ?: 0 }
                                    val shippingCost = shippingCostStr.toDoubleOrNull() ?: 0.0
                                    val extraCostPerUnit = if (totalQtySum > 0 && shippingCost > 0) (shippingCost / totalQtySum) else 0.0

                                    val batchList = rows.mapNotNull { row ->
                                        val prod = row.selectedProduct ?: return@mapNotNull null
                                        val q = row.qtyStr.toIntOrNull() ?: 0
                                        val baseCost = row.costStr.toDoubleOrNull() ?: 0.0
                                        val totalUnitHpp = baseCost + extraCostPerUnit
                                        if (q > 0) Pair(prod, Pair(q, totalUnitHpp)) else null
                                    }
                                    if (batchList.isEmpty()) {
                                        Toast.makeText(context, "Pilih minimal 1 produk dengan jumlah > 0", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    val notesCombined = if (shippingCost > 0) "$notes (Termasuk Ongkir Rp ${shippingCost.toInt()})".trim() else notes

                                    viewModel.addReceivingNotesBatch(
                                        supplierName = supplierName,
                                        refNumber = refNumber,
                                        warehouseId = selectedWarehouse?.id,
                                        warehouseName = selectedWarehouse?.name ?: "Gudang Utama",
                                        items = batchList,
                                        notes = notesCombined
                                    )

                                    Toast.makeText(context, "Berhasil menambah stok & alokasi HPP untuk ${batchList.size} item!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1.2f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Simpan", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun batchListSize(rows: List<ReceivingRowState>): Int {
    return rows.count { (it.qtyStr.toIntOrNull() ?: 0) > 0 && it.selectedProduct != null }
}
