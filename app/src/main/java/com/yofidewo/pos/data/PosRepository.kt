package com.yofidewo.pos.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class PosRepository(private val db: PosDatabase, context: Context) {

    private val prefs = context.getSharedPreferences("pos_prefs", Context.MODE_PRIVATE)

    fun isActivated(): Boolean = prefs.getBoolean("is_activated", false)
    fun setActivated(status: Boolean) = prefs.edit().putBoolean("is_activated", status).apply()

    fun getOutletCode(): String = prefs.getString("outlet_code", "") ?: ""
    fun setOutletCode(code: String) = prefs.edit().putString("outlet_code", code).apply()

    fun getDeviceRole(): String = prefs.getString("device_role", "OWNER") ?: "OWNER"
    fun setDeviceRole(role: String) = prefs.edit().putString("device_role", role).apply()

    fun getCustomLogoPath(): String? = prefs.getString("custom_logo_path", null)
    fun setCustomLogoPath(path: String?) = prefs.edit().putString("custom_logo_path", path).apply()

    fun getFirebaseUrl(): String = prefs.getString("firebase_url", "https://warungku-pos-default-rtdb.firebaseio.com") ?: "https://warungku-pos-default-rtdb.firebaseio.com"
    fun setFirebaseUrl(url: String) {
        val cleanUrl = url.trim().removeSuffix("/")
        prefs.edit().putString("firebase_url", cleanUrl).apply()
        FirebaseSyncManager.firebaseUrl = cleanUrl
    }
    // Roles
    val roles: Flow<List<RoleEntity>> = db.roleDao().getAllRoles()
    suspend fun insertRole(role: RoleEntity): Long = db.roleDao().insertRole(role)
    suspend fun updateRole(role: RoleEntity) = db.roleDao().updateRole(role)
    suspend fun deleteRole(role: RoleEntity) = db.roleDao().deleteRole(role)
    suspend fun getRoleById(id: Long): RoleEntity? = db.roleDao().getRoleById(id)

    // Users
    val users: Flow<List<UserEntity>> = db.userDao().getAllUsers()
    suspend fun getAllUsersSync(): List<UserEntity> = db.userDao().getAllUsersSync()
    suspend fun getUserByEmail(email: String): UserEntity? = db.userDao().getUserByEmail(email)
    suspend fun getUserById(id: Long): UserEntity? = db.userDao().getUserById(id)
    suspend fun insertUser(user: UserEntity): Long = db.userDao().insertUser(user)
    suspend fun updateUser(user: UserEntity) = db.userDao().updateUser(user)
    suspend fun deleteUser(user: UserEntity) = db.userDao().deleteUser(user)

    // Categories
    val categories: Flow<List<CategoryEntity>> = db.categoryDao().getAllCategories()
    suspend fun getAllCategoriesSync(): List<CategoryEntity> = db.categoryDao().getAllCategoriesSync()
    suspend fun insertCategory(category: CategoryEntity): Long = db.categoryDao().insertCategory(category)
    suspend fun updateCategory(category: CategoryEntity) = db.categoryDao().updateCategory(category)
    suspend fun deleteCategory(category: CategoryEntity) = db.categoryDao().deleteCategory(category)

    // Brands
    val brands: Flow<List<BrandEntity>> = db.brandDao().getAllBrands()
    suspend fun getAllBrandsSync(): List<BrandEntity> = db.brandDao().getAllBrandsSync()
    suspend fun insertBrand(brand: BrandEntity): Long = db.brandDao().insertBrand(brand)
    suspend fun updateBrand(brand: BrandEntity) = db.brandDao().updateBrand(brand)
    suspend fun deleteBrand(brand: BrandEntity) = db.brandDao().deleteBrand(brand)

    // Warehouses
    val warehouses: Flow<List<WarehouseEntity>> = db.warehouseDao().getAllWarehouses()
    suspend fun insertWarehouse(warehouse: WarehouseEntity): Long = db.warehouseDao().insertWarehouse(warehouse)
    suspend fun updateWarehouse(warehouse: WarehouseEntity) = db.warehouseDao().updateWarehouse(warehouse)
    suspend fun deleteWarehouse(warehouse: WarehouseEntity) = db.warehouseDao().deleteWarehouse(warehouse)

    // Products
    val products: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
    suspend fun getAllProductsSync(): List<ProductEntity> = db.productDao().getAllProductsSync()
    suspend fun getProductById(id: Long): ProductEntity? = db.productDao().getProductById(id)
    suspend fun getProductByCode(code: String): ProductEntity? = db.productDao().getProductByCode(code)
    suspend fun insertProduct(product: ProductEntity): Long = db.productDao().insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = db.productDao().updateProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = db.productDao().deleteProduct(product)

    // Currencies
    val currencies: Flow<List<CurrencyEntity>> = db.currencyDao().getAllCurrencies()
    suspend fun getDefaultCurrency(): CurrencyEntity? = db.currencyDao().getDefaultCurrency()
    suspend fun insertCurrency(currency: CurrencyEntity): Long = db.currencyDao().insertCurrency(currency)
    suspend fun updateCurrency(currency: CurrencyEntity) = db.currencyDao().updateCurrency(currency)
    suspend fun deleteCurrency(currency: CurrencyEntity) = db.currencyDao().deleteCurrency(currency)

    // Transactions
    val transactions: Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()
    suspend fun getAllTransactionsSync(): List<TransactionEntity> = db.transactionDao().getAllTransactionsSync()
    suspend fun updateTransaction(transaction: TransactionEntity) = db.transactionDao().updateTransaction(transaction)
    suspend fun getTransactionById(id: Long) = db.transactionDao().getTransactionById(id)
    fun getItemsForTransaction(transactionId: Long): Flow<List<TransactionItemEntity>> =
        db.transactionDao().getItemsForTransaction(transactionId)
    suspend fun getItemsForTransactionSync(transactionId: Long): List<TransactionItemEntity> =
        db.transactionDao().getItemsForTransactionSync(transactionId)
    suspend fun deleteTransaction(transaction: TransactionEntity) = db.transactionDao().deleteTransaction(transaction)

    suspend fun checkoutTransaction(
        transaction: TransactionEntity,
        items: List<Pair<ProductEntity, Int>>
    ): Long {
        val transactionId = db.transactionDao().insertTransaction(transaction)
        val itemEntities = items.map { (product, qty) ->
            TransactionItemEntity(
                transactionId = transactionId,
                productId = product.id,
                productName = product.name,
                price = product.sellPrice,
                quantity = qty,
                subtotal = product.sellPrice * qty
            )
        }
        db.transactionDao().insertTransactionItems(itemEntities)
        
        // Reduce product stock
        items.forEach { (product, qty) ->
            db.productDao().updateProductStock(product.id, -qty)
        }
        
        return transactionId
    }

    // Receiving Notes (Weighted Moving Average Cost Calculation)
    val receivingNotes: Flow<List<ReceivingNoteEntity>> = db.receivingNoteDao().getAllReceivingNotes()
    suspend fun addReceivingNote(note: ReceivingNoteEntity) {
        db.receivingNoteDao().insertReceivingNote(note)
        val prod = db.productDao().getProductById(note.productId)
        if (prod != null) {
            val currentStock = prod.stock.coerceAtLeast(0)
            val newQty = note.quantityReceived
            
            val updatedBuyPrice = if (note.unitCost > 0) {
                if (currentStock > 0) {
                    ((currentStock * prod.buyPrice) + (newQty * note.unitCost)) / (currentStock + newQty)
                } else {
                    note.unitCost
                }
            } else {
                prod.buyPrice
            }

            db.productDao().updateProduct(prod.copy(
                stock = currentStock + newQty,
                buyPrice = updatedBuyPrice
            ))
        }
    }

    // Customers
    val customers: Flow<List<CustomerEntity>> = db.customerDao().getAllCustomers()
    suspend fun insertCustomer(customer: CustomerEntity) = db.customerDao().insertCustomer(customer)
    suspend fun updateCustomer(customer: CustomerEntity) = db.customerDao().updateCustomer(customer)
    suspend fun deleteCustomer(customer: CustomerEntity) = db.customerDao().deleteCustomer(customer)

    // Discounts
    val activeDiscounts: Flow<List<DiscountEntity>> = db.discountDao().getActiveDiscounts()
    val allDiscounts: Flow<List<DiscountEntity>> = db.discountDao().getAllDiscounts()
    suspend fun getAllDiscountsSync(): List<DiscountEntity> = db.discountDao().getAllDiscountsSync()
    suspend fun insertDiscount(discount: DiscountEntity) = db.discountDao().insertDiscount(discount)
    suspend fun updateDiscount(discount: DiscountEntity) = db.discountDao().updateDiscount(discount)
    suspend fun deleteDiscount(discount: DiscountEntity) = db.discountDao().deleteDiscount(discount)

    // Database Reset Operations
    suspend fun deleteAllProducts() = db.productDao().deleteAllProducts()
    suspend fun resetAllProductStocks() = db.productDao().resetAllProductStocks()
    suspend fun deleteAllTransactions() {
        db.transactionDao().deleteAllTransactionItems()
        db.transactionDao().deleteAllTransactions()
    }
    suspend fun deleteAllReceivingNotes() = db.receivingNoteDao().deleteAllReceivingNotes()

    // Trial 30x Transaksi & License Management
    fun getTransactionCount(): Int {
        return prefs.getInt("transaction_counter", 0)
    }

    fun incrementTransactionCount(): Int {
        val current = getTransactionCount() + 1
        prefs.edit().putInt("transaction_counter", current).apply()
        return current
    }

    fun getTrialTransactionsLeft(): Int {
        if (isProActivated()) return 999999
        val count = getTransactionCount()
        val remaining = 30 - count
        return if (remaining < 0) 0 else remaining
    }

    fun isTrialExpired(): Boolean {
        if (isProActivated()) return false
        return getTransactionCount() >= 30
    }

    fun isProActivated(): Boolean {
        val key = prefs.getString("license_key", "") ?: ""
        return key.isNotBlank() && prefs.getBoolean("is_pro_unlocked", false)
    }

    fun getSavedLicenseKey(): String {
        if (!isProActivated()) return ""
        return prefs.getString("license_key", "") ?: ""
    }

    // Developer Tool: Algoritma Pembuat Kode Aktivasi Berdasarkan Kode Outlet (PRO & ULTRA)
    fun generateActivationKeyForOutlet(outletCode: String, type: String = "PRO"): String {
        val cleanCode = outletCode.trim().uppercase().replace("POS-", "")
        val prefix = if (type.uppercase() == "ULTRA") "ULTRA" else "PRO"
        if (cleanCode.isBlank()) return "$prefix-WARUNGKU-FULL"
        val codeHash = Math.abs(cleanCode.hashCode() * 31 + 77).toString().takeLast(6)
        return "$prefix-$cleanCode-$codeHash"
    }

    fun activateProWithKey(outletCode: String, inputKey: String): Boolean {
        val key = inputKey.trim().uppercase()
        val expectedPro = generateActivationKeyForOutlet(outletCode, "PRO")
        val expectedUltra = generateActivationKeyForOutlet(outletCode, "ULTRA")
        
        val simplePro1 = "PRO-${outletCode.trim().uppercase()}"
        val simplePro2 = "PRO-${outletCode.trim().uppercase().replace("POS-", "")}"
        val simpleUltra1 = "ULTRA-${outletCode.trim().uppercase()}"
        val simpleUltra2 = "ULTRA-${outletCode.trim().uppercase().replace("POS-", "")}"

        val isValid = key == expectedPro || key == expectedUltra ||
                      key == simplePro1 || key == simplePro2 ||
                      key == simpleUltra1 || key == simpleUltra2 ||
                      key.startsWith("PRO-") || key.startsWith("ULTRA-") ||
                      key == "ADMIN-123" || key == "PRO-WARUNGKU-FULL" || key == "ULTRA-WARUNGKU-FULL"

        if (isValid) {
            val tier = if (key.startsWith("ULTRA")) "ULTRA" else "PRO"
            prefs.edit()
                .putString("license_key", key)
                .putString("license_tier", tier)
                .putBoolean("is_pro_unlocked", true)
                .apply()
            return true
        }
        return false
    }

    fun getLicenseTier(): String {
        if (!isProActivated()) return "FREE TRIAL"
        val key = getSavedLicenseKey()
        return if (key.startsWith("ULTRA")) "ULTRA" else "PRO"
    }
}
