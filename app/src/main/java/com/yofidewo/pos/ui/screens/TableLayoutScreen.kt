package com.yofidewo.pos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yofidewo.pos.data.RestaurantTableEntity
import com.yofidewo.pos.ui.PosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableLayoutScreen(viewModel: PosViewModel) {
    val context = LocalContext.current
    val tables by viewModel.restaurantTables.collectAsState()

    var selectedFloor by remember { mutableStateOf("FIRST FLOOR") }
    var showAddTableDialog by remember { mutableStateOf(false) }
    var tableToEdit by remember { mutableStateOf<RestaurantTableEntity?>(null) }

    val floors = listOf("FIRST FLOOR", "SECOND FLOOR", "OUTDOOR PATIO", "VIP ROOM")

    // Filter tables by floor
    val filteredTables = tables.filter { it.floorName.equals(selectedFloor, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Layout & Floor Plan Maintenance (F&B)",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pengaturan Denah Meja, Area Lantai, Kapasitas & Printer Routing",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    tableToEdit = null
                    showAddTableDialog = true
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tambah Meja", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Floor Tabs Selector
        ScrollableTabRow(
            selectedTabIndex = floors.indexOf(selectedFloor).coerceAtLeast(0),
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            floors.forEach { floor ->
                Tab(
                    selected = selectedFloor == floor,
                    onClick = { selectedFloor = floor },
                    text = { Text(floor, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    icon = { Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend Status Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(0xFF2E7D32)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🟩 Kosong / Available (${tables.count { it.status == "AVAILABLE" }})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(0xFFC62828)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🟥 Terisi / Occupied (${tables.count { it.status == "OCCUPIED" }})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(0xFFF57C00)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🟨 Reserved (${tables.count { it.status == "RESERVED" }})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Floor Plan Grid Layout
        if (filteredTables.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.MeetingRoom, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Belum ada meja di area $selectedFloor", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Klik 'Tambah Meja' di atas untuk membuat denah lokasi meja.", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 130.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredTables) { table ->
                    val statusColor = when (table.status.uppercase()) {
                        "OCCUPIED" -> Color(0xFFC62828)
                        "RESERVED" -> Color(0xFFF57C00)
                        else -> Color(0xFF2E7D32)
                    }

                    val shapeVal = if (table.shape.contains("ROUND", ignoreCase = true)) CircleShape else RoundedCornerShape(12.dp)

                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(shapeVal)
                            .clickable {
                                tableToEdit = table
                                showAddTableDialog = true
                            },
                        colors = CardDefaults.cardColors(containerColor = statusColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = table.tableNumber,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EventSeat, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${table.capacity} Kursi", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = table.status,
                                    color = Color.White,
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

    // Modal Add / Edit Table
    if (showAddTableDialog) {
        var tableNo by remember { mutableStateOf(tableToEdit?.tableNumber ?: "Meja ${tables.size + 1}") }
        var floorName by remember { mutableStateOf(tableToEdit?.floorName ?: selectedFloor) }
        var capacityStr by remember { mutableStateOf(tableToEdit?.capacity?.toString() ?: "4") }
        var shapeStr by remember { mutableStateOf(tableToEdit?.shape ?: "RECTANGLE") }
        var statusStr by remember { mutableStateOf(tableToEdit?.status ?: "AVAILABLE") }

        Dialog(onDismissRequest = { showAddTableDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (tableToEdit == null) "Tambah Meja Baru" else "Edit Detail Meja ${tableToEdit?.tableNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    OutlinedTextField(
                        value = tableNo,
                        onValueChange = { tableNo = it },
                        label = { Text("Nomor / Nama Meja (e.g. A01, VIP1, Meja 5)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Area / Floor Location:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                        floors.forEach { fl ->
                            FilterChip(
                                selected = floorName == fl,
                                onClick = { floorName = fl },
                                label = { Text(fl, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = capacityStr,
                        onValueChange = { capacityStr = it },
                        label = { Text("Kapasitas Tempat Duduk (Jumlah Kursi)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Bentuk Meja:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("RECTANGLE", "ROUND").forEach { sh ->
                            FilterChip(
                                selected = shapeStr == sh,
                                onClick = { shapeStr = sh },
                                label = { Text(if (sh == "ROUND") "Bulat" else "Persegi", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Text("Status Meja Saat Ini:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("AVAILABLE", "OCCUPIED", "RESERVED").forEach { st ->
                            FilterChip(
                                selected = statusStr == st,
                                onClick = { statusStr = st },
                                label = { Text(st, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        if (tableToEdit != null) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.deleteTable(tableToEdit!!)
                                    Toast.makeText(context, "Meja terhapus!", Toast.LENGTH_SHORT).show()
                                    showAddTableDialog = false
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hapus")
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.saveTable(
                                    id = tableToEdit?.id ?: 0L,
                                    number = tableNo,
                                    floor = floorName,
                                    capacity = capacityStr.toIntOrNull() ?: 4,
                                    shape = shapeStr,
                                    status = statusStr
                                )
                                Toast.makeText(context, "Layout meja berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                showAddTableDialog = false
                            },
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text("Simpan Meja", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
