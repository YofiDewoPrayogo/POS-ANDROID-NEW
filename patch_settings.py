import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write("""package com.yofidewo.pos.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
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
import com.yofidewo.pos.data.RoleEntity
import com.yofidewo.pos.data.UserEntity
import com.yofidewo.pos.ui.PosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsUsersScreen(viewModel: PosViewModel) {
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            icon = Icons.Default.LocalOffer,
                            title = "Kelola Diskon",
                            subtitle = "Atur jenis dan nilai diskon",
                            onClick = { currentSubScreen = "discounts" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Print,
                            title = "Kelola Printer",
                            subtitle = "Atur printer bluetooth & struk",
                            onClick = { currentSubScreen = "printer" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.CloudSync,
                            title = "Sinkronisasi Cloud",
                            subtitle = "Sinkronisasi data (Mock)",
                            onClick = {}
                        )
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
        "printer" -> PrinterSettingsContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
        "discounts" -> DiscountsManagementContent(viewModel = viewModel, onBack = { currentSubScreen = "list" })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountsManagementContent(viewModel: PosViewModel, onBack: () -> Unit = {}) {
    val discounts by viewModel.activeDiscounts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingDiscount by remember { mutableStateOf<com.yofidewo.pos.data.DiscountEntity?>(null) }

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
                            IconButton(onClick = { viewModel.deleteDiscount(disc) }) { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
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
                            IconButton(onClick = { viewModel.deleteUser(user) }) { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
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
    var editingRole by remember { mutableStateOf<RoleEntity?>(null) }
    
    var name by remember { mutableStateOf("") }
    var canEdit by remember { mutableStateOf(false) }
    var canDelete by remember { mutableStateOf(false) }
    var canCreate by remember { mutableStateOf(false) }
    var canManageProducts by remember { mutableStateOf(false) }
    var canViewReports by remember { mutableStateOf(false) }
    var canManageStock by remember { mutableStateOf(false) }
    var canReturnSales by remember { mutableStateOf(false) }

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
                canEdit = false; canDelete = false; canCreate = false; canManageProducts = false; canViewReports = false; canManageStock = false; canReturnSales = false
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
                                canEdit = role.canEdit; canDelete = role.canDelete; canCreate = role.canCreate; canManageProducts = role.canManageProducts; canViewReports = role.canViewReports; canManageStock = role.canManageStock; canReturnSales = role.canReturnSales
                                showDialog = true
                            }) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                            if (role.id != 1L) { // Prevent deleting super admin
                                IconButton(onClick = { viewModel.deleteRole(role) }) { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error) }
                            }
                        }
                    }
                }
            }
        }
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
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canCreate, onCheckedChange = { canCreate = it }); Text("Bisa Buat Data") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canEdit, onCheckedChange = { canEdit = it }); Text("Bisa Edit Data") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canDelete, onCheckedChange = { canDelete = it }); Text("Bisa Hapus Data") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canManageProducts, onCheckedChange = { canManageProducts = it }); Text("Kelola Produk") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewReports, onCheckedChange = { canViewReports = it }); Text("Lihat Laporan") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canManageStock, onCheckedChange = { canManageStock = it }); Text("Kelola Stok") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canReturnSales, onCheckedChange = { canReturnSales = it }); Text("Retur Penjualan") } }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Batal") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (name.isBlank()) return@Button
                            if (editingRole != null) {
                                viewModel.updateRole(editingRole!!.copy(name = name, canEdit = canEdit, canDelete = canDelete, canCreate = canCreate, canManageProducts = canManageProducts, canViewReports = canViewReports, canManageStock = canManageStock, canReturnSales = canReturnSales))
                            } else {
                                viewModel.addRole(name, canEdit, canDelete, canCreate, canManageProducts, canViewReports, canManageStock, canReturnSales)
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
fun PrinterSettingsContent(viewModel: PosViewModel, onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Printer", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)) {
            Text("Mendukung semua model printer Bluetooth & Cash Drawer", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Fitur sinkronisasi perangkat keras sedang dalam pengembangan. Silakan pasangkan printer thermal Bluetooth melalui pengaturan perangkat Android Anda terlebih dahulu.")
        }
    }
}
""")
