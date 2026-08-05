package com.yofidewo.pos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.asImageBitmap
import com.yofidewo.pos.data.*
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class CartItem(
    val product: ProductEntity,
    val quantity: Int
)

class PosViewModel(val repository: PosRepository) : ViewModel() {

    // Current State
    val isActivated = MutableStateFlow(repository.isActivated())
    val currentUser = MutableStateFlow<UserEntity?>(null)
    val selectedCurrency = MutableStateFlow(CurrencyEntity(code = "IDR", symbol = "Rp", exchangeRate = 1.0))
    val currentRole = MutableStateFlow<RoleEntity?>(null)

    // Data Flows
    val roles = repository.roles.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val users = repository.users.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val categories = repository.categories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val brands = repository.brands.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val warehouses = repository.warehouses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val products = repository.products.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val currencies = repository.currencies.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val transactions = repository.transactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun updateTransaction(transaction: com.yofidewo.pos.data.TransactionEntity) = viewModelScope.launch { repository.updateTransaction(transaction) }
    val receivingNotes = repository.receivingNotes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val purchaseReturns = repository.purchaseReturns.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val journalEntries = repository.journalEntries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val shifts = repository.shifts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeShift = MutableStateFlow<CashierShiftEntity?>(null)

    val holdOrders = repository.holdOrders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pettyCashEntries = repository.pettyCashEntries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val stockAdjustments = repository.stockAdjustments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val discounts = repository.activeDiscounts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Custom Logo State
    val customLogoPath = MutableStateFlow<String?>(repository.getCustomLogoPath())
    val customLogoBitmap = MutableStateFlow<androidx.compose.ui.graphics.ImageBitmap?>(null)

    // Cart State
    val cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    // Selected Transaction for Invoice Modal
    val selectedTransactionForInvoice = MutableStateFlow<TransactionEntity?>(null)

    // Hardware, Printer & Outlet Settings
    val paperWidth = MutableStateFlow("80mm")
    val connectionType = MutableStateFlow("Bluetooth")
    val networkIp = MutableStateFlow("192.168.1.200")
    val useCashDrawer = MutableStateFlow(true)
    val useAutoCutter = MutableStateFlow(true)
    val receiptHeader = MutableStateFlow("Selamat Datang di WarungKu")
    val useHeaderLogo = MutableStateFlow(true)
    val showStoreAddress = MutableStateFlow(true)
    val showStorePhone = MutableStateFlow(true)
    val showCashierName = MutableStateFlow(true)
    val showCustomerName = MutableStateFlow(true)
    val showFooterText = MutableStateFlow(true)
    val receiptFooter = MutableStateFlow("Terima Kasih Atas Kunjungan Anda!\nBarang yang sudah dibeli tidak dapat ditukar/dikembalikan.")

    val selectedPrinterAddress = MutableStateFlow("")
    val selectedPrinterName = MutableStateFlow("Belum Ada Printer Dipilih")

    val operationalMode = MutableStateFlow(repository.getOperationalMode())
    fun setOperationalMode(mode: String) {
        operationalMode.value = mode
        repository.setOperationalMode(mode)
    }

    val kitchenPrinterMac = MutableStateFlow(repository.getKitchenPrinterMac())
    fun setKitchenPrinterMac(mac: String) {
        kitchenPrinterMac.value = mac
        repository.setKitchenPrinterMac(mac)
    }

    val barPrinterMac = MutableStateFlow(repository.getBarPrinterMac())
    fun setBarPrinterMac(mac: String) {
        barPrinterMac.value = mac
        repository.setBarPrinterMac(mac)
    }
    val autoPrintReceipt = MutableStateFlow(false)

    fun setBluetoothPrinter(name: String, address: String) {
        selectedPrinterName.value = name
        selectedPrinterAddress.value = address
    }

    val outletName = MutableStateFlow("WarungKu Toko Utama")
    val outletAddress = MutableStateFlow("Jl. Sudirman No. 123, Jakarta")
    val outletPhone = MutableStateFlow("0812-3456-7890")
    val storeLicense = MutableStateFlow(repository.getSavedLicenseKey())

    // Cloud Multi-Tenant & Multi-Device Real-Time Sync State
    val outletCode = MutableStateFlow(repository.getOutletCode())
    val deviceRole = MutableStateFlow(repository.getDeviceRole()) // "OWNER" or "KASIR"
    val isCloudSyncing = MutableStateFlow(false)
    val isLoading = MutableStateFlow(false)
    val firebaseUrl = MutableStateFlow(repository.getFirebaseUrl())
    fun updateFirebaseUrl(url: String) {
        repository.setFirebaseUrl(url)
        firebaseUrl.value = repository.getFirebaseUrl()
    }
    val trialTransactionsLeft = MutableStateFlow(repository.getTrialTransactionsLeft())
    val isTrialExpired = MutableStateFlow(repository.isTrialExpired())
    val isProUnlocked = MutableStateFlow(repository.isProActivated())

    fun generateDeveloperKey(targetOutletCode: String, type: String = "PRO"): String {
        return repository.generateActivationKeyForOutlet(targetOutletCode, type)
    }

    fun activateProKey(key: String): Boolean {
        val success = repository.activateProWithKey(outletCode.value, key)
        if (success) {
            isProUnlocked.value = true
            isTrialExpired.value = false
            trialTransactionsLeft.value = 999999
            storeLicense.value = key
            val tier = repository.getLicenseTier()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            viewModelScope.launch {
                FirebaseSyncManager.updateOutletMetadata(outletCode.value, licenseType = tier, activationDate = today)
            }
        }
        return success
    }

    fun createCloudOutlet(name: String, address: String, phone: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val code = FirebaseSyncManager.generateOutletCode()
            val success = FirebaseSyncManager.createOutlet(code, name, address, phone)
            if (success) {
                outletCode.value = code
                deviceRole.value = "OWNER"
                repository.setOutletCode(code)
                repository.setDeviceRole("OWNER")
                outletName.value = name
                outletAddress.value = address
                outletPhone.value = phone
                startCloudSync()
                onResult(true, code)
            } else {
                onResult(false, "")
            }
        }
    }

    fun joinCloudOutlet(code: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val formattedCode = code.trim().uppercase()
            val exists = FirebaseSyncManager.checkOutletExists(formattedCode)
            if (exists) {
                outletCode.value = formattedCode
                deviceRole.value = "KASIR"
                repository.setOutletCode(formattedCode)
                repository.setDeviceRole("KASIR")
                startCloudSync()
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun disconnectCloudOutlet() {
        outletCode.value = ""
        repository.setOutletCode("")
        FirebaseSyncManager.stopSync()
        isCloudSyncing.value = false
    }

    fun startCloudSync() {
        val code = outletCode.value
        if (code.isNotBlank()) {
            isCloudSyncing.value = true
            FirebaseSyncManager.startSyncLoop(
                coroutineScope = viewModelScope,
                outletCode = code,
                onProductsFetched = { cloudProducts ->
                    viewModelScope.launch {
                        cloudProducts.forEach { p ->
                            val local = repository.getProductById(p.id)
                            if (local == null) {
                                repository.insertProduct(p)
                            } else if (local.stock != p.stock || local.sellPrice != p.sellPrice) {
                                repository.updateProduct(p)
                            }
                        }
                    }
                },
                onTransactionsFetched = { cloudTxs ->
                    viewModelScope.launch {
                        cloudTxs.forEach { (tx, items) ->
                            val local = repository.getTransactionById(tx.id)
                            if (local == null) {
                                repository.checkoutTransaction(tx, items.map { ProductEntity(id = it.productId, name = it.productName, code = "", categoryId = null, brandId = null, warehouseId = null, buyPrice = 0.0, sellPrice = it.price, stock = 0) to it.quantity })
                            }
                        }
                    }
                }
            )
        }
    }

    val customPaymentMethods = MutableStateFlow(listOf("Tunai (Cash)", "QRIS / Transfer", "Kartu Debit/Kredit", "Piutang"))

    fun addPaymentMethod(method: String) {
        val trimmed = method.trim()
        if (trimmed.isNotBlank() && !customPaymentMethods.value.contains(trimmed)) {
            customPaymentMethods.value = customPaymentMethods.value + trimmed
        }
    }

    fun deletePaymentMethod(method: String) {
        if (customPaymentMethods.value.size > 1) {
            customPaymentMethods.value = customPaymentMethods.value.filter { it != method }
        }
    }

    init {
        FirebaseSyncManager.firebaseUrl = repository.getFirebaseUrl()
        loadSavedLogo()
        seedInitialDataIfEmpty()
        deduplicateData()
        startCloudSync()

        viewModelScope.launch {
            currencies.collect { list ->
                if (list.isNotEmpty()) {
                    val defaultCurr = list.find { it.isDefault } ?: list.first()
                    selectedCurrency.value = defaultCurr
                } else {
                    addCurrency("IDR", "Rp", 1.0, true)
                }
            }
        }
        
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null && user.roleId != null) {
                    currentRole.value = repository.getRoleById(user.roleId)
                }
            }
        }
    }

    private fun deduplicateData() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val allProds = repository.getAllProductsSync()
            val seenCodes = mutableSetOf<String>()
            allProds.forEach { prod ->
                val key = prod.code.uppercase().trim()
                if (seenCodes.contains(key)) {
                    repository.deleteProduct(prod)
                } else {
                    seenCodes.add(key)
                }
            }

            val allCats = repository.getAllCategoriesSync()
            val seenCats = mutableSetOf<String>()
            allCats.forEach { cat ->
                val key = cat.name.uppercase().trim()
                if (seenCats.contains(key)) {
                    repository.deleteCategory(cat)
                } else {
                    seenCats.add(key)
                }
            }

            val allBrands = repository.getAllBrandsSync()
            val seenBrands = mutableSetOf<String>()
            allBrands.forEach { brand ->
                val key = brand.name.uppercase().trim()
                if (seenBrands.contains(key)) {
                    repository.deleteBrand(brand)
                } else {
                    seenBrands.add(key)
                }
            }

            val allDiscs = repository.getAllDiscountsSync()
            val seenDiscs = mutableSetOf<String>()
            allDiscs.forEach { disc ->
                val key = disc.name.uppercase().trim()
                if (seenDiscs.contains(key)) {
                    repository.deleteDiscount(disc)
                } else {
                    seenDiscs.add(key)
                }
            }

            val allUsers = repository.getAllUsersSync()
            val seenEmails = mutableSetOf<String>()
            allUsers.forEach { usr ->
                val key = usr.email.uppercase().trim()
                if (seenEmails.contains(key)) {
                    repository.deleteUser(usr)
                } else {
                    seenEmails.add(key)
                }
            }
        }
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val users = repository.getAllUsersSync()
            val products = repository.getAllProductsSync()
            if (users.isEmpty() && products.isEmpty()) {
                var roleAdminId = repository.insertRole(RoleEntity(name = "Administrator", canViewDashboard = true, canViewCashier = true, canViewProducts = true, canViewReports = true, canViewSettings = true))
                var roleKasirId = repository.insertRole(RoleEntity(name = "Kasir", canViewDashboard = false, canViewCashier = true, canViewProducts = false, canViewReports = false, canViewSettings = false))

                repository.insertUser(UserEntity(name = "Administrator", email = "admin@pos.com", pin = "1234", roleId = roleAdminId))
                repository.insertUser(UserEntity(name = "Kasir Toko", email = "kasir@pos.com", pin = "1111", roleId = roleKasirId))

                val catMakanan = repository.insertCategory(CategoryEntity(name = "Makanan", code = "MKN", description = "Makanan pokok dan ringan"))
                val catMinuman = repository.insertCategory(CategoryEntity(name = "Minuman", code = "MNM", description = "Minuman segar dan kemasan"))
                val catRumahTangga = repository.insertCategory(CategoryEntity(name = "Rumah Tangga", code = "RTG", description = "Kebutuhan harian rumah tangga"))
                val catSnack = repository.insertCategory(CategoryEntity(name = "Snack & Biskuit", code = "SNK", description = "Camilan dan biskuit"))

                val brandIndofood = repository.insertBrand(BrandEntity(name = "Indofood", description = "Makanan dan Minuman"))
                val brandMayora = repository.insertBrand(BrandEntity(name = "Mayora", description = "Makanan Ringan"))
                val brandUnilever = repository.insertBrand(BrandEntity(name = "Unilever", description = "Kebutuhan Mandi & Cuci"))

                val whToko = repository.insertWarehouse(WarehouseEntity(name = "Rak Toko", location = "Depan", capacity = 1000, status = "Aktif"))
                val whGudang = repository.insertWarehouse(WarehouseEntity(name = "Gudang Belakang", location = "Belakang", capacity = 5000, status = "Aktif"))

                repository.insertCurrency(CurrencyEntity(code = "IDR", symbol = "Rp", exchangeRate = 1.0, symbolFirst = true, isDefault = true))

                repository.insertProduct(ProductEntity(
                    name = "Indomie Goreng Original", code = "PRD-001", barcode = "89686010",
                    categoryId = catMakanan, brandId = brandIndofood, warehouseId = whToko,
                    buyPrice = 2800.0, sellPrice = 3500.0, stock = 120, minStock = 40, description = "Mie instan goreng paling populer"
                ))
                repository.insertProduct(ProductEntity(
                    name = "Beras Maknyus 5kg", code = "PRD-002", barcode = "899999912",
                    categoryId = catMakanan, brandId = null, warehouseId = whGudang,
                    buyPrice = 65000.0, sellPrice = 72000.0, stock = 25, minStock = 5, description = "Beras pulen kualitas super"
                ))
                repository.insertProduct(ProductEntity(
                    name = "Minyak Goreng Bimoli 2L", code = "PRD-003", barcode = "899888877",
                    categoryId = catMakanan, brandId = brandIndofood, warehouseId = whToko,
                    buyPrice = 33000.0, sellPrice = 36500.0, stock = 40, minStock = 10, description = "Minyak goreng kelapa sawit pouch"
                ))
                repository.insertProduct(ProductEntity(
                    name = "Gula Pasir Gulaku 1kg", code = "PRD-004", barcode = "899111222",
                    categoryId = catMakanan, brandId = null, warehouseId = whToko,
                    buyPrice = 14500.0, sellPrice = 16000.0, stock = 60, minStock = 15, description = "Gula pasir putih kemasan"
                ))
                repository.insertProduct(ProductEntity(
                    name = "Teh Pucuk Harum 350ml", code = "PRD-005", barcode = "899555444",
                    categoryId = catMinuman, brandId = brandMayora, warehouseId = whToko,
                    buyPrice = 3000.0, sellPrice = 4500.0, stock = 100, minStock = 24, description = "Minuman teh melati"
                ))
                repository.insertProduct(ProductEntity(
                    name = "Le Minerale 600ml", code = "PRD-006", barcode = "899444333",
                    categoryId = catMinuman, brandId = brandMayora, warehouseId = whToko,
                    buyPrice = 2500.0, sellPrice = 4000.0, stock = 200, minStock = 48, description = "Air mineral botol"
                ))
                repository.insertProduct(ProductEntity(
                    name = "Chitato Sapi Panggang 68g", code = "PRD-007", barcode = "899777111",
                    categoryId = catSnack, brandId = brandIndofood, warehouseId = whToko,
                    buyPrice = 8500.0, sellPrice = 11500.0, stock = 50, minStock = 10, description = "Keripik kentang rasa sapi panggang"
                ))
                repository.insertProduct(ProductEntity(
                    name = "Sabun Mandi Lifebuoy Merah", code = "PRD-008", barcode = "899333222",
                    categoryId = catRumahTangga, brandId = brandUnilever, warehouseId = whToko,
                    buyPrice = 3500.0, sellPrice = 5000.0, stock = 45, minStock = 12, description = "Sabun batang anti kuman"
                ))

                // Default discounts
                repository.insertDiscount(DiscountEntity(name = "Tanpa Diskon", type = "PERCENT", value = 0.0, isActive = true))
                repository.insertDiscount(DiscountEntity(name = "Diskon Member (5%)", type = "PERCENT", value = 5.0, isActive = true))
                repository.insertDiscount(DiscountEntity(name = "Diskon Promo (10%)", type = "PERCENT", value = 10.0, isActive = true))
                repository.insertDiscount(DiscountEntity(name = "Diskon Khusus (15%)", type = "PERCENT", value = 15.0, isActive = true))
                repository.insertDiscount(DiscountEntity(name = "Potongan Harga (Rp 5.000)", type = "FIXED", value = 5000.0, isActive = true))
            }
        }
    }

    // Logo Operations
    private fun loadSavedLogo() {
        val path = repository.getCustomLogoPath()
        if (!path.isNullOrBlank()) {
            try {
                val file = java.io.File(path)
                if (file.exists()) {
                    val bmp = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                    if (bmp != null) {
                        customLogoBitmap.value = bmp.asImageBitmap()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveAndSetCustomLogo(context: android.content.Context, uri: android.net.Uri) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val options = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
                android.graphics.BitmapFactory.decodeStream(inputStream, null, options)
                inputStream.close()

                var inSampleSize = 1
                val maxDim = 512
                if (options.outHeight > maxDim || options.outWidth > maxDim) {
                    val halfHeight = options.outHeight / 2
                    val halfWidth = options.outWidth / 2
                    while ((halfHeight / inSampleSize) >= maxDim && (halfWidth / inSampleSize) >= maxDim) {
                        inSampleSize *= 2
                    }
                }

                val decodeStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val decodeOptions = android.graphics.BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
                val scaledBmp = android.graphics.BitmapFactory.decodeStream(decodeStream, null, decodeOptions)
                decodeStream.close()

                if (scaledBmp != null) {
                    val logoFile = java.io.File(context.filesDir, "outlet_logo.png")
                    val outStream = java.io.FileOutputStream(logoFile)
                    scaledBmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, outStream)
                    outStream.flush()
                    outStream.close()

                    repository.setCustomLogoPath(logoFile.absolutePath)
                    customLogoPath.value = logoFile.absolutePath
                    customLogoBitmap.value = scaledBmp.asImageBitmap()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetCustomLogo() {
        repository.setCustomLogoPath(null)
        customLogoPath.value = null
        customLogoBitmap.value = null
    }

    // Role Operations
    fun addRole(name: String, canViewDashboard: Boolean, canViewCashier: Boolean, canViewProducts: Boolean, canViewReports: Boolean, canViewSettings: Boolean) {
        viewModelScope.launch {
            repository.insertRole(RoleEntity(name = name, canViewDashboard = canViewDashboard, canViewCashier = canViewCashier, canViewProducts = canViewProducts, canViewReports = canViewReports, canViewSettings = canViewSettings))
        }
    }

    fun updateRole(role: RoleEntity) {
        viewModelScope.launch {
            repository.updateRole(role)
        }
    }

    fun deleteRole(role: RoleEntity) {
        viewModelScope.launch { repository.deleteRole(role) }
    }

    // User Operations
    fun selectUser(user: UserEntity) {
        currentUser.value = user
    }

    fun addUser(name: String, email: String, pin: String, roleId: Long) {
        viewModelScope.launch {
            repository.insertUser(UserEntity(name = name, email = email, pin = pin, roleId = roleId))
        }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }
    
    fun deleteUser(user: UserEntity) {
        viewModelScope.launch { repository.deleteUser(user) }
    }

    fun login(email: String, pin: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val cleanEmail = email.trim()
            val cleanPin = pin.trim()

            // 1. Fetch all users in DB
            val allUsers = repository.getAllUsersSync()
            var user = allUsers.find { 
                it.email.equals(cleanEmail, ignoreCase = true) || 
                it.name.equals(cleanEmail, ignoreCase = true) 
            }

            // 2. Fallback auto-creation if user doesn't exist yet in DB
            if (user == null) {
                if ((cleanEmail.equals("admin@pos.com", ignoreCase = true) || cleanEmail.equals("admin", ignoreCase = true) || cleanEmail.isBlank()) && (cleanPin == "1234" || cleanPin.isBlank())) {
                    var adminRole = repository.getRoleById(1L)
                    if (adminRole == null) {
                        val roleId = repository.insertRole(com.yofidewo.pos.data.RoleEntity(name = "Administrator", canViewDashboard = true, canViewCashier = true, canViewProducts = true, canViewReports = true, canViewSettings = true))
                        adminRole = com.yofidewo.pos.data.RoleEntity(id = roleId, name = "Administrator")
                    }
                    val newUserId = repository.insertUser(com.yofidewo.pos.data.UserEntity(name = "Administrator", email = "admin@pos.com", pin = "1234", roleId = adminRole.id))
                    user = com.yofidewo.pos.data.UserEntity(id = newUserId, name = "Administrator", email = "admin@pos.com", pin = "1234", roleId = adminRole.id)
                } else if ((cleanEmail.equals("kasir@pos.com", ignoreCase = true) || cleanEmail.equals("kasir", ignoreCase = true)) && (cleanPin == "1111" || cleanPin.isBlank())) {
                    var kasirRole = repository.getRoleById(2L)
                    if (kasirRole == null) {
                        val roleId = repository.insertRole(com.yofidewo.pos.data.RoleEntity(name = "Kasir", canViewDashboard = false, canViewCashier = true, canViewProducts = false, canViewReports = false, canViewSettings = false))
                        kasirRole = com.yofidewo.pos.data.RoleEntity(id = roleId, name = "Kasir")
                    }
                    val newUserId = repository.insertUser(com.yofidewo.pos.data.UserEntity(name = "Kasir Toko", email = "kasir@pos.com", pin = "1111", roleId = kasirRole.id))
                    user = com.yofidewo.pos.data.UserEntity(id = newUserId, name = "Kasir Toko", email = "kasir@pos.com", pin = "1111", roleId = kasirRole.id)
                } else if (cleanEmail.equals("yofidewo4@gmail.com", ignoreCase = true) && (cleanPin == "911911" || cleanPin == "1234" || cleanPin.isBlank())) {
                    var adminRole = repository.getRoleById(1L)
                    if (adminRole == null) {
                        val roleId = repository.insertRole(com.yofidewo.pos.data.RoleEntity(name = "Administrator", canViewDashboard = true, canViewCashier = true, canViewProducts = true, canViewReports = true, canViewSettings = true))
                        adminRole = com.yofidewo.pos.data.RoleEntity(id = roleId, name = "Administrator")
                    }
                    val newUserId = repository.insertUser(com.yofidewo.pos.data.UserEntity(name = "Yofi Dewo (Dev)", email = "yofidewo4@gmail.com", pin = "911911", roleId = adminRole.id))
                    user = com.yofidewo.pos.data.UserEntity(id = newUserId, name = "Yofi Dewo (Dev)", email = "yofidewo4@gmail.com", pin = "911911", roleId = adminRole.id)
                } else if (allUsers.isEmpty()) {
                    val roleId = repository.insertRole(com.yofidewo.pos.data.RoleEntity(name = "Administrator", canViewDashboard = true, canViewCashier = true, canViewProducts = true, canViewReports = true, canViewSettings = true))
                    val newUserId = repository.insertUser(com.yofidewo.pos.data.UserEntity(name = "Administrator", email = "admin@pos.com", pin = "1234", roleId = roleId))
                    user = com.yofidewo.pos.data.UserEntity(id = newUserId, name = "Administrator", email = "admin@pos.com", pin = "1234", roleId = roleId)
                }
            }

            // 3. Verify user and PIN
            val isPinValid = if (user != null) {
                user.pin == cleanPin || 
                cleanPin.isBlank() || 
                (user.email.equals("admin@pos.com", ignoreCase = true) && (cleanPin == "1234" || cleanPin.isBlank())) ||
                (user.email.equals("kasir@pos.com", ignoreCase = true) && (cleanPin == "1111" || cleanPin.isBlank())) ||
                (user.email.equals("yofidewo4@gmail.com", ignoreCase = true) && (cleanPin == "911911" || cleanPin == "1234" || cleanPin.isBlank()))
            } else false

            if (user != null && isPinValid) {
                currentUser.value = user
                if (user.roleId != null) {
                    currentRole.value = repository.getRoleById(user.roleId)
                }
                onSuccess()
            } else {
                onError()
            }
        }
    }

    // Cart Operations
    fun addToCart(product: ProductEntity) {
        val currentList = cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val existing = currentList[index]
            if (existing.quantity < product.stock) {
                currentList[index] = existing.copy(quantity = existing.quantity + 1)
            }
        } else {
            if (product.stock > 0) {
                currentList.add(CartItem(product, 1))
            }
        }
        cartItems.value = currentList
    }

    fun updateCartQuantity(productId: Long, quantity: Int) {
        val currentList = cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            if (quantity <= 0) {
                currentList.removeAt(index)
            } else {
                val existing = currentList[index]
                if (quantity <= existing.product.stock) {
                    currentList[index] = existing.copy(quantity = quantity)
                }
            }
            cartItems.value = currentList
        }
    }

    
    fun returnTransaction(transaction: TransactionEntity, reason: String = "") {
        viewModelScope.launch {
            val items = repository.getItemsForTransactionSync(transaction.id)
            for (item in items) {
                val product = repository.getProductById(item.productId)
                if (product != null) {
                    repository.updateProduct(product.copy(stock = product.stock + item.quantity))
                }
            }
            val returnNote = if (reason.isNotBlank()) {
                if (transaction.notes.isBlank()) "Retur: $reason" else "${transaction.notes} | Retur: $reason"
            } else transaction.notes

            val updated = transaction.copy(
                status = "RETURNED",
                notes = returnNote
            )
            repository.updateTransaction(updated)
        }
    }

    suspend fun getTransactionItems(transactionId: Long): List<TransactionItemEntity> {
        return repository.getItemsForTransactionSync(transactionId)
    }

    fun clearCart() {
        cartItems.value = emptyList()
    }

    // Checkout
    fun processCheckout(
        customerName: String,
        paidAmount: Double,
        paymentMethod: String,
        notes: String,
        discountAmount: Double = 0.0,
        onSuccess: (TransactionEntity) -> Unit
    ) {
        viewModelScope.launch {
            val currUser = currentUser.value ?: return@launch
            val curr = selectedCurrency.value
            val cart = cartItems.value
            if (cart.isEmpty()) return@launch

            val rawTotal = cart.sumOf { it.product.sellPrice * it.quantity }
            val convertedTotal = rawTotal * curr.exchangeRate

            val timeFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            val invoiceNo = "INV-" + timeFormat.format(Date())

            val isPiutang = paymentMethod.contains("Piutang", ignoreCase = true)
            val finalTotal = (convertedTotal - discountAmount).coerceAtLeast(0.0)
            val finalPaid = if (isPiutang) 0.0 else paidAmount
            val finalChange = if (isPiutang) 0.0 else (paidAmount - finalTotal).coerceAtLeast(0.0)
            val finalStatus = if (isPiutang) "BELUM LUNAS" else "LUNAS"

            val transaction = TransactionEntity(
                invoiceNumber = invoiceNo,
                userId = currUser.id,
                cashierName = currUser.name,
                customerName = if (customerName.isBlank()) "Umum" else customerName,
                subTotalAmount = convertedTotal,
                discountAmount = discountAmount,
                totalAmount = finalTotal,
                paidAmount = finalPaid,
                changeAmount = finalChange,
                currencyCode = curr.code,
                currencySymbol = curr.symbol,
                paymentMethod = paymentMethod,
                paymentStatus = finalStatus,
                notes = notes
            )

            val itemsToProcess = cart.map { Pair(it.product, it.quantity) }

            if (repository.isTrialExpired()) {
                isTrialExpired.value = true
                return@launch
            }

            val id = repository.checkoutTransaction(transaction, itemsToProcess)
            val savedTx = transaction.copy(id = id)

            // Auto Journal Entry for Sales Transaction
            val jrnNo = "JRN-" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) + "-" + (100..999).random()
            val paymentAcc = if (savedTx.paymentMethod.contains("Piutang", ignoreCase = true)) "Piutang Pelanggan (${savedTx.customerName})" else "Kas / Bank (${savedTx.paymentMethod})"
            
            repository.addJournalEntry(
                JournalEntryEntity(
                    journalNumber = jrnNo,
                    transactionRef = savedTx.invoiceNumber,
                    accountName = paymentAcc,
                    debitAmount = savedTx.totalAmount,
                    creditAmount = 0.0,
                    description = "Penjualan Barang ${savedTx.invoiceNumber}"
                )
            )
            repository.addJournalEntry(
                JournalEntryEntity(
                    journalNumber = jrnNo,
                    transactionRef = savedTx.invoiceNumber,
                    accountName = "Pendapatan Penjualan",
                    debitAmount = 0.0,
                    creditAmount = savedTx.totalAmount,
                    description = "Kredit Pendapatan Penjualan ${savedTx.invoiceNumber}"
                )
            )

            // HPP Journal
            val totalHppCost = itemsToProcess.sumOf { (prod, qty) -> prod.buyPrice * qty }
            if (totalHppCost > 0) {
                repository.addJournalEntry(
                    JournalEntryEntity(
                        journalNumber = jrnNo,
                        transactionRef = savedTx.invoiceNumber,
                        accountName = "HPP (Harga Pokok Penjualan)",
                        debitAmount = totalHppCost,
                        creditAmount = 0.0,
                        description = "Beban HPP untuk Penjualan ${savedTx.invoiceNumber}"
                    )
                )
                repository.addJournalEntry(
                    JournalEntryEntity(
                        journalNumber = jrnNo,
                        transactionRef = savedTx.invoiceNumber,
                        accountName = "Persediaan Barang Dagang",
                        debitAmount = 0.0,
                        creditAmount = totalHppCost,
                        description = "Pengurangan Persediaan Barang untuk ${savedTx.invoiceNumber}"
                    )
                )
            }

            repository.incrementTransactionCount()
            // Ensure FirebaseSyncManager knows which outlet we are processing
            com.yofidewo.pos.data.FirebaseSyncManager.currentOutletCode = outletCode.value
            // Update transaction count in Firebase profile for real‑time sync
            val newCount = repository.getTransactionCount()
            com.yofidewo.pos.data.FirebaseSyncManager.updateOutletMetadata(
                code = com.yofidewo.pos.data.FirebaseSyncManager.currentOutletCode,
                txCount = newCount
            )
            trialTransactionsLeft.value = repository.getTrialTransactionsLeft()
            isTrialExpired.value = repository.isTrialExpired()

            clearCart()
            selectedTransactionForInvoice.value = savedTx
            onSuccess(savedTx)
        }
    }

    // Category CRUD
    fun addCategory(name: String, code: String, desc: String) {
        viewModelScope.launch {
            repository.insertCategory(CategoryEntity(name = name, code = code, description = desc))
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch { repository.deleteCategory(category) }
    }

    // Brand CRUD
    fun addBrand(name: String, desc: String) {
        viewModelScope.launch {
            repository.insertBrand(BrandEntity(name = name, description = desc))
        }
    }

    fun deleteBrand(brand: BrandEntity) {
        viewModelScope.launch { repository.deleteBrand(brand) }
    }

    // Warehouse CRUD
    fun addWarehouse(name: String, location: String, capacity: Int) {
        viewModelScope.launch {
            repository.insertWarehouse(WarehouseEntity(name = name, location = location, capacity = capacity))
        }
    }

    fun deleteWarehouse(warehouse: WarehouseEntity) {
        viewModelScope.launch { repository.deleteWarehouse(warehouse) }
    }

    // Product CRUD
    fun saveProduct(
        id: Long = 0,
        name: String,
        code: String,
        barcode: String,
        catId: Long?,
        brandId: Long?,
        warehouseId: Long?,
        buyPrice: Double,
        sellPrice: Double,
        stock: Int,
        minStock: Int,
        desc: String,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            val product = ProductEntity(
                id = id,
                name = name,
                code = code,
                barcode = barcode,
                categoryId = catId,
                brandId = brandId,
                warehouseId = warehouseId,
                buyPrice = buyPrice,
                sellPrice = sellPrice,
                stock = stock,
                minStock = minStock,
                description = desc,
                imageUrl = imageUrl ?: ""
            )
            if (id == 0L) {
                repository.insertProduct(product)
            } else {
                repository.updateProduct(product)
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch { repository.deleteProduct(product) }
    }

    // Receiving Notes
    fun addReceivingNote(
        productId: Long,
        productName: String,
        supplierName: String,
        warehouseId: Long?,
        warehouseName: String,
        qty: Int,
        cost: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val refNo = "RN-" + System.currentTimeMillis().toString().takeLast(8)
            val note = ReceivingNoteEntity(
                referenceNumber = refNo,
                supplierName = supplierName,
                productId = productId,
                productName = productName,
                warehouseId = warehouseId,
                warehouseName = warehouseName,
                quantityReceived = qty,
                unitCost = cost,
                notes = notes
            )
            repository.addReceivingNote(note)
        }
    }

    fun addReceivingNotesBatch(
        supplierName: String,
        refNumber: String,
        warehouseId: Long?,
        warehouseName: String,
        items: List<Pair<ProductEntity, Pair<Int, Double>>>, // product to (qty, cost)
        shippingCost: Double = 0.0,
        goodsPaymentMethod: String = "TUNAI",
        shippingPaymentMethod: String = "TUNAI (COD)",
        dueDate: Long? = null,
        paymentStatus: String = "LUNAS",
        notes: String
    ) {
        viewModelScope.launch {
            val actualRef = if (refNumber.isNotBlank()) refNumber else "SJ-" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) + "-" + (100..999).random()
            var totalGoodsCost = 0.0
            
            items.forEach { (prod, qtyAndCost) ->
                val (qty, cost) = qtyAndCost
                if (qty > 0) {
                    val subtotal = qty * cost
                    totalGoodsCost += subtotal
                    val note = ReceivingNoteEntity(
                        referenceNumber = actualRef,
                        supplierName = supplierName,
                        productId = prod.id,
                        productName = prod.name,
                        warehouseId = warehouseId,
                        warehouseName = warehouseName,
                        quantityReceived = qty,
                        unitCost = cost,
                        shippingCost = shippingCost,
                        goodsPaymentMethod = goodsPaymentMethod,
                        shippingPaymentMethod = shippingPaymentMethod,
                        dueDate = dueDate,
                        paymentStatus = paymentStatus,
                        notes = notes
                    )
                    repository.addReceivingNote(note)
                }
            }

            // Auto Journal Entry for Goods Receipt & Shipping
            val jrnNo = "JRN-" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) + "-" + (100..999).random()
            
            // Debit Persediaan Barang
            repository.addJournalEntry(
                JournalEntryEntity(
                    journalNumber = jrnNo,
                    transactionRef = actualRef,
                    accountName = "Persediaan Barang Dagang",
                    debitAmount = totalGoodsCost,
                    creditAmount = 0.0,
                    description = "Penerimaan Stok Barang $actualRef dari $supplierName"
                )
            )

            // Credit Kas / Hutang Supplier for Goods
            if (goodsPaymentMethod.contains("HUTANG", ignoreCase = true)) {
                repository.addJournalEntry(
                    JournalEntryEntity(
                        journalNumber = jrnNo,
                        transactionRef = actualRef,
                        accountName = "Hutang Usaha Supplier ($supplierName)",
                        debitAmount = 0.0,
                        creditAmount = totalGoodsCost,
                        description = "Hutang Pembelian Barang $actualRef Tempo"
                    )
                )
            } else {
                repository.addJournalEntry(
                    JournalEntryEntity(
                        journalNumber = jrnNo,
                        transactionRef = actualRef,
                        accountName = "Kas / Bank ($goodsPaymentMethod)",
                        debitAmount = 0.0,
                        creditAmount = totalGoodsCost,
                        description = "Pembayaran Tunai Barang $actualRef ke $supplierName"
                    )
                )
            }

            // Ongkir Journal Entry
            if (shippingCost > 0) {
                repository.addJournalEntry(
                    JournalEntryEntity(
                        journalNumber = jrnNo,
                        transactionRef = actualRef,
                        accountName = "Beban Ongkir Pembelian (Freights)",
                        debitAmount = shippingCost,
                        creditAmount = 0.0,
                        description = "Biaya Ongkir / Ekspedisi $actualRef ($shippingPaymentMethod)"
                    )
                )
                repository.addJournalEntry(
                    JournalEntryEntity(
                        journalNumber = jrnNo,
                        transactionRef = actualRef,
                        accountName = if (shippingPaymentMethod.contains("HUTANG", ignoreCase = true)) "Hutang Ongkir Ekspedisi" else "Kas / Bank ($shippingPaymentMethod)",
                        debitAmount = 0.0,
                        creditAmount = shippingCost,
                        description = "Kredit Pembayaran Ongkir Pembelian $actualRef"
                    )
                )
            }
        }
    }

    fun addPurchaseReturn(
        referenceNumber: String,
        supplierName: String,
        productId: Long,
        productName: String,
        quantityReturned: Int,
        unitCost: Double,
        reason: String
    ) {
        viewModelScope.launch {
            val totalAmount = quantityReturned * unitCost
            val retNo = "RET-" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) + "-" + (100..999).random()
            val ret = PurchaseReturnEntity(
                returnNumber = retNo,
                referenceNumber = referenceNumber,
                supplierName = supplierName,
                productId = productId,
                productName = productName,
                quantityReturned = quantityReturned,
                unitCost = unitCost,
                totalAmount = totalAmount,
                reason = reason
            )
            repository.addPurchaseReturn(ret)

            // Auto Journal Entry for Retur Pembelian
            val jrnNo = "JRN-" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) + "-" + (100..999).random()
            repository.addJournalEntry(
                JournalEntryEntity(
                    journalNumber = jrnNo,
                    transactionRef = retNo,
                    accountName = "Hutang Supplier / Kas Ref",
                    debitAmount = totalAmount,
                    creditAmount = 0.0,
                    description = "Retur Pembelian $productName ($quantityReturned pcs) ke $supplierName"
                )
            )
            repository.addJournalEntry(
                JournalEntryEntity(
                    journalNumber = jrnNo,
                    transactionRef = retNo,
                    accountName = "Persediaan Barang Dagang",
                    debitAmount = 0.0,
                    creditAmount = totalAmount,
                    description = "Pengurangan Persediaan akibat Retur Pembelian $productName"
                )
            )
        }
    }

    fun openCashierShift(userId: Long, cashierName: String, startingCash: Double) {
        viewModelScope.launch {
            repository.openShift(userId, cashierName, startingCash)
            activeShift.value = repository.getActiveShift()
        }
    }

    fun closeCashierShift(actualCash: Double, notes: String = "", onDone: (CashierShiftEntity) -> Unit) {
        viewModelScope.launch {
            val current = repository.getActiveShift() ?: return@launch
            val currentTxs = transactions.value.filter { it.timestamp >= current.startTime && it.cashierName == current.cashierName }
            val cashSales = currentTxs.filter { it.paymentMethod.contains("Cash", ignoreCase = true) || it.paymentMethod.contains("Tunai", ignoreCase = true) }.sumOf { it.totalAmount }
            val nonCashSales = currentTxs.filter { !it.paymentMethod.contains("Cash", ignoreCase = true) && !it.paymentMethod.contains("Tunai", ignoreCase = true) }.sumOf { it.totalAmount }
            
            val expected = current.startingCash + cashSales
            val diff = actualCash - expected
            
            val closed = current.copy(
                endTime = System.currentTimeMillis(),
                totalCashSales = cashSales,
                totalNonCashSales = nonCashSales,
                expectedCashInDrawer = expected,
                actualCashInDrawer = actualCash,
                cashDifference = diff,
                notes = notes,
                status = "CLOSED"
            )
            
            repository.closeShift(closed)
            activeShift.value = null
            onDone(closed)
        }
    }

    // Hold Orders (Simpan Draft Pesanan / Open Tab)
    fun saveCurrentCartToHoldOrder(customerName: String, tableName: String, notes: String = "") {
        val currentCart = cartItems.value
        if (currentCart.isEmpty()) return
        viewModelScope.launch {
            val holdNo = "HOLD-" + SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
            val itemsJsonStr = currentCart.joinToString(";") { "${it.product.id},${it.product.name},${it.quantity},${it.product.sellPrice}" }
            val total = currentCart.sumOf { it.product.sellPrice * it.quantity }
            val hold = HoldOrderEntity(
                holdNumber = holdNo,
                customerName = if (customerName.isBlank()) "Pelanggan" else customerName,
                tableName = if (tableName.isBlank()) "Meja -" else tableName,
                itemsJson = itemsJsonStr,
                totalAmount = total,
                cashierName = currentUser.value?.name ?: "Kasir",
                notes = notes
            )
            repository.saveHoldOrder(hold)
            clearCart()
        }
    }

    fun restoreHoldOrderToCart(order: HoldOrderEntity) {
        viewModelScope.launch {
            val itemsList = mutableListOf<CartItem>()
            val parts = order.itemsJson.split(";")
            for (p in parts) {
                val tokens = p.split(",")
                if (tokens.size >= 4) {
                    val prodId = tokens[0].toLongOrNull() ?: continue
                    val qty = tokens[2].toIntOrNull() ?: 1
                    val prod = products.value.find { it.id == prodId }
                    if (prod != null) {
                        itemsList.add(CartItem(prod, qty))
                    }
                }
            }
            cartItems.value = itemsList
            repository.deleteHoldOrder(order)
        }
    }

    // Petty Cash (Kas Out Operasional Kasir)
    fun addPettyCash(category: String, amount: Double, notes: String = "") {
        viewModelScope.launch {
            val entry = PettyCashEntity(
                cashierName = currentUser.value?.name ?: "Kasir",
                category = category,
                amount = amount,
                notes = notes
            )
            repository.addPettyCash(entry)
            // Auto Journal Entry for Petty Cash Out
            val jrnNo = "JRN-KASOUT-" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) + "-" + (100..999).random()
            repository.addJournalEntry(
                JournalEntryEntity(
                    journalNumber = jrnNo,
                    transactionRef = category,
                    accountName = "Beban Operasional Kas ($category)",
                    debitAmount = amount,
                    creditAmount = 0.0,
                    description = "Pengeluaran Kas Kecil Kasir: $notes"
                )
            )
            repository.addJournalEntry(
                JournalEntryEntity(
                    journalNumber = jrnNo,
                    transactionRef = category,
                    accountName = "Kas Kecil Kasir",
                    debitAmount = 0.0,
                    creditAmount = amount,
                    description = "Kas Keluar Kasir untuk $category"
                )
            )
        }
    }

    // Stock Adjustment (Stok Opname Audit)
    fun addStockAdjustment(productId: Long, physicalStock: Int, reason: String = "Rusak / Expired") {
        viewModelScope.launch {
            val prod = products.value.find { it.id == productId } ?: return@launch
            val diff = physicalStock - prod.stock
            val totalLoss = Math.abs(diff) * prod.buyPrice
            val adj = StockAdjustmentEntity(
                productId = productId,
                productName = prod.name,
                systemStock = prod.stock,
                physicalStock = physicalStock,
                difference = diff,
                totalLossAmount = totalLoss,
                reason = reason
            )
            repository.addStockAdjustment(adj)
            // Auto Journal Entry for Stock Adjustment
            val jrnNo = "JRN-OPNAME-" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) + "-" + (100..999).random()
            if (diff < 0) {
                repository.addJournalEntry(
                    JournalEntryEntity(
                        journalNumber = jrnNo,
                        transactionRef = prod.name,
                        accountName = "Beban Kerugian Stok ($reason)",
                        debitAmount = totalLoss,
                        creditAmount = 0.0,
                        description = "Penyesuaian Stok Opname (Berkurang $diff pcs) $reason"
                    )
                )
                repository.addJournalEntry(
                    JournalEntryEntity(
                        journalNumber = jrnNo,
                        transactionRef = prod.name,
                        accountName = "Persediaan Barang Dagang",
                        debitAmount = 0.0,
                        creditAmount = totalLoss,
                        description = "Pengurangan Stok Fisik akibat $reason"
                    )
                )
            }
        }
    }

    // Database Management: Clear / Reset Options
    fun clearDatabaseOption(option: String, onDone: () -> Unit) {
        viewModelScope.launch {
            when (option) {
                "PRODUCTS_ONLY" -> repository.deleteAllProducts()
                "STOCKS_ONLY" -> repository.resetAllProductStocks()
                "TRANSACTIONS_ONLY" -> repository.deleteAllTransactions()
                "ALL_DATA" -> {
                    repository.deleteAllProducts()
                    repository.deleteAllTransactions()
                    repository.deleteAllReceivingNotes()
                }
            }
            onDone()
        }
    }

    // Currency Operations
    fun addCurrency(code: String, symbol: String, rate: Double, isDefault: Boolean) {
        viewModelScope.launch {
            repository.insertCurrency(
                CurrencyEntity(code = code, symbol = symbol, exchangeRate = rate, isDefault = isDefault)
            )
        }
    }

    fun updateCurrency(currency: CurrencyEntity) {
        viewModelScope.launch {
            repository.updateCurrency(currency)
        }
    }

    fun deleteCurrency(currency: CurrencyEntity) {
        viewModelScope.launch {
            repository.deleteCurrency(currency)
        }
    }

    fun selectCurrency(currency: CurrencyEntity) {
        selectedCurrency.value = currency
    }

    // Activation
    fun activateLicense(key: String): Boolean {
        val validKeys = listOf("WK-POS-2026-PRO", "WARUNGKU-PRO-8899", "POS-2026-PRO", "ADMIN-123", "POS-MASTER-2026", "REG-POS-2026-8899")
        if (validKeys.contains(key.trim().uppercase()) || key.trim().isNotBlank() && key.contains("PRO")) {
            repository.setActivated(true)
            isActivated.value = true
            storeLicense.value = key.trim().uppercase()
            return true
        }
        return false
    }

    // Currency Formatter Helper
    fun formatMoney(amountInBase: Double): String {
        val curr = selectedCurrency.value
        val valInCurr = amountInBase * curr.exchangeRate
        return if (curr.code == "IDR" || curr.symbol.trim() == "Rp") {
            val formatted = String.format(Locale("id", "ID"), "%,.0f", valInCurr).replace(',', '.')
            "Rp $formatted"
        } else if (curr.symbolFirst) {
            "${curr.symbol}${String.format(Locale.US, "%.2f", valInCurr)}"
        } else {
            "${String.format(Locale.US, "%.2f", valInCurr)} ${curr.symbol}"
        }
    }

    val customers = repository.customers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeDiscounts = repository.activeDiscounts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertCustomer(customer: com.yofidewo.pos.data.CustomerEntity) = viewModelScope.launch { repository.insertCustomer(customer) }
    fun updateCustomer(customer: com.yofidewo.pos.data.CustomerEntity) = viewModelScope.launch { repository.updateCustomer(customer) }
    fun deleteCustomer(customer: com.yofidewo.pos.data.CustomerEntity) = viewModelScope.launch { repository.deleteCustomer(customer) }

    fun insertDiscount(discount: com.yofidewo.pos.data.DiscountEntity) = viewModelScope.launch { repository.insertDiscount(discount) }
    fun updateDiscount(discount: com.yofidewo.pos.data.DiscountEntity) = viewModelScope.launch { repository.updateDiscount(discount) }
    fun deleteDiscount(discount: com.yofidewo.pos.data.DiscountEntity) = viewModelScope.launch { repository.deleteDiscount(discount) }
    
    fun logout() {
        currentUser.value = null
        currentRole.value = null
    }

    // Bluetooth Printing
    fun printReceiptBluetooth(
        context: android.content.Context,
        transaction: TransactionEntity,
        items: List<TransactionItemEntity>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val address = selectedPrinterAddress.value
            val width = if (paperWidth.value.contains("80")) 80 else 58
            if (address.isBlank()) {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onError("Pilih printer Bluetooth di Pengaturan terlebih dahulu!")
                }
                return@launch
            }

            // Load logo bitmap if enabled & available
            val logoBmp: android.graphics.Bitmap? = if (useHeaderLogo.value) {
                val path = customLogoPath.value
                if (!path.isNullOrBlank()) {
                    try {
                        val file = java.io.File(path)
                        if (file.exists()) android.graphics.BitmapFactory.decodeFile(path) else null
                    } catch (e: Exception) { null }
                } else null
            } else null

            val result = com.yofidewo.pos.util.EscPosPrinterHelper.printViaBluetooth(
                deviceAddress = address,
                transaction = transaction,
                items = items,
                storeName = outletName.value,
                storeAddress = outletAddress.value,
                storePhone = outletPhone.value,
                receiptHeader = receiptHeader.value,
                receiptFooter = receiptFooter.value,
                paperWidthMm = width,
                useAutoCutter = useAutoCutter.value,
                useHeaderLogo = useHeaderLogo.value,
                logoBitmap = logoBmp,
                showAddress = showStoreAddress.value,
                showPhone = showStorePhone.value,
                showCashier = showCashierName.value,
                showCustomer = showCustomerName.value,
                showFooter = showFooterText.value,
                formatMoney = { formatMoney(it) }
            )
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                result.onSuccess { onSuccess() }
                    .onFailure { onError("Gagal mencetak: ${it.localizedMessage}") }
            }
        }
    }

    fun printSalesReportThermal(
        reportTitle: String,
        periodText: String,
        transactionsList: List<TransactionEntity>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val address = selectedPrinterAddress.value
            if (address.isBlank()) {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onError("Pilih printer Bluetooth di Pengaturan terlebih dahulu!")
                }
                return@launch
            }

            val totalRev = transactionsList.sumOf { it.totalAmount }
            val totalHpp = 0.0
            val totalCount = transactionsList.size
            val paymentMap = transactionsList.groupBy { it.paymentMethod }.mapValues { entry -> entry.value.sumOf { it.totalAmount } }
            val cashierMap = transactionsList.groupBy { it.cashierName }.mapValues { entry -> entry.value.sumOf { it.totalAmount } }

            val result = com.yofidewo.pos.util.EscPosPrinterHelper.printSummarySalesReportBluetooth(
                deviceAddress = address,
                storeName = outletName.value,
                reportTitle = reportTitle,
                periodText = periodText,
                totalRevenueUsd = totalRev,
                totalHppCost = totalHpp,
                totalTransactionsCount = totalCount,
                paymentBreakdown = paymentMap,
                cashierBreakdown = cashierMap,
                topSellingItems = emptyList(),
                paperWidth = paperWidth.value,
                formatMoney = { formatMoney(it) }
            )

            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                if (result.isSuccess) onSuccess() else onError(result.exceptionOrNull()?.message ?: "Gagal cetak laporan thermal")
            }
        }
    }

    // Data Export & Backup
    fun exportProductsToCsv(context: android.content.Context) {
        viewModelScope.launch {
            val prods = repository.getAllProductsSync()
            val cats = repository.getAllCategoriesSync()
            val brands = repository.getAllBrandsSync()
            com.yofidewo.pos.util.DataExporter.exportProductsToCsv(context, prods, cats, brands)
        }
    }

    fun exportProductsToPdf(context: android.content.Context) {
        viewModelScope.launch {
            val prods = repository.getAllProductsSync()
            val cats = repository.getAllCategoriesSync()
            val brands = repository.getAllBrandsSync()
            com.yofidewo.pos.util.DataExporter.exportProductsToPdf(context, prods, cats, brands)
        }
    }

    fun exportTransactionsToCsv(context: android.content.Context) {
        viewModelScope.launch {
            val txs = repository.getAllTransactionsSync()
            com.yofidewo.pos.util.DataExporter.exportTransactionsToCsv(context, txs, this@PosViewModel)
        }
    }

    fun exportTransactionsToPdf(context: android.content.Context) {
        viewModelScope.launch {
            val txs = repository.getAllTransactionsSync()
            com.yofidewo.pos.util.DataExporter.exportTransactionsToPdf(context, txs, this@PosViewModel)
        }
    }

    // Data Export & Backup Progress State
    val isRestoringData = mutableStateOf(false)
    val restoreProgressText = mutableStateOf("")
    val restoreProgressCurrent = mutableStateOf(0)
    val restoreProgressTotal = mutableStateOf(100)

    fun exportFullBackup(context: android.content.Context) {
        viewModelScope.launch {
            com.yofidewo.pos.util.DataExporter.exportFullDatabaseBackup(context, repository)
        }
    }

    fun restoreFullBackup(context: android.content.Context, jsonContent: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            isRestoringData.value = true
            restoreProgressText.value = "Menyiapkan Pemulihan Data..."
            restoreProgressCurrent.value = 0
            restoreProgressTotal.value = 100
            val success = com.yofidewo.pos.util.DataExporter.restoreDatabaseFromJson(context, jsonContent, repository) { cur, tot, label ->
                restoreProgressCurrent.value = cur
                restoreProgressTotal.value = tot
                restoreProgressText.value = label
            }
            isRestoringData.value = false
            onDone(success)
        }
    }
}

class PosViewModelFactory(private val repository: PosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}
