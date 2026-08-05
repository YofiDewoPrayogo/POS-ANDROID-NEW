package com.yofidewo.pos.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yofidewo.pos.data.TransactionEntity
import com.yofidewo.pos.data.TransactionItemEntity
import com.yofidewo.pos.ui.PosViewModel
import com.yofidewo.pos.util.EscPosPrinterHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoiceDialog(
    transaction: TransactionEntity,
    viewModel: PosViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var items by remember { mutableStateOf<List<TransactionItemEntity>>(emptyList()) }
    val selectedPrinterAddress by viewModel.selectedPrinterAddress.collectAsState()
    val selectedPrinterName by viewModel.selectedPrinterName.collectAsState()
    val paperWidth by viewModel.paperWidth.collectAsState()
    var isPrinting by remember { mutableStateOf(false) }
    var lastPrintedTxId by remember(transaction.id) { mutableLongStateOf(-1L) }

    LaunchedEffect(transaction.id) {
        items = viewModel.repository.getItemsForTransactionSync(transaction.id)
    }

    // Auto print ONCE when items loaded if printer is configured
    LaunchedEffect(items, transaction.id) {
        if (items.isNotEmpty() && selectedPrinterAddress.isNotBlank() && lastPrintedTxId != transaction.id) {
            lastPrintedTxId = transaction.id
            isPrinting = true
            viewModel.printReceiptBluetooth(
                context = context,
                transaction = transaction,
                items = items,
                onSuccess = {
                    isPrinting = false
                    Toast.makeText(context, "Struk terkirim ke printer!", Toast.LENGTH_SHORT).show()
                },
                onError = { err ->
                    isPrinting = false
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val formattedDate = dateFormatter.format(Date(transaction.timestamp))

    val doPrintBluetooth = {
        if (selectedPrinterAddress.isBlank()) {
            Toast.makeText(context, "Pilih printer Bluetooth di Pengaturan terlebih dahulu!", Toast.LENGTH_LONG).show()
        } else {
            isPrinting = true
            viewModel.printReceiptBluetooth(
                context = context,
                transaction = transaction,
                items = items,
                onSuccess = {
                    isPrinting = false
                    Toast.makeText(context, "Struk berhasil dicetak ke $selectedPrinterName!", Toast.LENGTH_SHORT).show()
                },
                onError = { err ->
                    isPrinting = false
                    Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    val doShareText = {
        val widthMm = if (paperWidth.contains("80")) 80 else 58
        val textReceipt = EscPosPrinterHelper.buildTextReceipt(
            transaction = transaction,
            items = items,
            storeName = viewModel.outletName.value,
            storeAddress = viewModel.outletAddress.value,
            paperWidthMm = widthMm,
            formatMoney = { viewModel.formatMoney(it) }
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, textReceipt)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Bagikan Struk Nota"))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Sukses",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Struk Pembayaran",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = transaction.invoiceNumber,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Tanggal: $formattedDate", fontSize = 12.sp)
                        Text(text = "Kasir: ${transaction.cashierName}", fontSize = 12.sp)
                        Text(text = "Pelanggan: ${transaction.customerName}", fontSize = 12.sp)
                        Text(text = "Metode Bayar: ${transaction.paymentMethod}", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Daftar Belanja",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${item.quantity} x ${viewModel.formatMoney(item.price)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = viewModel.formatMoney(item.subtotal),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = viewModel.formatMoney(transaction.totalAmount),
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Jumlah Dibayar:", fontSize = 12.sp)
                        Text(
                            text = viewModel.formatMoney(transaction.paidAmount),
                            fontSize = 12.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Kembalian:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = viewModel.formatMoney(transaction.changeAmount),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { doPrintBluetooth() },
                            enabled = !isPrinting,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isPrinting) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                            } else {
                                Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cetak Thermal", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { doShareText() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bagikan Struk", fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Selesai (Tutup)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
