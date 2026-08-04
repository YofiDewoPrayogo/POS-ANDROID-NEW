package com.yofidewo.pos.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yofidewo.pos.data.CategoryEntity
import com.yofidewo.pos.data.CurrencyEntity
import com.yofidewo.pos.data.DiscountEntity
import com.yofidewo.pos.data.ProductEntity
import com.yofidewo.pos.ui.CartItem
import com.yofidewo.pos.ui.PosViewModel
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashierPosScreen(viewModel: PosViewModel) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val cart by viewModel.cartItems.collectAsState()
    val currencies by viewModel.currencies.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isActivated by viewModel.isActivated.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showTrialExpiredDialog by remember { mutableStateOf(false) }

    val trialTxLeft by viewModel.trialTransactionsLeft.collectAsState()
    val isTrialExpiredState by viewModel.isTrialExpired.collectAsState()
    val isProUnlocked by viewModel.isProUnlocked.collectAsState()
    val outletCode by viewModel.outletCode.collectAsState()

    val isTrialExpired = !isProUnlocked && (isTrialExpiredState || trialTxLeft <= 0)

    val filteredProducts = products.filter { p ->
        val matchSearch = p.name.contains(searchQuery, ignoreCase = true) || p.barcode.contains(searchQuery, ignoreCase = true) || p.code.contains(searchQuery, ignoreCase = true)
        val matchCategory = if (selectedCategoryId == null) true else p.categoryId == selectedCategoryId
        matchSearch && matchCategory
    }

    val totalUsd = cart.sumOf { it.product.sellPrice * it.quantity }

    val handleBarcodeScanSubmit = { queryToScan: String ->
        val cleanQuery = queryToScan.trim()
        if (cleanQuery.isNotEmpty()) {
            val matchedProduct = products.find { p ->
                p.barcode.equals(cleanQuery, ignoreCase = true) ||
                p.code.equals(cleanQuery, ignoreCase = true)
            } ?: filteredProducts.firstOrNull()

            if (matchedProduct != null) {
                viewModel.addToCart(matchedProduct)
                searchQuery = ""
                Toast.makeText(
                    context,
                    "✓ ${matchedProduct.name} masuk ke keranjang",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Produk '$cleanQuery' tidak ditemukan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth > 600.dp
        
        if (isTablet) {
            // TABLET LAYOUT: Split Screen (Left: Catalog, Right: Cart)
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Side (Catalog)
                Column(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    if (!isActivated) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isTrialExpired) Color(0xFFFFEBEE) else Color(0xFFFFF8E1)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(
                                        imageVector = if (isTrialExpired) Icons.Default.Warning else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (isTrialExpired) Color(0xFFC62828) else Color(0xFFF57F17),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (isTrialExpired) "Masa Trial 30x Transaksi Habis!" else "Mode Trial: Sisa $trialTxLeft / 30 Transaksi Gratis",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTrialExpired) Color(0xFFC62828) else Color(0xFFF57F17)
                                    )
                                }
                                if (isTrialExpired) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFC62828),
                                        onClick = { showTrialExpiredDialog = true }
                                    ) {
                                        Text("Aktivasi", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }

                    PosHeader(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onBarcodeScanSubmit = { handleBarcodeScanSubmit(it) },
                        currencies = currencies,
                        selectedCurrency = selectedCurrency,
                        onSelectCurrency = { viewModel.selectCurrency(it) },
                        currentUser = currentUser
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryFilters(
                        categories = categories,
                        selectedCategoryId = selectedCategoryId,
                        onSelectCategory = { selectedCategoryId = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ProductGrid(
                        products = filteredProducts,
                        viewModel = viewModel,
                        columns = 3
                    )
                }

                // Right Side (Cart Panel)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    CartPanel(
                        cart = cart,
                        totalUsd = totalUsd,
                        viewModel = viewModel,
                        onCheckout = {
                            if (isTrialExpired) {
                                showTrialExpiredDialog = true
                            } else {
                                showCheckoutDialog = true
                            }
                        }
                    )
                }
            }
        } else {
            // PHONE LAYOUT: Stacked
            var showCartSheet by remember { mutableStateOf(false) }

            Scaffold(
                floatingActionButton = {
                    if (cart.isNotEmpty()) {
                        ExtendedFloatingActionButton(
                            onClick = { showCartSheet = true },
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${cart.size} Item(s) • ${viewModel.formatMoney(totalUsd)}")
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    if (!isActivated) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isTrialExpired) Color(0xFFFFEBEE) else Color(0xFFFFF8E1)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(
                                        imageVector = if (isTrialExpired) Icons.Default.Warning else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (isTrialExpired) Color(0xFFC62828) else Color(0xFFF57F17),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (isTrialExpired) "Masa Trial 30x Transaksi Habis!" else "Trial: Sisa $trialTxLeft / 30 Transaksi",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTrialExpired) Color(0xFFC62828) else Color(0xFFF57F17)
                                    )
                                }
                                if (isTrialExpired) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFC62828),
                                        onClick = { showTrialExpiredDialog = true }
                                    ) {
                                        Text("Aktivasi", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }

                    PosHeader(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onBarcodeScanSubmit = { handleBarcodeScanSubmit(it) },
                        currencies = currencies,
                        selectedCurrency = selectedCurrency,
                        onSelectCurrency = { viewModel.selectCurrency(it) },
                        currentUser = currentUser
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryFilters(
                        categories = categories,
                        selectedCategoryId = selectedCategoryId,
                        onSelectCategory = { selectedCategoryId = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ProductGrid(
                        products = filteredProducts,
                        viewModel = viewModel,
                        columns = 2
                    )
                }
            }

            if (showCartSheet) {
                ModalBottomSheet(onDismissRequest = { showCartSheet = false }) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        CartPanel(
                            cart = cart,
                            totalUsd = totalUsd,
                            viewModel = viewModel,
                            onCheckout = {
                                showCartSheet = false
                                if (isTrialExpired) {
                                    showTrialExpiredDialog = true
                                } else {
                                    showCheckoutDialog = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showTrialExpiredDialog) {
        var inputLicenseKey by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { if (!isTrialExpired) showTrialExpiredDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Masa Trial 30x Transaksi Habis!", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Batas 30x transaksi gratis versi trial telah tercapai.\nSilakan hubungi Admin/Developer dengan Kode Outlet Anda untuk mendapatkan Kode Aktivasi PRO.",
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Kode Outlet Anda:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(if (outletCode.isBlank()) "POS-LOCAL" else outletCode, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = inputLicenseKey,
                        onValueChange = { inputLicenseKey = it },
                        label = { Text("Masukkan Kode Lisensi PRO") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (inputLicenseKey.isBlank()) {
                                Toast.makeText(context, "Masukkan Kode Lisensi terlebih dahulu", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val success = viewModel.activateProKey(inputLicenseKey)
                            if (success) {
                                Toast.makeText(context, "🎉 Selamat! WarungKu POS Lisensi PRO Berhasil Diaktivasi!", Toast.LENGTH_LONG).show()
                                showTrialExpiredDialog = false
                            } else {
                                Toast.makeText(context, "❌ Kode Lisensi tidak cocok untuk Kode Outlet ini", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Aktivasi PRO Sekarang", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            try {
                                val currentCode = if (outletCode.isBlank()) "POS-LOCAL" else outletCode
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
                        Text("💬 Hubungi Admin via WhatsApp", fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (showCheckoutDialog) {
        CheckoutDialog(
            viewModel = viewModel,
            totalUsd = totalUsd,
            onDismiss = { showCheckoutDialog = false }
        )
    }
}

@Composable
fun PosHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onBarcodeScanSubmit: (String) -> Unit,
    currencies: List<CurrencyEntity>,
    selectedCurrency: CurrencyEntity,
    onSelectCurrency: (CurrencyEntity) -> Unit,
    currentUser: com.yofidewo.pos.data.UserEntity?
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Cari / Scan (Nama, SKU, Barcode)...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { onBarcodeScanSubmit(searchQuery) }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan / Submit", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onBarcodeScanSubmit(searchQuery) }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                        onBarcodeScanSubmit(searchQuery)
                        true
                    } else {
                        false
                    }
                }
        )
    }
}

@Composable
fun CategoryFilters(
    categories: List<CategoryEntity>,
    selectedCategoryId: Long?,
    onSelectCategory: (Long?) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = if (selectedCategoryId == null) 0 else categories.indexOfFirst { it.id == selectedCategoryId } + 1,
        edgePadding = 0.dp,
        modifier = Modifier.fillMaxWidth(),
        divider = {} // Remove default divider
    ) {
        Tab(
            selected = selectedCategoryId == null,
            onClick = { onSelectCategory(null) },
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (selectedCategoryId == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Semua Produk",
                    color = if (selectedCategoryId == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        categories.forEach { cat ->
            Tab(
                selected = selectedCategoryId == cat.id,
                onClick = { onSelectCategory(cat.id) },
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedCategoryId == cat.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = cat.name,
                        color = if (selectedCategoryId == cat.id) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ProductGrid(
    products: List<ProductEntity>,
    viewModel: PosViewModel,
    columns: Int
) {
    if (products.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tidak ada produk.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(products) { product ->
                PosProductCard(product = product, viewModel = viewModel)
            }
        }
    }
}


@Composable
fun PosProductCard(
    product: ProductEntity,
    viewModel: PosViewModel
) {
    val isOutOfStock = product.stock <= 0
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isOutOfStock) {
                viewModel.addToCart(product)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isOutOfStock) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, if (isOutOfStock) Color.LightGray else MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        val productBitmap = rememberUriBitmap(product.imageUrl)
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
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
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp)
                    )
                }
                if (isOutOfStock) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "HABIS",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = viewModel.formatMoney(product.sellPrice),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Stok: ${product.stock}",
                    fontSize = 11.sp,
                    color = if (isOutOfStock) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CartPanel(
    cart: List<CartItem>,
    totalUsd: Double,
    viewModel: PosViewModel,
    onCheckout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Keranjang (${cart.sumOf { it.quantity }})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (cart.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearCart() }) {
                        Text("Kosongkan", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        if (cart.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Keranjang kosong",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f).padding(8.dp)) {
                items(cart) { item ->
                    CartItemRow(
                        item = item,
                        viewModel = viewModel,
                        onAdd = { viewModel.updateCartQuantity(item.product.id, item.quantity + 1) },
                        onRemove = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) },
                        onChangeQty = { newQty -> viewModel.updateCartQuantity(item.product.id, newQty) }
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 14.sp)
                        Text(viewModel.formatMoney(totalUsd), fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Bayar ${viewModel.formatMoney(totalUsd)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    viewModel: PosViewModel,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onChangeQty: (Int) -> Unit
) {
    var showQtyDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = viewModel.formatMoney(item.product.sellPrice),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurang", modifier = Modifier.size(18.dp))
            }
            
            Text(
                text = "${item.quantity}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clickable { showQtyDialog = true }
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            IconButton(
                onClick = onAdd,
                enabled = item.quantity < item.product.stock,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(18.dp))
            }
        }
    }

    if (showQtyDialog) {
        var qtyInput by remember { mutableStateOf(item.quantity.toString()) }
        AlertDialog(
            onDismissRequest = { showQtyDialog = false },
            title = { Text("Ubah Qty") },
            text = {
                OutlinedTextField(
                    value = qtyInput,
                    onValueChange = { qtyInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    val newQty = qtyInput.toIntOrNull() ?: item.quantity
                    if (newQty > 0 && newQty <= item.product.stock) {
                        onChangeQty(newQty)
                    }
                    showQtyDialog = false
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showQtyDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun CurrencySelector(
    selectedCurrency: CurrencyEntity,
    currencies: List<CurrencyEntity>,
    onSelect: (CurrencyEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "${selectedCurrency.code} (${selectedCurrency.symbol})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            currencies.forEach { curr ->
                DropdownMenuItem(
                    text = {
                        Text(text = "${curr.code} (${curr.symbol}) - Rate: ${curr.exchangeRate}")
                    },
                    onClick = {
                        onSelect(curr)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutDialog(
    viewModel: PosViewModel,
    totalUsd: Double,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val curr by viewModel.selectedCurrency.collectAsState()
    val customMethods by viewModel.customPaymentMethods.collectAsState()
    val discounts by viewModel.discounts.collectAsState()
    
    var customerName by remember { mutableStateOf("") }
    var paidAmountInput by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Tunai (Cash)") }
    var notes by remember { mutableStateOf("") }
    var isPiutang by remember { mutableStateOf(false) }
    var selectedDiscount by remember { mutableStateOf<DiscountEntity?>(null) }
    var expandedDiscount by remember { mutableStateOf(false) }
    
    var showReceipt by remember { mutableStateOf(false) }

    val discountAmount = when {
        selectedDiscount == null -> 0.0
        selectedDiscount?.type == "PERCENT" -> (totalUsd * curr.exchangeRate) * ((selectedDiscount?.value ?: 0.0) / 100.0)
        selectedDiscount?.type == "FIXED" -> (selectedDiscount?.value ?: 0.0)
        else -> 0.0
    }
    val totalInCurrBeforeDiscount = totalUsd * curr.exchangeRate
    val totalInCurr = (totalInCurrBeforeDiscount - discountAmount).coerceAtLeast(0.0)
    val totalAfterDiscountUsd = totalInCurr / curr.exchangeRate

    val paidDouble = if (isPiutang) 0.0 else (paidAmountInput.toDoubleOrNull() ?: 0.0)
    val changeDouble = if (isPiutang) 0.0 else (paidDouble - totalInCurr).coerceAtLeast(0.0)
    
    val quickAmounts = listOf(
        totalInCurr, 
        ((totalInCurr / 10000).toInt() + 1) * 10000.0,
        50000.0, 100000.0
    ).filter { it >= totalInCurr }.distinct().sorted()

    if (showReceipt) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                    Text("WARUNGKU POS", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterHorizontally), color = Color(0xFFFF6600))
                    Text("Struk Pembayaran", fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Pelanggan: ${if(customerName.isNotBlank()) customerName else "Umum"}")
                    Text("Metode: $paymentMethod")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Tagihan: ${viewModel.formatMoney(totalUsd)}")
                    if (discountAmount > 0) {
                        Text("Diskon: -${viewModel.formatMoney(discountAmount)}", color = MaterialTheme.colorScheme.error)
                    }
                    Text("Total Akhir: ${viewModel.formatMoney(totalAfterDiscountUsd)}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!isPiutang) {
                        Text("Dibayar: ${viewModel.formatMoney(paidDouble / curr.exchangeRate)}")
                        Text("Kembali: ${viewModel.formatMoney(changeDouble / curr.exchangeRate)}", fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                    } else {
                        Text("Status: BELUM LUNAS (Piutang)", color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            val printerAddr = viewModel.selectedPrinterAddress.value
                            if (printerAddr.isNotBlank()) {
                                Toast.makeText(context, "Mencetak struk via Bluetooth...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
                            }
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Selesai & Cetak Struk", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
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
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .heightIn(max = 680.dp)
            ) {
                Text(
                    "Sistem Pembayaran",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Total Tagihan Banner
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Total Tagihan", fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(
                                    text = viewModel.formatMoney(totalAfterDiscountUsd),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                if (discountAmount > 0) {
                                    Text(
                                        "Sudah termasuk diskon: -${viewModel.formatMoney(discountAmount)}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Metode Pembayaran Chips (Consistent Grid)
                    item {
                        Column {
                            Text("Pilih Metode Pembayaran", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val allMethods = customMethods.ifEmpty { listOf("Tunai (Cash)", "QRIS / Transfer", "Piutang") }
                            
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                allMethods.chunked(2).forEach { rowMethods ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowMethods.forEach { method ->
                                            val isSelected = if (method.contains("Piutang", ignoreCase = true)) isPiutang else (!isPiutang && paymentMethod == method)
                                            Surface(
                                                onClick = {
                                                    if (method.contains("Piutang", ignoreCase = true)) {
                                                        isPiutang = true
                                                        paymentMethod = method
                                                        paidAmountInput = "0"
                                                    } else {
                                                        isPiutang = false
                                                        paymentMethod = method
                                                    }
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                                                    Text(
                                                        text = method,
                                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        fontSize = 13.sp,
                                                        maxLines = 1,
                                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                        if (rowMethods.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Inputs: Nominal & Diskon
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (!isPiutang) {
                                OutlinedTextField(
                                    value = paidAmountInput,
                                    onValueChange = { paidAmountInput = it },
                                    label = { Text("Uang Diterima") },
                                    placeholder = { Text("0") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            
                            ExposedDropdownMenuBox(
                                expanded = expandedDiscount,
                                onExpandedChange = { expandedDiscount = !expandedDiscount },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = selectedDiscount?.let { disc ->
                                        if (disc.type == "PERCENT") "${disc.name} (${disc.value.toInt()}%)"
                                        else "${disc.name} (${viewModel.formatMoney(disc.value)})"
                                    } ?: "Tanpa Diskon",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Pilih Diskon") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDiscount) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedDiscount,
                                    onDismissRequest = { expandedDiscount = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Tanpa Diskon (0%)") },
                                        onClick = {
                                            selectedDiscount = null
                                            expandedDiscount = false
                                        }
                                    )
                                    discounts.forEach { disc ->
                                        val label = if (disc.type == "PERCENT") "${disc.name} (${disc.value.toInt()}%)" else "${disc.name} (${viewModel.formatMoney(disc.value)})"
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                selectedDiscount = disc
                                                expandedDiscount = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Quick Cash Amount Buttons
                    if (!isPiutang && paymentMethod.contains("Tunai", ignoreCase = true)) {
                        item {
                            Column {
                                Text("Pilihan Uang Cepat", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    quickAmounts.take(4).forEach { amount ->
                                        OutlinedButton(
                                            onClick = {
                                                paidAmountInput = if (curr.code == "IDR") String.format(java.util.Locale("id", "ID"), "%.0f", amount) else String.format(java.util.Locale.US, "%.2f", amount)
                                            },
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                if (amount == totalInCurr) "Uang Pas" else viewModel.formatMoney(amount / curr.exchangeRate),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Kembalian / Status Display Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPiutang) Color(0xFFFFF3E0) else if (paidDouble >= totalInCurr) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (isPiutang) "Status Pembayaran:" else "Kembalian:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isPiutang) "Belum Lunas (Piutang)" else if (paidDouble >= totalInCurr) viewModel.formatMoney(changeDouble / curr.exchangeRate) else "Uang Kurang!",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = if (isPiutang) Color(0xFFE65100) else if (paidDouble >= totalInCurr) Color(0xFF065F46) else Color(0xFF991B1B)
                                )
                            }
                        }
                    }

                    // Customer Name & Notes
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = customerName,
                                onValueChange = { customerName = it },
                                label = { Text(if (isPiutang) "Nama Pelanggan (Wajib)" else "Nama Pelanggan (Opsional)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Catatan Transaksi") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }
                    
                    Button(
                        onClick = {
                            if (!isPiutang && paidDouble < totalInCurr) {
                                Toast.makeText(context, "Uang pembayaran kurang!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (isPiutang && customerName.isBlank()) {
                                Toast.makeText(context, "Nama pelanggan wajib diisi untuk piutang", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.processCheckout(
                                customerName = customerName.ifBlank { "Umum" },
                                paymentMethod = paymentMethod,
                                paidAmount = paidDouble,
                                notes = notes,
                                discountAmount = discountAmount / curr.exchangeRate,
                                onSuccess = { showReceipt = true }
                            )
                        },
                        modifier = Modifier.weight(1.5f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("BAYAR", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
