package com.yofidewo.pos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yofidewo.pos.ui.PosViewModel
import com.yofidewo.pos.util.QrCodeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutletSetupScreen(
    viewModel: PosViewModel,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    var mode by remember { mutableStateOf("SELECT") } // "SELECT", "CREATE", "JOIN"
    var inputCode by remember { mutableStateOf("") }
    var storeName by remember { mutableStateOf(viewModel.outletName.value) }
    var storeAddress by remember { mutableStateOf(viewModel.outletAddress.value) }
    var storePhone by remember { mutableStateOf(viewModel.outletPhone.value) }
    var isLoading by remember { mutableStateOf(false) }

    val currentOutletCode by viewModel.outletCode.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (mode) {
                "SELECT" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Storefront,
                                    contentDescription = null,
                                    modifier = Modifier.size(44.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "WarungKu POS Real-Time",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "Multi-Device & Cloud Multi-Tenant",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Card(
                            onClick = { mode = "CREATE" },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AddBusiness,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Buat Outlet Baru (HP Owner)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Dapatkan Kode Outlet unik & hubungkan HP kasir lain", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            onClick = { mode = "JOIN" },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Gabung Kode Outlet (HP Kasir)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Masukkan Kode Outlet atau Scan QR Code dari HP Owner", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }

                        if (currentOutletCode.isNotBlank()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            OutlinedButton(
                                onClick = onComplete,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Lanjutkan Dengan Kode Outlet Saat Ini ($currentOutletCode)")
                            }
                        }
                    }
                }

                "CREATE" -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Buat Outlet Baru", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Aplikasi akan mengenerate Kode Outlet Unik untuk Cloud Sync", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = storeName,
                            onValueChange = { storeName = it },
                            label = { Text("Nama Toko / Outlet") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = storeAddress,
                            onValueChange = { storeAddress = it },
                            label = { Text("Alamat Toko") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = storePhone,
                            onValueChange = { storePhone = it },
                            label = { Text("No. HP / WA") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (storeName.isBlank()) {
                                    Toast.makeText(context, "Nama toko wajib diisi!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isLoading = true
                                viewModel.createCloudOutlet(storeName, storeAddress, storePhone) { success, code ->
                                    isLoading = false
                                    if (success) {
                                        Toast.makeText(context, "Outlet berhasil dibuat! Kode: $code", Toast.LENGTH_LONG).show()
                                        onComplete()
                                    } else {
                                        Toast.makeText(context, "Gagal terhubung ke Cloud. Mencoba mode lokal.", Toast.LENGTH_SHORT).show()
                                        onComplete()
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            else Text("Buat & Dapatkan Kode Outlet", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { mode = "SELECT" },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Kembali")
                        }
                    }
                }

                "JOIN" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Gabung Kode Outlet", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Masukkan Kode Outlet yang didapatkan dari HP Owner", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = inputCode,
                            onValueChange = { inputCode = it.uppercase() },
                            label = { Text("Masukkan Kode Outlet (Contoh: POS-88219)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                val trimmed = inputCode.trim()
                                if (trimmed.isBlank()) {
                                    Toast.makeText(context, "Masukkan Kode Outlet valid!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isLoading = true
                                viewModel.joinCloudOutlet(trimmed) { success ->
                                    isLoading = false
                                    if (success) {
                                        Toast.makeText(context, "Berhasil terhubung ke Outlet $trimmed!", Toast.LENGTH_LONG).show()
                                        onComplete()
                                    } else {
                                        Toast.makeText(context, "Kode Outlet $trimmed tidak ditemukan!", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            else Text("Verifikasi & Gabung Outlet", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = { mode = "SELECT" },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Batal / Kembali")
                        }
                    }
                }
            }
        }
    }
}
