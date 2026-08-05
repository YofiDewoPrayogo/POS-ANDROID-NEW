package com.yofidewo.pos.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yofidewo.pos.data.BrandEntity
import com.yofidewo.pos.data.CategoryEntity
import com.yofidewo.pos.data.ProductEntity
import com.yofidewo.pos.data.WarehouseEntity
import com.yofidewo.pos.ui.PosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: PosViewModel, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val brands by viewModel.brands.collectAsState()
    val warehouses by viewModel.warehouses.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showCategoriesModal by remember { mutableStateOf(false) }

    val filteredProducts = products.filter {
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.code.contains(searchQuery, ignoreCase = true) ||
        it.barcode.contains(searchQuery, ignoreCase = true)
    }

    if (showCategoriesModal) {
        Dialog(
            onDismissRequest = { showCategoriesModal = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                CategoriesBrandsScreen(viewModel = viewModel, onBack = { showCategoriesModal = false })
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produk & Stok", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    productToEdit = null
                    showAddDialog = true 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Produk", tint = Color.White)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Katalog Produk & Stok", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Kelola daftar barang jualan Anda", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            shadowElevation = 4.dp,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.width(340.dp)
                        ) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Cari produk (Nama, SKU, Barcode)...", fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Katalog Produk & Stok", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(text = "Kelola daftar barang jualan Anda", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            shadowElevation = 4.dp,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Cari produk (Nama, SKU, Barcode)...", fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showCategoriesModal = true },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Category, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kategori & Merek", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { viewModel.exportProductsToPdf(context) },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ekspor PDF", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { viewModel.exportProductsToCsv(context) },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Excel / CSV", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Tidak ada produk ditemukan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredProducts) { product ->
                        ProductItemRow(
                            product = product,
                            category = categories.find { it.id == product.categoryId },
                            viewModel = viewModel,
                            onEdit = {
                                productToEdit = product
                                showAddDialog = true
                            },
                            onDelete = { productToDelete = product }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProductFormDialog(
            productToEdit = productToEdit,
            categories = categories,
            brands = brands,
            warehouses = warehouses,
            viewModel = viewModel,
            onDismiss = { showAddDialog = false }
        )
    }
    if (showAddCategoryDialog) {
        AddCategoryDialog(viewModel = viewModel, onDismiss = { showAddCategoryDialog = false })
    }

    productToDelete?.let { prod ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Konfirmasi Hapus Produk", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus produk \"${prod.name}\"? Action ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(prod)
                        productToDelete = null
                        Toast.makeText(context, "Produk ${prod.name} berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Hapus Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun rememberUriBitmap(uriString: String?): androidx.compose.ui.graphics.ImageBitmap? {
    val context = LocalContext.current
    return remember(uriString) {
        if (uriString.isNullOrBlank()) null
        else {
            try {
                val uri = Uri.parse(uriString)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    android.graphics.BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}

@Composable
fun ProductItemRow(
    product: ProductEntity,
    category: CategoryEntity?,
    viewModel: PosViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isLow = product.stock <= product.minStock
    val productBitmap = rememberUriBitmap(product.imageUrl)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (productBitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = productBitmap,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Inventory, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1
                )
                Text(
                    text = "SKU: ${product.code} ${if(product.barcode.isNotBlank()) "• BC: ${product.barcode}" else ""}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = category?.name ?: "Tanpa Kategori",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = viewModel.formatMoney(product.sellPrice),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = if (isLow) Color(0xFFFEE2E2) else Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Stok: ${product.stock}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLow) Color(0xFF991B1B) else Color(0xFF065F46),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
    productToEdit: ProductEntity?,
    categories: List<CategoryEntity>,
    brands: List<BrandEntity>,
    warehouses: List<WarehouseEntity>,
    viewModel: PosViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(productToEdit?.name ?: "") }
    var code by remember { mutableStateOf(productToEdit?.code ?: "") }
    var barcode by remember { mutableStateOf(productToEdit?.barcode ?: "") }
    var buyPriceStr by remember { mutableStateOf(productToEdit?.buyPrice?.toString() ?: "") }
    var sellPriceStr by remember { mutableStateOf(productToEdit?.sellPrice?.toString() ?: "") }
    var stockStr by remember { mutableStateOf(productToEdit?.stock?.toString() ?: "") }
    var minStockStr by remember { mutableStateOf(productToEdit?.minStock?.toString() ?: "5") }
    var description by remember { mutableStateOf(productToEdit?.description ?: "") }
    var imageUrlStr by remember { mutableStateOf(productToEdit?.imageUrl ?: "") }
    
    var selectedCatId by remember { mutableStateOf(productToEdit?.categoryId ?: categories.firstOrNull()?.id) }
    var selectedBrandId by remember { mutableStateOf(productToEdit?.brandId ?: brands.firstOrNull()?.id) }
    var selectedWhId by remember { mutableStateOf(productToEdit?.warehouseId ?: warehouses.firstOrNull()?.id) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(20.dp)) {
                item {
                    Text(
                        text = if (productToEdit == null) "Tambah Produk Baru" else "Edit Produk",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Produk") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("SKU / Kode") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = barcode,
                            onValueChange = { barcode = it },
                            label = { Text("Barcode") },
                            singleLine = true,
                            trailingIcon = { Icon(Icons.Default.QrCode2, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = buyPriceStr,
                            onValueChange = { buyPriceStr = it },
                            label = { Text("Harga Beli / Modal") },
                            supportingText = { Text("Otomatis (Modal + Ongkir)", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = sellPriceStr,
                            onValueChange = { sellPriceStr = it },
                            label = { Text("Harga Jual") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    var expandedCat by remember { mutableStateOf(false) }
                    var expandedBrand by remember { mutableStateOf(false) }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = expandedCat,
                            onExpandedChange = { expandedCat = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = categories.find { it.id == selectedCatId }?.name ?: "Pilih Kategori",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Kategori") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.name) },
                                        onClick = { selectedCatId = cat.id; expandedCat = false }
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = expandedBrand,
                            onExpandedChange = { expandedBrand = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = brands.find { it.id == selectedBrandId }?.name ?: "Pilih Brand",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Brand") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBrand) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expandedBrand, onDismissRequest = { expandedBrand = false }) {
                                brands.forEach { brand ->
                                    DropdownMenuItem(
                                        text = { Text(brand.name) },
                                        onClick = { selectedBrandId = brand.id; expandedBrand = false }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    val context = LocalContext.current
                    val imagePickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        uri?.let {
                            imageUrlStr = it.toString()
                            Toast.makeText(context, "Foto produk berhasil dipilih!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        val formBitmap = rememberUriBitmap(imageUrlStr)
                        Surface(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (formBitmap != null) {
                                    androidx.compose.foundation.Image(
                                        bitmap = formBitmap,
                                        contentDescription = "Preview Foto Produk",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    Icon(Icons.Default.Image, contentDescription = "Gambar Produk")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Foto Produk", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                if (imageUrlStr.isNotBlank()) "Foto terpilih dari galeri HP" else "Ketuk untuk memilih foto produk dari galeri",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = stockStr,
                            onValueChange = { stockStr = it },
                            label = { Text("Stok Awal") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minStockStr,
                            onValueChange = { minStockStr = it },
                            label = { Text("Peringatan Stok Min") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                item {
                    var showSaveConfirm by remember { mutableStateOf(false) }

                    if (showSaveConfirm) {
                        AlertDialog(
                            onDismissRequest = { showSaveConfirm = false },
                            title = { Text("Konfirmasi Simpan Produk", fontWeight = FontWeight.Bold) },
                            text = { Text("Apakah Anda yakin ingin menyimpan perubahan data produk \"$name\"?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showSaveConfirm = false
                                        viewModel.saveProduct(
                                            id = productToEdit?.id ?: 0L,
                                            name = name,
                                            code = code,
                                            barcode = barcode,
                                            catId = selectedCatId,
                                            brandId = selectedBrandId,
                                            warehouseId = selectedWhId,
                                            buyPrice = buyPriceStr.toDoubleOrNull() ?: 0.0,
                                            sellPrice = sellPriceStr.toDoubleOrNull() ?: 0.0,
                                            stock = stockStr.toIntOrNull() ?: 0,
                                            minStock = minStockStr.toIntOrNull() ?: 5,
                                            desc = description,
                                            imageUrl = imageUrlStr
                                        )
                                        Toast.makeText(context, "Data produk berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    }
                                ) {
                                    Text("Ya, Lanjutkan")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showSaveConfirm = false }) { Text("Batal") }
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                if (name.isBlank() || code.isBlank()) {
                                    Toast.makeText(context, "Lengkapi Nama dan SKU", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                showSaveConfirm = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Simpan Produk")
                        }
                    }
                }
            }
        }

}


@Composable
fun AddCategoryDialog(viewModel: PosViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Tambah Kategori", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Kategori") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi (Opsional)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addCategory(name = name, code = name.take(3).uppercase(), desc = description)
                            onDismiss()
                        }
                    }) { Text("Simpan") }
                }
            }
        }
    }
}
}
