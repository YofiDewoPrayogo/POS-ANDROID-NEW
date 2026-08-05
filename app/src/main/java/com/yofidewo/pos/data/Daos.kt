package com.yofidewo.pos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoleDao {
    @Query("SELECT * FROM roles ORDER BY name ASC")
    fun getAllRoles(): Flow<List<RoleEntity>>

    @Query("SELECT * FROM roles WHERE id = :id LIMIT 1")
    suspend fun getRoleById(id: Long): RoleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRole(role: RoleEntity): Long

    @Update
    suspend fun updateRole(role: RoleEntity)

    @Delete
    suspend fun deleteRole(role: RoleEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users")
    suspend fun getAllUsersSync(): List<UserEntity>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesSync(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}

@Dao
interface BrandDao {
    @Query("SELECT * FROM brands ORDER BY name ASC")
    fun getAllBrands(): Flow<List<BrandEntity>>

    @Query("SELECT * FROM brands")
    suspend fun getAllBrandsSync(): List<BrandEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brand: BrandEntity): Long

    @Update
    suspend fun updateBrand(brand: BrandEntity)

    @Delete
    suspend fun deleteBrand(brand: BrandEntity)
}

@Dao
interface WarehouseDao {
    @Query("SELECT * FROM warehouses ORDER BY name ASC")
    fun getAllWarehouses(): Flow<List<WarehouseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarehouse(warehouse: WarehouseEntity): Long

    @Update
    suspend fun updateWarehouse(warehouse: WarehouseEntity)

    @Delete
    suspend fun deleteWarehouse(warehouse: WarehouseEntity)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products")
    suspend fun getAllProductsSync(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE code = :code LIMIT 1")
    suspend fun getProductByCode(code: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Query("UPDATE products SET stock = 0")
    suspend fun resetAllProductStocks()

    @Query("UPDATE products SET stock = stock + :quantityDelta WHERE id = :productId")
    suspend fun updateProductStock(productId: Long, quantityDelta: Int)
}

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies ORDER BY isDefault DESC, code ASC")
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultCurrency(): CurrencyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: CurrencyEntity): Long

    @Update
    suspend fun updateCurrency(currency: CurrencyEntity)

    @Delete
    suspend fun deleteCurrency(currency: CurrencyEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactionsSync(): List<TransactionEntity>

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionItems(items: List<TransactionItemEntity>)

    @androidx.room.Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    fun getItemsForTransaction(transactionId: Long): Flow<List<TransactionItemEntity>>

    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    suspend fun getItemsForTransactionSync(transactionId: Long): List<TransactionItemEntity>

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM transaction_items")
    suspend fun deleteAllTransactionItems()
}

@Dao
interface ReceivingNoteDao {
    @Query("SELECT * FROM receiving_notes ORDER BY timestamp DESC")
    fun getAllReceivingNotes(): Flow<List<ReceivingNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceivingNote(note: ReceivingNoteEntity): Long

    @Query("DELETE FROM receiving_notes")
    suspend fun deleteAllReceivingNotes()
}

@Dao
interface PurchaseReturnDao {
    @Query("SELECT * FROM purchase_returns ORDER BY timestamp DESC")
    fun getAllPurchaseReturns(): Flow<List<PurchaseReturnEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchaseReturn(ret: PurchaseReturnEntity): Long

    @Query("DELETE FROM purchase_returns")
    suspend fun deleteAllPurchaseReturns()
}

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity): Long

    @Query("DELETE FROM journal_entries")
    suspend fun deleteAllJournalEntries()
}

@Dao
interface CashierShiftDao {
    @Query("SELECT * FROM cashier_shifts ORDER BY startTime DESC")
    fun getAllShifts(): Flow<List<CashierShiftEntity>>

    @Query("SELECT * FROM cashier_shifts WHERE status = 'OPEN' ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveShift(): CashierShiftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: CashierShiftEntity): Long

    @Update
    suspend fun updateShift(shift: CashierShiftEntity)
}

@Dao
interface HoldOrderDao {
    @Query("SELECT * FROM hold_orders ORDER BY timestamp DESC")
    fun getAllHoldOrders(): Flow<List<HoldOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoldOrder(order: HoldOrderEntity): Long

    @Delete
    suspend fun deleteHoldOrder(order: HoldOrderEntity)
}

@Dao
interface PettyCashDao {
    @Query("SELECT * FROM petty_cash ORDER BY timestamp DESC")
    fun getAllPettyCash(): Flow<List<PettyCashEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPettyCash(entry: PettyCashEntity): Long
}

@Dao
interface StockAdjustmentDao {
    @Query("SELECT * FROM stock_adjustments ORDER BY timestamp DESC")
    fun getAllStockAdjustments(): Flow<List<StockAdjustmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockAdjustment(adj: StockAdjustmentEntity): Long
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): kotlinx.coroutines.flow.Flow<List<CustomerEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long
    @Update
    suspend fun updateCustomer(customer: CustomerEntity)
    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)
}

@Dao
interface DiscountDao {
    @Query("SELECT * FROM discounts WHERE isActive = 1")
    fun getActiveDiscounts(): kotlinx.coroutines.flow.Flow<List<DiscountEntity>>

    @Query("SELECT * FROM discounts")
    fun getAllDiscounts(): kotlinx.coroutines.flow.Flow<List<DiscountEntity>>

    @Query("SELECT * FROM discounts")
    suspend fun getAllDiscountsSync(): List<DiscountEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscount(discount: DiscountEntity): Long
    @Update
    suspend fun updateDiscount(discount: DiscountEntity)
    @Delete
    suspend fun deleteDiscount(discount: DiscountEntity)
}
