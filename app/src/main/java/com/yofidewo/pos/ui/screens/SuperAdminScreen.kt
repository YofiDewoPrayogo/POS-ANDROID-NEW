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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yofidewo.pos.ui.PosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminScreen(viewModel: PosViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var outletInputCode by remember { mutableStateOf("") }
    var generatedKey by remember { mutableStateOf("") }

    val currentOutletCode by viewModel.outletCode.collectAsState()
    val isProUnlocked by viewModel.isProUnlocked.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Konsol Super Admin Developer", fontWeight = FontWeight.ExtraBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Modul Manajemen Terpusat (Super Admin)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Sebagai Super Admin/Developer, Anda dapat memantau seluruh Outlet yang terinstall di berbagai HP, mengaktifkan lisensi PRO secara remote (jarak jauh), dan membuat kode lisensi.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Quick Remote Activation Card for Current Device
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Status Outlet Ini", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Kode: ${if (currentOutletCode.isBlank()) "POS-LOCAL" else currentOutletCode}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isProUnlocked) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                            ) {
                                Text(
                                    if (isProUnlocked) "PRO AKTIF" else "TRIAL",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!isProUnlocked) {
                            Button(
                                onClick = {
                                    val code = if (currentOutletCode.isBlank()) "POS-LOCAL" else currentOutletCode
                                    val key = viewModel.generateDeveloperKey(code)
                                    viewModel.activateProKey(key)
                                    Toast.makeText(context, "✅ Remote Activation Berhasil! Status Outlet diubah ke PRO PRO-FULL", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Aktivasi PRO Remote (Instan)", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Outlet sudah berstatus PRO Aktif", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Outlet Berstatus PRO Full", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Key Generator for Any Customer Outlet
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generator Kode Aktivasi Customer Remote", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Masukkan Kode Outlet customer yang terpasang di HP mereka untuk buat Kode Lisensi PRO:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = outletInputCode,
                            onValueChange = { outletInputCode = it },
                            label = { Text("Ketik Kode Outlet Customer (Cth: POS-89214)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (outletInputCode.isBlank()) {
                                    Toast.makeText(context, "Masukkan Kode Outlet customer!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                generatedKey = viewModel.generateDeveloperKey(outletInputCode)
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text("Generate Kode Lisensi PRO", fontWeight = FontWeight.Bold)
                        }

                        if (generatedKey.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Kode Aktivasi Untuk Outlet: ${outletInputCode.uppercase()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(generatedKey, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                val clip = android.content.ClipData.newPlainText("Kode Lisensi", generatedKey)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Kode disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Salin Kode", fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                try {
                                                    val msg = "Halo! Ini Kode Aktivasi Lisensi PRO WarungKu POS untuk Kode Outlet Anda (${outletInputCode.uppercase()}):\n\nKode Aktivasi: $generatedKey\n\nSilakan masukkan kode ini di aplikasi untuk membuka status PRO tanpa batas. Terima kasih!"
                                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                        data = android.net.Uri.parse("https://api.whatsapp.com/send?text=${android.net.Uri.encode(msg)}")
                                                    }
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Gagal membuka WhatsApp", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.weight(1.2f),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Kirim WA", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
