package com.yofidewo.pos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RoleEntity::class,
        UserEntity::class,
        CategoryEntity::class,
        BrandEntity::class,
        WarehouseEntity::class,
        ProductEntity::class,
        CurrencyEntity::class,
        TransactionEntity::class,
        TransactionItemEntity::class,
        ReceivingNoteEntity::class,
        PurchaseReturnEntity::class,
        JournalEntryEntity::class,
        CustomerEntity::class,
        DiscountEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class PosDatabase : RoomDatabase() {
    abstract fun roleDao(): RoleDao
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun brandDao(): BrandDao
    abstract fun warehouseDao(): WarehouseDao
    abstract fun productDao(): ProductDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun customerDao(): CustomerDao
    abstract fun discountDao(): DiscountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun receivingNoteDao(): ReceivingNoteDao
    abstract fun purchaseReturnDao(): PurchaseReturnDao
    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        @Volatile
        private var INSTANCE: PosDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PosDatabase::class.java,
                    "pos_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class PosDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

                suspend fun populateInitialData(db: PosDatabase) {
            val roleDao = db.roleDao()
            val userDao = db.userDao()
            val categoryDao = db.categoryDao()
            val brandDao = db.brandDao()
            val warehouseDao = db.warehouseDao()
            val productDao = db.productDao()
            val currencyDao = db.currencyDao()

            val roleAdminId = roleDao.insertRole(RoleEntity(name = "Administrator", canViewDashboard = true, canViewCashier = true, canViewProducts = true, canViewReports = true, canViewSettings = true))
            val roleKasirId = roleDao.insertRole(RoleEntity(name = "Kasir", canViewDashboard = false, canViewCashier = true, canViewProducts = false, canViewReports = false, canViewSettings = false))

            // Users
            userDao.insertUser(UserEntity(name = "Administrator", email = "admin@pos.com", pin = "1234", roleId = roleAdminId))
            userDao.insertUser(UserEntity(name = "Kasir Toko", email = "kasir@pos.com", pin = "1111", roleId = roleKasirId))

            // Categories
            val catMakanan = categoryDao.insertCategory(CategoryEntity(name = "Makanan", code = "MKN", description = "Makanan pokok dan ringan"))
            val catMinuman = categoryDao.insertCategory(CategoryEntity(name = "Minuman", code = "MNM", description = "Minuman segar dan kemasan"))
            val catRumahTangga = categoryDao.insertCategory(CategoryEntity(name = "Rumah Tangga", code = "RTG", description = "Kebutuhan harian rumah tangga"))
            
            // Brands
            val brandIndofood = brandDao.insertBrand(BrandEntity(name = "Indofood", description = "Makanan dan Minuman"))
            val brandMayora = brandDao.insertBrand(BrandEntity(name = "Mayora", description = "Makanan Ringan"))
            val brandUnilever = brandDao.insertBrand(BrandEntity(name = "Unilever", description = "Kebutuhan Mandi & Cuci"))
            
            // Warehouses
            val whToko = warehouseDao.insertWarehouse(WarehouseEntity(name = "Rak Toko", location = "Depan", capacity = 1000, status = "Aktif"))
            val whGudang = warehouseDao.insertWarehouse(WarehouseEntity(name = "Gudang Belakang", location = "Belakang", capacity = 5000, status = "Aktif"))

            // Currencies
            currencyDao.insertCurrency(CurrencyEntity(code = "IDR", symbol = "Rp", exchangeRate = 1.0, symbolFirst = true, isDefault = true))

            // Products
            productDao.insertProduct(ProductEntity(
                name = "Indomie Goreng Original", code = "PRD-001", barcode = "89686010",
                categoryId = catMakanan, brandId = brandIndofood, warehouseId = whToko,
                buyPrice = 2800.0, sellPrice = 3500.0, stock = 120, minStock = 40, description = "Mie instan goreng paling populer"
            ))
            productDao.insertProduct(ProductEntity(
                name = "Beras Maknyus 5kg", code = "PRD-002", barcode = "899999912",
                categoryId = catMakanan, brandId = null, warehouseId = whGudang,
                buyPrice = 65000.0, sellPrice = 72000.0, stock = 25, minStock = 5, description = "Beras pulen kualitas super"
            ))
            productDao.insertProduct(ProductEntity(
                name = "Minyak Goreng Bimoli 2L", code = "PRD-003", barcode = "899888877",
                categoryId = catMakanan, brandId = brandIndofood, warehouseId = whToko,
                buyPrice = 33000.0, sellPrice = 36500.0, stock = 40, minStock = 10, description = "Minyak goreng kelapa sawit pouch"
            ))
            productDao.insertProduct(ProductEntity(
                name = "Gula Pasir Gulaku 1kg", code = "PRD-004", barcode = "899111222",
                categoryId = catMakanan, brandId = null, warehouseId = whToko,
                buyPrice = 14500.0, sellPrice = 16000.0, stock = 60, minStock = 15, description = "Gula pasir putih kemasan"
            ))
            productDao.insertProduct(ProductEntity(
                name = "Teh Pucuk Harum 350ml", code = "PRD-005", barcode = "899555444",
                categoryId = catMinuman, brandId = brandMayora, warehouseId = whToko,
                buyPrice = 3000.0, sellPrice = 4500.0, stock = 100, minStock = 24, description = "Minuman teh melati"
            ))
            productDao.insertProduct(ProductEntity(
                name = "Le Minerale 600ml", code = "PRD-006", barcode = "899444333",
                categoryId = catMinuman, brandId = brandMayora, warehouseId = whToko,
                buyPrice = 2500.0, sellPrice = 4000.0, stock = 200, minStock = 48, description = "Air mineral botol"
            ))
            productDao.insertProduct(ProductEntity(
                name = "Kopi Kapal Api Mix 25g", code = "PRD-007", barcode = "899777666",
                categoryId = catMinuman, brandId = null, warehouseId = whToko,
                buyPrice = 1200.0, sellPrice = 2000.0, stock = 300, minStock = 50, description = "Kopi bubuk plus gula kemasan sachet"
            ))
            productDao.insertProduct(ProductEntity(
                name = "Sabun Mandi Lifebuoy Merah", code = "PRD-008", barcode = "899333222",
                categoryId = catRumahTangga, brandId = brandUnilever, warehouseId = whToko,
                buyPrice = 3500.0, sellPrice = 5000.0, stock = 45, minStock = 12, description = "Sabun batang anti kuman"
            ))
            productDao.insertProduct(ProductEntity(
                name = "Sampo Clear Men 160ml", code = "PRD-009", barcode = "899222111",
                categoryId = catRumahTangga, brandId = brandUnilever, warehouseId = whToko,
                buyPrice = 22000.0, sellPrice = 28000.0, stock = 15, minStock = 5, description = "Sampo anti ketombe"
            ))

            // Discounts
            val discountDao = db.discountDao()
            discountDao.insertDiscount(DiscountEntity(name = "Tanpa Diskon", type = "PERCENT", value = 0.0, isActive = true))
            discountDao.insertDiscount(DiscountEntity(name = "Diskon Member (5%)", type = "PERCENT", value = 5.0, isActive = true))
            discountDao.insertDiscount(DiscountEntity(name = "Diskon Promo (10%)", type = "PERCENT", value = 10.0, isActive = true))
            discountDao.insertDiscount(DiscountEntity(name = "Diskon Khusus (15%)", type = "PERCENT", value = 15.0, isActive = true))
            discountDao.insertDiscount(DiscountEntity(name = "Potongan Harga (Rp 5.000)", type = "FIXED", value = 5000.0, isActive = true))
        }
    }
}
