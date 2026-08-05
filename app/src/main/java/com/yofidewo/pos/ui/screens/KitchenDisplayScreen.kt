package com.yofidewo.pos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yofidewo.pos.data.TransactionEntity
import com.yofidewo.pos.ui.PosViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenDisplayScreen(viewModel: PosViewModel, onBack: () -> Unit) {
    val transactions by viewModel.transactions.collectAsState()
    var targetStation by remember { mutableStateOf("ALL") } // "ALL", "KITCHEN", "BAR"

    val activeOrders = transactions.take(20)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Kitchen, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kitchen Display System (KDS Dapur & Bar)", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    Row(modifier = Modifier.padding(end = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = targetStation == "ALL",
                            onClick = { targetStation = "ALL" },
                            label = { Text("Semua Order") }
                        )
                        FilterChip(
                            selected = targetStation == "KITCHEN",
                            onClick = { targetStation = "KITCHEN" },
                            label = { Text("🍳 Dapur Makanan") }
                        )
                        FilterChip(
                            selected = targetStation == "BAR",
                            onClick = { targetStation = "BAR" },
                            label = { Text("🍹 Bar Minuman") }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            if (activeOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada antrean orderan dapur baru", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 280.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(activeOrders) { tx ->
                        KitchenOrderCard(transaction = tx)
                    }
                }
            }
        }
    }
}

@Composable
fun KitchenOrderCard(transaction: TransactionEntity) {
    var isDone by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFFE8F5E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.invoiceNumber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    color = if (isDone) Color(0xFF2E7D32) else Color(0xFFE65100),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isDone) "SELESAI" else "MENUNGGU",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pelanggan: ${transaction.customerName}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(transaction.timestamp)),
                fontSize = 12.sp,
                color = Color.Gray
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Order List Items
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("• 1x Nasi Goreng Spesial (Extra Pedas)", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text("• 1x Iced Americano (Less Sugar, Less Ice)", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { isDone = !isDone },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDone) Color(0xFF757575) else Color(0xFF2E7D32)
                )
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isDone) "Tandai Belum Selesai" else "Selesai Dimasak / Ready")
            }
        }
    }
}
