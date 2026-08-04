package com.yofidewo.pos.ui.screens

import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Verified
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Warning
import com.yofidewo.pos.ui.components.CustomLogo
import com.yofidewo.pos.data.RoleEntity
import com.yofidewo.pos.data.UserEntity
import com.yofidewo.pos.ui.PosViewModel
import com.yofidewo.pos.util.EscPosPrinterHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsUsersScreen(viewModel: PosViewModel) {
    val context = LocalContext.current
    var currentSubScreen by remember { mutableStateOf("list") }

    when (currentSubScreen) {
        "list" -> {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Pengaturan", fontWeight = FontWeight.Bold) })
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SettingsItem(
                            icon = Icons.Default.ManageAccounts,
                            title = "Kelola Kasir / Pengguna",
                            subtitle = "Tambah atau hapus akun pengguna",
                            onClick = { currentSubScreen = "users" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Security,
                            title = "Kelola Role",
                            subtitle = "Atur hak akses tiap role",
                            onClick = { currentSubScreen = "roles" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Category,
                            title = "Kelola Kategori, Merek & Gudang",
                            subtitle = "Atur kategori produk, merek (brands) & gudang toko",
                            onClick = { currentSubScreen = "categories_brands" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Store,
                            title = "Pengaturan Outlet & Toko",
                            subtitle = "Nama toko, alamat, logo & lisensi",
                            onClick = { currentSubScreen = "outlet" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.CreditCard,
                            title = "Metode Pembayaran",
                            subtitle = "Tambah / atur metode pembayaran",
                            onClick = { currentSubScreen = "payment_methods" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.LocalOffer,
                            title = "Kelola Diskon",
                            subtitle = "Atur jenis dan nilai diskon",
                            onClick = { currentSubScreen = "discounts" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Print,
                            title = "Kelola Printer & Struk",
                            subtitle = "Ukuran kertas (58/80), BT/LAN, Cashdrawer & Struk",
                            onClick = { currentSubScreen = "printer" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Storage,
                            title = "Kelola & Kosongkan Database",
                            subtitle = "Opsi hapus produk/stok/transaksi, restore & ekspor database",
                            onClick = { currentSubScreen = "database" }
                        )
                    }
                    if (com.yofidewo.pos.BuildConfig.IS_DEVELOPER_BUILD) {
                        item {
                            var showMasterPinPrompt by remember { mutableStateOf(false) }
                            var masterPinInput by remember { mutableStateOf("") }

                            SettingsItem(
                                icon = Icons.Default.Security,
                                title = "Konsol Super Admin Developer",
                                subtitle = "Manajemen terpusat, remote activation PRO & generator lisensi",
                                onClick = { showMasterPinPrompt = true }
                            )

                            if (showMasterPinPrompt) {
                                AlertDialog(
                                    onDismissRequest = { showMasterPinPrompt = false },
                                    title = { Text("Verifikasi Developer Master", fontWeight = FontWeight.Bold) },
                                    text = {
                                        Column {
                                            Text("Khusus Akun Developer: yofidewo4@gmail.com", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Masukkan PIN Master Developer untuk masuk ke Konsol Super Admin:", fontSize = 12.sp)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(
                                                value = masterPinInput,
                                                onValueChange = { masterPinInput = it },
                                                label = { Text("PIN Master Developer (911911)") },
                                                singleLine = true,
                                                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                                            )
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                if (masterPinInput.trim() == "911911") {
                                                    showMasterPinPrompt = false
                                                    currentSubScreen = "super_admin"
                                                    Toast.makeText(context, "Akses Developer yofidewo4@gmail.com Terverifikasi ✅", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "❌ PIN Master Developer Salah! Khusus akun yofidewo4@gmail.com", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Text("Masuk Konsol")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showMasterPinPrompt = false }) { Text("Batal") }
                                    }
                                )
                            }
                        }
                    }
                    item {
                        var showTrialDialog by remember { mutableStateOf(false) }
                        val isActivated by viewModel.isActivated.collectAsState()
                        val storeLicense by viewModel.storeLicense.collectAsState()

                        SettingsItem(
                            icon = Icons.Default.VpnKey,
                            title = "Aktivasi Lisensi / Status",
                            subtitle = if (isActivated) "Aplikasi Terlisensi PRO (Aktif)" else "Masukkan atau pilih kode aktivasi",
                            onClick = { showTrialDialog = true }
                        )

                        if (showTrialDialog) {
                            if (isActivated) {
                                AlertDialog(
                                    onDismissRequest = { showTrialDialog = false },
                                    icon = {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(52.dp)
                                        )
                                    },
                                    title = {
                                        Text(
                                            "Aplikasi Sudah Terlisensi! ✅",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    },
                                    text = {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                "Status: PRO FULL VERSION (Aktif Selamanya)",
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF065F46)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                "Kode Lisensi Terdaftar:",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Surface(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.padding(top = 4.dp)
                                            ) {
                                                Text(
                                                    storeLicense,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 16.sp,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = { showTrialDialog = false },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                        ) {
                                            Text("Tutup", fontWeight = FontWeight.Bold)
                                        }
                                    },
                                    shape = RoundedCornerShape(20.dp)
                                )
                            } else {
                                var activationCode by remember { mutableStateOf("") }
                                val validCodes = listOf(
                                    "WK-POS-2026-PRO" to "Lisensi Pro Utama WarungKu",
                                    "WARUNGKU-PRO-8899" to "Lisensi Toko Retail",
                                    "POS-2026-PRO" to "Lisensi Multi-Outlet Enterprise",
                                    "ADMIN-123" to "Lisensi Developer Master"
                                )

                                Dialog(onDismissRequest = { showTrialDialog = false }) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        shadowElevation = 8.dp
                                    ) {
                                        Column(modifier = Modifier.padding(20.dp)) {
                                            Text(
                                                "Aktivasi Lisensi WarungKu POS",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))

                                            OutlinedTextField(
                                                value = activationCode,
                                                onValueChange = { activationCode = it },
                                                label = { Text("Masukkan Kode Lisensi") },
                                                placeholder = { Text("Contoh: REG-POS-2026-8899") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            OutlinedButton(
                                                onClick = {
                                                    try {
                                                        val outletCodeVal = viewModel.outletCode.value
                                                        val currentCode = if (outletCodeVal.isBlank()) "POS-LOCAL" else outletCodeVal
                                                        val msg = "Halo Admin WarungKu POS, saya mau beli Kode Aktivasi PRO untuk Kode Outlet: $currentCode"
                                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                            data = android.net.Uri.parse("https://api.whatsapp.com/send?phone=6281929491887&text=${android.net.Uri.encode(msg)}")
                                                        }
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Gagal membuka WhatsApp", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("💬 Hubungi Admin WA (081929491887)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))
                                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                                TextButton(onClick = { showTrialDialog = false }) { Text("Batal") }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Button(
                                                    onClick = {
                                                        val success = viewModel.activateProKey(activationCode.trim())
                                                        if (success) {
                                                            Toast.makeText(context, "Aplikasi Berhasil Diaktivasi PRO! ✅", Toast.LENGTH_LONG).show()
                                                            showTrialDialog = false
                                                        } else {
                                                            Toast.makeText(context, "Kode lisensi tidak valid untuk Kode Outlet ini.", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                ) {
                                                    Text("Aktivasi Sekarang")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Keluar (Logout)")
                        }
                    }
                }
            }
        }
        "users" -> UsersManagementContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
        "roles" -> RolesManagementContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
        "categories_brands" -> CategoriesBrandsScreen(viewModel = viewModel, onBack = { currentSubScreen = "list" })
        "outlet" -> OutletSettingsContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
        "payment_methods" -> PaymentMethodsSettingsContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
        "printer" -> PrinterSettingsContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
        "discounts" -> DiscountsManagementContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
        "database" -> DatabaseManagementContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
        "super_admin" -> SuperAdminScreen(viewModel = viewModel, onBack = { currentSubScreen = "list" })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp, pressedElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                    if (badgeText != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountsManagementContent(viewModel: PosViewModel, onBack: () -> Unit = {}) {
    val discounts by viewModel.activeDiscounts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var editingDiscount by remember { mutableStateOf<com.yofidewo.pos.data.DiscountEntity?>(null) }
    var pendingDeleteDisc by remember { mutableStateOf<com.yofidewo.pos.data.DiscountEntity?>(null) }

    var nameInput by remember { mutableStateOf("") }
    var typeInput by remember { mutableStateOf("PERCENT") }
    var valueInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Diskon", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingDiscount = null
                nameInput = ""
                typeInput = "PERCENT"
                valueInput = ""
                showDialog = true
            }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)) {
            items(discounts) { disc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = disc.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                text = if (disc.type == "PERCENT") "${disc.value}%" else viewModel.formatMoney(disc.value),
                                fontSize = 14.sp, color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row {
                            IconButton(onClick = {
                                editingDiscount = disc
                                nameInput = disc.name
                                typeInput = disc.type
                                valueInput = disc.value.toString()
                                showDialog = true
                            }) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                            IconButton(onClick = { pendingDeleteDisc = disc }) { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }

    pendingDeleteDisc?.let { disc ->
        AlertDialog(
            onDismissRequest = { pendingDeleteDisc = null },
            title = { Text("Konfirmasi Aksi", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin mengubah/menghapus data ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDiscount(disc)
                        pendingDeleteDisc = null
                        Toast.makeText(context, "Diskon berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Ya, Lanjutkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteDisc = null }) { Text("Batal") }
            }
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (editingDiscount == null) "Tambah Diskon" else "Edit Diskon",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nameInput, onValueChange = { nameInput = it },
                        label = { Text("Nama Diskon") }, modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Tipe Diskon", style = MaterialTheme.typography.labelLarge)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                            color = if (typeInput == "PERCENT") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            onClick = { typeInput = "PERCENT" },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) { Text("Persentase (%)") }
                        }
                        Surface(
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                            color = if (typeInput == "FIXED") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            onClick = { typeInput = "FIXED" },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) { Text("Nominal (Rp)") }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = valueInput, onValueChange = { valueInput = it },
                        label = { Text(if (typeInput == "PERCENT") "Nilai Diskon (%)" else "Nilai Diskon (Rp)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Batal") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            val v = valueInput.toDoubleOrNull() ?: 0.0
                            if (editingDiscount == null) {
                                viewModel.insertDiscount(com.yofidewo.pos.data.DiscountEntity(name = nameInput, type = typeInput, value = v))
                            } else {
                                viewModel.updateDiscount(editingDiscount!!.copy(name = nameInput, type = typeInput, value = v))
                            }
                            showDialog = false
                        }) { Text("Simpan") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersManagementContent(viewModel: PosViewModel, onBack: () -> Unit = {}) {
    val users by viewModel.users.collectAsState()
    val roles by viewModel.roles.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserEntity?>(null) }
    var pendingDeleteUser by remember { mutableStateOf<UserEntity?>(null) }
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var roleId by remember { mutableStateOf<Long?>(null) }
    
    var expandedRole by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Kasir", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingUser = null
                name = ""
                email = ""
                pin = ""
                roleId = roles.firstOrNull()?.id
                showDialog = true
            }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)) {
            items(users) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = user.name, fontWeight = FontWeight.Bold)
                            Text(text = user.email)
                            Text(text = "Role: ${roles.find { it.id == user.roleId }?.name ?: "Unknown"}", color = MaterialTheme.colorScheme.primary)
                        }
                        Row {
                            IconButton(onClick = {
                                editingUser = user
                                name = user.name
                                email = user.email
                                pin = user.pin
                                roleId = user.roleId
                                showDialog = true
                            }) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                            IconButton(onClick = { pendingDeleteUser = user }) { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }

    pendingDeleteUser?.let { u ->
        AlertDialog(
            onDismissRequest = { pendingDeleteUser = null },
            title = { Text("Konfirmasi Aksi", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin mengubah/menghapus data ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(u)
                        pendingDeleteUser = null
                        Toast.makeText(context, "Pengguna berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Ya, Lanjutkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteUser = null }) { Text("Batal") }
            }
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(if (editingUser == null) "Tambah Pengguna" else "Edit Pengguna", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email/Username") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pin, onValueChange = { pin = it }, label = { Text("PIN") }, singleLine = true, 
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(expanded = expandedRole, onExpandedChange = { expandedRole = !expandedRole }) {
                        OutlinedTextField(
                            value = roles.find { it.id == roleId }?.name ?: "Pilih Role",
                            onValueChange = {}, readOnly = true, label = { Text("Role") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expandedRole, onDismissRequest = { expandedRole = false }) {
                            roles.forEach { role ->
                                DropdownMenuItem(text = { Text(role.name) }, onClick = { roleId = role.id; expandedRole = false })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Batal") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (name.isBlank() || email.isBlank() || pin.isBlank() || roleId == null) {
                                Toast.makeText(context, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (editingUser != null) {
                                viewModel.updateUser(editingUser!!.copy(name = name, email = email, pin = pin, roleId = roleId))
                            } else {
                                viewModel.addUser(name, email, pin, roleId!!)
                            }
                            showDialog = false
                        }) { Text("Simpan") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolesManagementContent(viewModel: PosViewModel, onBack: () -> Unit = {}) {
    val roles by viewModel.roles.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var editingRole by remember { mutableStateOf<RoleEntity?>(null) }
    var pendingDeleteRole by remember { mutableStateOf<RoleEntity?>(null) }
    
    var name by remember { mutableStateOf("") }
    var canViewDashboard by remember { mutableStateOf(false) }
    var canViewCashier by remember { mutableStateOf(false) }
    var canViewProducts by remember { mutableStateOf(false) }
    var canViewReports by remember { mutableStateOf(false) }
    var canViewSettings by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Role", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingRole = null
                name = ""
                canViewDashboard = false; canViewCashier = false; canViewProducts = false; canViewReports = false; canViewSettings = false
                showDialog = true
            }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)) {
            items(roles) { role ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = role.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Row {
                            IconButton(onClick = {
                                editingRole = role
                                name = role.name
                                canViewDashboard = role.canViewDashboard; canViewCashier = role.canViewCashier; canViewProducts = role.canViewProducts; canViewReports = role.canViewReports; canViewSettings = role.canViewSettings
                                showDialog = true
                            }) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                            if (role.id != 1L) { // Prevent deleting super admin
                                IconButton(onClick = { pendingDeleteRole = role }) { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error) }
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDeleteRole?.let { r ->
        AlertDialog(
            onDismissRequest = { pendingDeleteRole = null },
            title = { Text("Konfirmasi Aksi", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin mengubah/menghapus data ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteRole(r)
                        pendingDeleteRole = null
                        Toast.makeText(context, "Role berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Ya, Lanjutkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteRole = null }) { Text("Batal") }
            }
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(if (editingRole == null) "Tambah Role" else "Edit Role", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Role") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewDashboard, onCheckedChange = { canViewDashboard = it }); Text("Akses Dashboard") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewCashier, onCheckedChange = { canViewCashier = it }); Text("Akses Kasir") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewProducts, onCheckedChange = { canViewProducts = it }); Text("Akses Produk (Maintenance Item)") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewReports, onCheckedChange = { canViewReports = it }); Text("Akses Laporan") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewSettings, onCheckedChange = { canViewSettings = it }); Text("Akses Pengaturan") } }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Batal") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (name.isBlank()) return@Button
                            if (editingRole != null) {
                                viewModel.updateRole(editingRole!!.copy(name = name, canViewDashboard = canViewDashboard, canViewCashier = canViewCashier, canViewProducts = canViewProducts, canViewReports = canViewReports, canViewSettings = canViewSettings))
                            } else {
                                viewModel.addRole(name, canViewDashboard, canViewCashier, canViewProducts, canViewReports, canViewSettings)
                            }
                            showDialog = false
                        }) { Text("Simpan") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutletSettingsContent(viewModel: PosViewModel, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(viewModel.outletName.value) }
    var address by remember { mutableStateOf(viewModel.outletAddress.value) }
    var phone by remember { mutableStateOf(viewModel.outletPhone.value) }
    var license by remember { mutableStateOf(viewModel.storeLicense.value) }

    val customLogoBitmap by viewModel.customLogoBitmap.collectAsState()

    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.saveAndSetCustomLogo(context, it)
            Toast.makeText(context, "Logo outlet berhasil diubah & dikompresi!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Outlet & Toko", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") } }
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
            item {
                Text("Informasi Profil Outlet & Logo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CustomLogo(modifier = Modifier.size(64.dp), bitmap = customLogoBitmap)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Logo Outlet WarungKu", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("Dukungan format JPEG, PNG, JPG (Otomatis Kompres & Scale)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { logoPickerLauncher.launch("image/*") },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pilih / Ubah Logo", fontSize = 13.sp)
                            }
                            if (customLogoBitmap != null) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.resetCustomLogo()
                                        Toast.makeText(context, "Logo outlet dikembalikan ke default", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Reset Default", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            item {
                val outletCode by viewModel.outletCode.collectAsState()
                val deviceRole by viewModel.deviceRole.collectAsState()
                val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Kode Outlet Multi-Device (Cloud)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    if (outletCode.isNotBlank()) "Status: Terhubung ($deviceRole)" else "Status: Belum Terhubung Cloud",
                                    fontSize = 12.sp,
                                    color = if (isCloudSyncing) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (outletCode.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                val qrBitmap = remember(outletCode) { com.yofidewo.pos.util.QrCodeUtils.generateQrCodeBitmap(outletCode, 200, 200) }
                                if (qrBitmap != null) {
                                    androidx.compose.foundation.Image(
                                        bitmap = qrBitmap.asImageBitmap(),
                                        contentDescription = "QR Kode Outlet",
                                        modifier = Modifier.size(90.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Kode Outlet Anda:", fontSize = 12.sp)
                                    Text(outletCode, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                val clip = android.content.ClipData.newPlainText("Kode Outlet", outletCode)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Kode Outlet disalin!", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Salin Kode", fontSize = 11.sp)
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.disconnectCloudOutlet()
                                                Toast.makeText(context, "Cloud Sync terputus.", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("Putuskan", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (name.isBlank() || address.isBlank() || phone.isBlank()) {
                                        Toast.makeText(context, "Nama toko, Alamat, dan No. HP/WA WAJIB diisi!", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    viewModel.createCloudOutlet(name, address, phone) { success, code ->
                                        if (success) {
                                            Toast.makeText(context, "Berhasil membuat Kode Outlet: $code", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "Gagal membuat Kode Outlet Cloud", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Buat Kode Outlet Real-time Baru")
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Toko / Outlet") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Alamat Lengkap Toko") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Nomor Telepon / WA") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    val currentFirebaseUrl by viewModel.firebaseUrl.collectAsState()
                    var customFirebaseUrlInput by remember(currentFirebaseUrl) { mutableStateOf(currentFirebaseUrl) }

                    OutlinedTextField(
                        value = customFirebaseUrlInput,
                        onValueChange = { 
                            customFirebaseUrlInput = it 
                            viewModel.updateFirebaseUrl(it)
                        },
                        label = { Text("URL Server Cloud Firebase Realtime DB") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    val isProUnlocked by viewModel.isProUnlocked.collectAsState()
                    val currentLicenseKey by viewModel.storeLicense.collectAsState()

                    OutlinedTextField(
                        value = if (isProUnlocked && currentLicenseKey.isNotBlank()) currentLicenseKey else "Belum Diaktivasi (Mode Trial 30x Transaksi)",
                        onValueChange = { },
                        enabled = false,
                        readOnly = true,
                        label = { Text("Kode Lisensi / Registrasi Toko (Otomatis & Terkunci)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = if (isProUnlocked) MaterialTheme.colorScheme.primary else Color(0xFFD32F2F),
                            disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    if (com.yofidewo.pos.BuildConfig.IS_DEVELOPER_BUILD) {
                        // Card Khusus Developer: Generator Kode Aktivasi Customer (Dilindungi PIN Master Developer)
                        var devInputOutletCode by remember { mutableStateOf("") }
                        var generatedDevKey by remember { mutableStateOf("") }
                        var isDevUnlocked by remember { mutableStateOf(false) }
                        var devPinInput by remember { mutableStateOf("") }
                        var showDevPinDialog by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tool Developer: Generator Kode Aktivasi PRO", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Gunakan tool ini untuk membuat Kode Aktivasi Lisensi PRO khusus untuk Kode Outlet customer.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f))
                                Spacer(modifier = Modifier.height(10.dp))

                                if (!isDevUnlocked) {
                                    Button(
                                        onClick = { showDevPinDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Buka Tool Developer (Input PIN Master)", fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    OutlinedTextField(
                                        value = devInputOutletCode,
                                        onValueChange = { devInputOutletCode = it },
                                        label = { Text("Ketik Kode Outlet Customer (Cth: POS-89214)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            if (devInputOutletCode.isBlank()) {
                                                Toast.makeText(context, "Masukkan Kode Outlet customer", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            generatedDevKey = viewModel.generateDeveloperKey(devInputOutletCode)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Buat Kode Aktivasi PRO")
                                    }

                                    if (generatedDevKey.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                            Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Kode Lisensi Hasil Generate:", fontSize = 11.sp)
                                                Text(generatedDevKey, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                OutlinedButton(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                        val clip = android.content.ClipData.newPlainText("Kode Lisensi PRO", generatedDevKey)
                                                        clipboard.setPrimaryClip(clip)
                                                        Toast.makeText(context, "Kode Lisensi PRO berhasil disalin!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Salin Kode Ke Clipboard", fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (showDevPinDialog) {
                            AlertDialog(
                                onDismissRequest = { showDevPinDialog = false },
                                title = { Text("Verifikasi Developer Master", fontWeight = FontWeight.Bold) },
                                text = {
                                    Column {
                                        Text("Masukkan PIN Master Developer untuk mengakses tool pembuat lisensi:", fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = devPinInput,
                                            onValueChange = { devPinInput = it },
                                            label = { Text("PIN Master Developer") },
                                            singleLine = true,
                                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            if (devPinInput == "889977" || devPinInput == "9999") {
                                                isDevUnlocked = true
                                                showDevPinDialog = false
                                                Toast.makeText(context, "Akses Developer Terverifikasi ✅", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "❌ PIN Developer Salah!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Text("Verifikasi")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDevPinDialog = false }) { Text("Batal") }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.outletName.value = name
                            viewModel.outletAddress.value = address
                            viewModel.outletPhone.value = phone
                            viewModel.storeLicense.value = license
                            Toast.makeText(context, "Pengaturan outlet berhasil disimpan!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Simpan Perubahan Outlet", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsSettingsContent(viewModel: PosViewModel, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val methods by viewModel.customPaymentMethods.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newMethodName by remember { mutableStateOf("") }
    var pendingDeleteMethod by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metode Pembayaran", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                newMethodName = ""
                showDialog = true
            }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Daftar Metode Pembayaran Aktif", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            }
            items(methods) { method ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(method, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        if (methods.size > 1 && method != "Tunai (Cash)") {
                            IconButton(onClick = { pendingDeleteMethod = method }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDeleteMethod?.let { m ->
        AlertDialog(
            onDismissRequest = { pendingDeleteMethod = null },
            title = { Text("Konfirmasi Aksi", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin mengubah/menghapus data ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePaymentMethod(m)
                        pendingDeleteMethod = null
                        Toast.makeText(context, "Metode pembayaran $m berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Ya, Lanjutkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteMethod = null }) { Text("Batal") }
            }
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Tambah Metode Pembayaran", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newMethodName,
                        onValueChange = { newMethodName = it },
                        label = { Text("Nama Metode (mis. GoPay, ShopeePay, EDC BCA)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Batal") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (newMethodName.isNotBlank()) {
                                viewModel.addPaymentMethod(newMethodName)
                                Toast.makeText(context, "Metode pembayaran ditambahkan", Toast.LENGTH_SHORT).show()
                                showDialog = false
                            }
                        }) { Text("Simpan") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterSettingsContent(viewModel: PosViewModel, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var paperWidth by remember { mutableStateOf(viewModel.paperWidth.value) }
    var connectionType by remember { mutableStateOf(viewModel.connectionType.value) }
    var networkIp by remember { mutableStateOf(viewModel.networkIp.value) }
    var useCashDrawer by remember { mutableStateOf(viewModel.useCashDrawer.value) }
    var headerText by remember { mutableStateOf(viewModel.receiptHeader.value) }
    var useHeaderLogo by remember { mutableStateOf(viewModel.useHeaderLogo.value) }
    var footerText by remember { mutableStateOf(viewModel.receiptFooter.value) }

    var isSearching by remember { mutableStateOf(false) }
    val selectedPrinterName by viewModel.selectedPrinterName.collectAsState()
    val selectedPrinterAddress by viewModel.selectedPrinterAddress.collectAsState()

    val bluetoothAdapter: android.bluetooth.BluetoothAdapter? = remember { android.bluetooth.BluetoothAdapter.getDefaultAdapter() }
    val pairedDevices = remember(isSearching) {
        try {
            bluetoothAdapter?.bondedDevices?.map { "${it.name ?: "Printer BT"} (${it.address})" } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    // Hanya tampilkan paired devices sungguhan — hapus mock
    val displayPrinters = pairedDevices

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Printer & Struk", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") } }
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
            // Section 1: Connection & Printer
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Koneksi Printer Bluetooth Thermal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Mendukung seluruh printer thermal ESC/POS (XSERIES, BLUEPRINT, BTII, BTIIZ, Zjiang, Panda, Mobile Printer 58mm/80mm).",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = connectionType == "Bluetooth",
                                onClick = { connectionType = "Bluetooth" },
                                label = { Text("Bluetooth") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = connectionType == "LAN / Network",
                                onClick = { connectionType = "LAN / Network" },
                                label = { Text("LAN / Network") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (connectionType == "LAN / Network") {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = networkIp,
                                onValueChange = { networkIp = it },
                                label = { Text("IP Address Printer (mis. 192.168.1.200)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                            if (selectedPrinterAddress.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Status: Terhubung", color = Color(0xFF065F46), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(selectedPrinterName, fontWeight = FontWeight.Bold)
                                        Text(selectedPrinterAddress, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    OutlinedButton(onClick = { viewModel.setBluetoothPrinter("Belum Ada Printer Dipilih", "") }) { Text("Putuskan") }
                                }
                            } else {
                                Button(
                                    onClick = { isSearching = !isSearching },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (isSearching) "Sembunyikan Perangkat" else "Pindai Perangkat Bluetooth")
                                }
                            }

                            if (isSearching || selectedPrinterAddress.isBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Pilih Printer Bluetooth Terpasang:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                displayPrinters.forEach { pFull ->
                                    val parts = pFull.split(" (")
                                    val pName = parts[0]
                                    val pAddress = if (parts.size > 1) parts[1].replace(")", "") else "00:11:22:33:44:55"

                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                                            viewModel.setBluetoothPrinter(pName, pAddress)
                                            isSearching = false
                                            Toast.makeText(context, "Terhubung ke $pName", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(pName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text(pAddress, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Icon(Icons.Default.Bluetooth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 2b: Test Printer & Cash Drawer
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val scope = rememberCoroutineScope()
                        Text("Test Printer & Cash Drawer", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Kirim perintah test ke printer yang dipilih untuk verifikasi koneksi.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Test Cetak
                            Button(
                                onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        val result = if (connectionType == "LAN / Network" && networkIp.isNotBlank()) {
                                            EscPosPrinterHelper.testPrintNetwork(
                                                ip = networkIp,
                                                storeName = viewModel.outletName.value
                                            )
                                        } else if (selectedPrinterAddress.isNotBlank()) {
                                            EscPosPrinterHelper.testPrintBluetooth(
                                                deviceAddress = selectedPrinterAddress,
                                                storeName = viewModel.outletName.value
                                            )
                                        } else {
                                            Result.failure(Exception("Pilih printer terlebih dahulu"))
                                        }
                                        kotlinx.coroutines.withContext(Dispatchers.Main) {
                                            if (result.isSuccess) {
                                                Toast.makeText(context, "Test Print berhasil dikirim!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Gagal: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test Cetak", fontSize = 13.sp)
                            }

                            // Test Cash Drawer
                            OutlinedButton(
                                onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        val result = if (connectionType == "LAN / Network" && networkIp.isNotBlank()) {
                                            EscPosPrinterHelper.testCashDrawerNetwork(ip = networkIp)
                                        } else if (selectedPrinterAddress.isNotBlank()) {
                                            EscPosPrinterHelper.testCashDrawerBluetooth(deviceAddress = selectedPrinterAddress)
                                        } else {
                                            Result.failure(Exception("Pilih printer terlebih dahulu"))
                                        }
                                        kotlinx.coroutines.withContext(Dispatchers.Main) {
                                            if (result.isSuccess) {
                                                Toast.makeText(context, "Laci uang berhasil dibuka!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Gagal: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test Laci", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Section 3: Paper Size & Cash Drawer
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ukuran Kertas & Hardware", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Ukuran Kertas Thermal Struk:", fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                            FilterChip(
                                selected = paperWidth == "58mm",
                                onClick = { paperWidth = "58mm" },
                                label = { Text("58 mm (Standard)") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = paperWidth == "80mm",
                                onClick = { paperWidth = "80mm" },
                                label = { Text("80 mm (Wide)") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Gunakan Cash Drawer (Laci Uang)", fontWeight = FontWeight.SemiBold)
                                Text("Buka laci otomatis saat cetak struk", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = useCashDrawer, onCheckedChange = { useCashDrawer = it })
                        }
                    }
                }
            }

            // Section 3: Header & Footer Struk
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Kustomisasi Tampilan Struk", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Gunakan Logo Outlet di Header Struk", fontWeight = FontWeight.Medium)
                                Text("Mengambil logo dari Pengaturan Outlet", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = useHeaderLogo, onCheckedChange = { useHeaderLogo = it })
                        }

                        if (useHeaderLogo) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color.LightGray),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CustomLogo(modifier = Modifier.size(44.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(viewModel.outletName.value, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                    Text(headerText, fontSize = 11.sp, color = Color.DarkGray)
                                    Text("-----------------------------", fontSize = 10.sp, color = Color.Gray)
                                    Text("[ Preview Logo & Header Struk ]", fontSize = 10.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = headerText,
                            onValueChange = { headerText = it },
                            label = { Text("Pesan Header Struk") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = footerText,
                            onValueChange = { footerText = it },
                            label = { Text("Pesan Footer Struk (Bawah Struk)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Save Action
            item {
                Button(
                    onClick = {
                        viewModel.paperWidth.value = paperWidth
                        viewModel.connectionType.value = connectionType
                        viewModel.networkIp.value = networkIp
                        viewModel.useCashDrawer.value = useCashDrawer
                        viewModel.receiptHeader.value = headerText
                        viewModel.useHeaderLogo.value = useHeaderLogo
                        viewModel.receiptFooter.value = footerText
                        Toast.makeText(context, "Pengaturan printer & struk berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Simpan Pengaturan Printer", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseManagementContent(viewModel: PosViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedClearOption by remember { mutableStateOf("PRODUCTS_ONLY") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var jsonRestoreText by remember { mutableStateOf("") }
    var showRestoreDialog by remember { mutableStateOf(false) }

    val clearOptionLabels = mapOf(
        "PRODUCTS_ONLY" to ("Hapus Seluruh Produk Saja" to "Menghapus seluruh daftar barang dan stok produk dari toko."),
        "STOCKS_ONLY" to ("Hapus / Reset Seluruh Stok Saja" to "Mengubah jumlah stok seluruh produk menjadi 0 pcs."),
        "TRANSACTIONS_ONLY" to ("Hapus Riwayat Transaksi Saja" to "Menghapus seluruh transaksi penjualan dan item riwayat."),
        "ALL_DATA" to ("Kosongkan Seluruh Data Toko" to "Menghapus seluruh produk, transaksi, dan log penerimaan stok secara total.")
    )

    val deviceRole by viewModel.deviceRole.collectAsState()
    var showPinDialog by remember { mutableStateOf(false) }
    var pinVerifiedAction by remember { mutableStateOf<(() -> Unit)?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola & Kosongkan Database", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Kosongkan Database
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kosongkan / Reset Database", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Pilih opsi data yang ingin dihapus. Perhatian: Tindakan ini tidak dapat dibatalkan!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        clearOptionLabels.forEach { (key, pair) ->
                            val (title, subtitle) = pair
                            val isSelected = selectedClearOption == key
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedClearOption = key },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.error else Color.LightGray),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedClearOption = key },
                                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.error)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (isSelected) MaterialTheme.colorScheme.error else Color.Unspecified)
                                        Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (deviceRole == "KASIR") {
                                    pinVerifiedAction = { showConfirmDialog = true }
                                    showPinDialog = true
                                } else {
                                    showConfirmDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kosongkan Database Terpilih", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }


            // Card 2: Backup & Ekspor
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cadangkan & Ekspor Database", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                        }
                        Text("Simpan salinan database toko atau ekspor daftar barang ke file.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.exportFullBackup(context) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Ekspor Database (JSON)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { viewModel.exportProductsToCsv(context) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Ekspor Produk (Excel)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Card 3: Restore / Pulihkan Database
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Upload, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Restore / Pulihkan Database", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, fontSize = 16.sp)
                        }
                        Text("Kembalikan data dari berkas cadangan JSON yang telah disimpan.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Button(
                            onClick = { showRestoreDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pulihkan Data / Import JSON", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    var pendingJsonRestoreContent by remember { mutableStateOf("") }
    var pendingMetadata by remember { mutableStateOf<com.yofidewo.pos.util.BackupMetadata?>(null) }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }

    val jsonFilePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val jsonString = inputStream?.bufferedReader()?.use { reader -> reader.readText() } ?: ""
                if (jsonString.isNotBlank()) {
                    val meta = com.yofidewo.pos.util.DataExporter.parseBackupMetadata(jsonString)
                    if (meta != null) {
                        pendingJsonRestoreContent = jsonString
                        pendingMetadata = meta
                        showRestoreConfirmDialog = true
                    } else {
                        Toast.makeText(context, "Format berkas cadangan JSON tidak valid / rusak", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membaca berkas: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Confirmation Dialog for Clear Database
    if (showConfirmDialog) {
        val labelInfo = clearOptionLabels[selectedClearOption]?.first ?: "Data Terpilih"
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(36.dp)) },
            title = { Text("Konfirmasi Kosongkan Data") },
            text = { Text("Apakah Anda yakin ingin melakukan: \"$labelInfo\"? Action ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearDatabaseOption(selectedClearOption) {
                            Toast.makeText(context, "Proses penghapusan database berhasil dilakukan!", Toast.LENGTH_LONG).show()
                        }
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Hapus Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Batal") }
            }
        )
    }

    // Restore Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Pulihkan / Restore Database") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            jsonFilePickerLauncher.launch("*/*")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pilih File .json Dari HP / Drive", fontWeight = FontWeight.Bold)
                    }

                    Text("— Atau tempel teks JSON manual di bawah ini —", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    OutlinedTextField(
                        value = jsonRestoreText,
                        onValueChange = { jsonRestoreText = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("{\"products\": [...], ...}") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (jsonRestoreText.isBlank()) {
                            Toast.makeText(context, "Pilih berkas JSON atau tempel teks JSON terlebih dahulu", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val meta = com.yofidewo.pos.util.DataExporter.parseBackupMetadata(jsonRestoreText)
                        if (meta != null) {
                            pendingJsonRestoreContent = jsonRestoreText
                            pendingMetadata = meta
                            showRestoreConfirmDialog = true
                        } else {
                            Toast.makeText(context, "Format teks JSON tidak valid / rusak", Toast.LENGTH_LONG).show()
                        }
                    }
                ) {
                    Text("Periksa & Lanjutkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) { Text("Batal") }
            }
        )
    }

    // Restore Confirmation Dialog with File Metadata
    if (showRestoreConfirmDialog && pendingMetadata != null) {
        val meta = pendingMetadata!!
        val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        AlertDialog(
            onDismissRequest = { showRestoreConfirmDialog = false },
            icon = { Icon(Icons.Default.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp)) },
            title = { Text("Konfirmasi Pemulihan Data Toko") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Rincian Berkas Cadangan Ditemukan:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("📅 Tanggal Cadangan: ${df.format(Date(meta.timestamp))}", fontSize = 12.sp)
                    Text("📦 Jumlah Produk: ${meta.productCount} Item", fontSize = 12.sp)
                    Text("📁 Jumlah Kategori: ${meta.categoryCount} Kategori", fontSize = 12.sp)
                    Text("🏷️ Jumlah Diskon: ${meta.discountCount} Diskon", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Apakah Anda yakin ingin memulihkan data ini? Data toko akan diperbarui dengan berkas ini.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRestoreConfirmDialog = false
                        showRestoreDialog = false
                        viewModel.restoreFullBackup(context, pendingJsonRestoreContent) { success ->
                            if (success) {
                                Toast.makeText(context, "Database Berhasil Dipulihkan 100%! ✅", Toast.LENGTH_LONG).show()
                                pendingJsonRestoreContent = ""
                                jsonRestoreText = ""
                            } else {
                                Toast.makeText(context, "Gagal memulihkan database. Format JSON rusak.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text("Ya, Mulai Pemulihan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirmDialog = false }) { Text("Batal") }
            }
        )
    }

    // Animated Fullscreen Progress Loading Dialog for Heavy Restore
    if (viewModel.isRestoringData.value) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { },
            properties = androidx.compose.ui.window.DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(52.dp), color = MaterialTheme.colorScheme.primary)
                    Text("Memproses Pemulihan Database...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(viewModel.restoreProgressText.value, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    
                    val cur = viewModel.restoreProgressCurrent.value
                    val tot = viewModel.restoreProgressTotal.value
                    val pct = if (tot > 0) (cur.toFloat() / tot.toFloat()) else 0f
                    LinearProgressIndicator(progress = pct, modifier = Modifier.fillMaxWidth())
                    
                    Text("⚠️ PENTING: Mohon tunggu dan JANGAN tutup aplikasi agar data tidak rusak.", fontSize = 11.sp, color = MaterialTheme.colorScheme.error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }

    // PIN Dialog for Kasir verification
    if (showPinDialog) {
        OwnerPinDialog(
            onDismiss = { showPinDialog = false },
            onSuccess = {
                showPinDialog = false
                pinVerifiedAction?.invoke()
            }
        )
    }
}

@Composable
fun OwnerPinDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Verifikasi PIN Owner", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Fitur ini memerlukan verifikasi PIN Owner / Admin (Default: 1234).", fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("PIN Owner") },
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (pin == "1234") {
                    onSuccess()
                } else {
                    Toast.makeText(context, "PIN Owner Salah!", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Verifikasi PIN")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

