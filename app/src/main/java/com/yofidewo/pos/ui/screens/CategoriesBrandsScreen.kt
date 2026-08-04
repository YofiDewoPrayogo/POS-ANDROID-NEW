package com.yofidewo.pos.ui.screens



import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yofidewo.pos.ui.PosViewModel
import com.yofidewo.pos.data.CategoryEntity

import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesBrandsScreen(viewModel: PosViewModel, onBack: (() -> Unit)? = null) {
    var selectedSubTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (onBack != null) {
            TopAppBar(
                title = { Text("Kelola Kategori, Merek & Gudang", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
        SecondaryTabRow(selectedTabIndex = selectedSubTab) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Kategori", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Category, contentDescription = null) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Merek (Brands)", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Factory, contentDescription = null) }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("Gudang", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Store, contentDescription = null) }
            )
        }

        Box(modifier = Modifier.weight(1f).padding(16.dp)) {
            when (selectedSubTab) {
                0 -> CategoriesContent(viewModel)
                1 -> BrandsContent(viewModel)
                2 -> WarehousesContent(viewModel)
            }
        }
    }
}

@Composable
fun CategoriesContent(viewModel: PosViewModel) {
    val categories by viewModel.categories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<CategoryEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Kategori Produk", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "Kelompokkan produk Anda", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kategori Baru")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { cat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(cat.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Kode: ${cat.code} • ${cat.description}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row {
                            IconButton(onClick = { categoryToEdit = cat; showAddDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deleteCategory(cat) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(viewModel = viewModel, categoryToEdit = categoryToEdit, onDismiss = { showAddDialog = false; categoryToEdit = null })
    }
}

@Composable
fun BrandsContent(viewModel: PosViewModel) {
    val brands by viewModel.brands.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Merek (Brands)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "Daftar merek produk", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Merek Baru")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(brands) { brand ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(brand.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(brand.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { viewModel.deleteBrand(brand) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBrandDialog(viewModel = viewModel, onDismiss = { showAddDialog = false })
    }
}

@Composable
fun WarehousesContent(viewModel: PosViewModel) {
    val warehouses by viewModel.warehouses.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Gudang (Warehouses)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "Kelola lokasi penyimpanan stok", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gudang Baru")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(warehouses) { wh ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(wh.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Lokasi: ${wh.location} • Kapasitas: ${wh.capacity}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { viewModel.deleteWarehouse(wh) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddWarehouseDialog(viewModel = viewModel, onDismiss = { showAddDialog = false })
    }
}

@Composable
fun AddCategoryDialog(viewModel: PosViewModel, categoryToEdit: CategoryEntity? = null, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(categoryToEdit?.name ?: "") }
    var code by remember { mutableStateOf(categoryToEdit?.code ?: "") }
    var desc by remember { mutableStateOf(categoryToEdit?.description ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Tambah Kategori", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Kategori") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Kode Kategori") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi Singkat") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal") }
                    Button(onClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (categoryToEdit != null) {
                            viewModel.updateCategory(categoryToEdit!!.copy(name = name, code = code, description = desc))
                        } else {
                            viewModel.addCategory(name, code, desc)
                        }
                        onDismiss()
                    }, modifier = Modifier.weight(1f)) { Text("Simpan") }
                }
            }
        }
    }
}

@Composable
fun AddBrandDialog(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Tambah Merek", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Merek") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal") }
                    Button(onClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addBrand(name, desc)
                        onDismiss()
                    }, modifier = Modifier.weight(1f)) { Text("Simpan") }
                }
            }
        }
    }
}

@Composable
fun AddWarehouseDialog(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var loc by remember { mutableStateOf("") }
    var capStr by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Tambah Gudang", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Gudang") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = loc, onValueChange = { loc = it }, label = { Text("Lokasi") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = capStr, onValueChange = { capStr = it }, label = { Text("Kapasitas") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal") }
                    Button(onClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addWarehouse(name, loc, capStr.toIntOrNull() ?: 1000)
                        onDismiss()
                    }, modifier = Modifier.weight(1f)) { Text("Simpan") }
                }
            }
        }
    }
}
