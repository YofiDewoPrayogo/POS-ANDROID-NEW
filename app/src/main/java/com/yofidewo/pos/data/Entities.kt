package com.yofidewo.pos.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "roles")
data class RoleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val canViewDashboard: Boolean = false,
    val canViewCashier: Boolean = false,
    val canViewProducts: Boolean = false,
    val canViewReports: Boolean = false,
    val canViewSettings: Boolean = false
)

@Entity(tableName = "users",
    foreignKeys = [
        ForeignKey(
            entity = RoleEntity::class,
            parentColumns = ["id"],
            childColumns = ["roleId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["roleId"])]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val pin: String = "1234",
    val roleId: Long?,
    val avatarUrl: String = ""
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String = "",
    val description: String = "",
    val printerTarget: String = "KASIR" // "KASIR", "KITCHEN", "BAR"
)

@Entity(tableName = "brands")
data class BrandEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = ""
)

@Entity(tableName = "warehouses")
data class WarehouseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val location: String = "",
    val capacity: Int = 1000,
    val status: String = "Active"
)

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = BrandEntity::class,
            parentColumns = ["id"],
            childColumns = ["brandId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = WarehouseEntity::class,
            parentColumns = ["id"],
            childColumns = ["warehouseId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["brandId"]),
        Index(value = ["warehouseId"])
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String, // SKU
    val barcode: String = "", // Barcode
    val categoryId: Long?,
    val brandId: Long?,
    val warehouseId: Long?,
    val buyPrice: Double,
    val sellPrice: Double,
    val stock: Int,
    val minStock: Int = 5,
    val description: String = "",
    val imageUrl: String = "",
    val printerTarget: String = "KASIR", // "KASIR", "KITCHEN", "BAR"
    val modifierOptions: String = "", // "Less Sugar, Normal Sugar, Less Ice"
    val wholesaleMinQty: Int = 0,
    val wholesalePrice: Double = 0.0,
    val unitName: String = "Pcs",
    val expiryDate: Long? = null,
    val isCombo: Boolean = false,
    val comboItemsJson: String = ""
)

@Entity(tableName = "hold_orders")
data class HoldOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val holdNumber: String,
    val customerName: String = "Pelanggan",
    val tableName: String = "Meja -",
    val itemsJson: String,
    val totalAmount: Double,
    val cashierName: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "petty_cash")
data class PettyCashEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cashierName: String,
    val category: String, // e.g. "Beli Es Batu", "Air Galon", "Plastik/Kresek"
    val amount: Double,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "stock_adjustments")
data class StockAdjustmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val systemStock: Int,
    val physicalStock: Int,
    val difference: Int,
    val totalLossAmount: Double = 0.0,
    val reason: String = "Rusak / Expired",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String, // USD, IDR, EUR, etc.
    val symbol: String, // $, Rp, €
    val exchangeRate: Double = 1.0, // relative to base currency
    val symbolFirst: Boolean = true,
    val isDefault: Boolean = false
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val userId: Long,
    val cashierName: String,
    val customerName: String = "General Customer",
    val customerId: Long? = null,
    val subTotalAmount: Double = 0.0,
    val discountName: String = "",
    val discountAmount: Double = 0.0,
    val totalAmount: Double,
    val paidAmount: Double,
    val changeAmount: Double,
    val currencyCode: String = "USD",
    val currencySymbol: String = "$",
    val paymentMethod: String = "Cash",
    val paymentStatus: String = "LUNAS",
    val notes: String = "",
    val status: String = "COMPLETED",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "transaction_items",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["transactionId"])]
)
data class TransactionItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long,
    val productId: Long,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val subtotal: Double,
    val selectedModifiers: String = ""
)

@Entity(tableName = "cashier_shifts")
data class CashierShiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val cashierName: String,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val startingCash: Double = 0.0,
    val totalCashSales: Double = 0.0,
    val totalNonCashSales: Double = 0.0,
    val expectedCashInDrawer: Double = 0.0,
    val actualCashInDrawer: Double = 0.0,
    val cashDifference: Double = 0.0,
    val notes: String = "",
    val status: String = "OPEN"
)

@Entity(tableName = "receiving_notes")
data class ReceivingNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val referenceNumber: String,
    val supplierName: String,
    val productId: Long,
    val productName: String,
    val warehouseId: Long?,
    val warehouseName: String = "Main Warehouse",
    val quantityReceived: Int,
    val unitCost: Double,
    val shippingCost: Double = 0.0,
    val goodsPaymentMethod: String = "TUNAI", // TUNAI, QRIS/TRANSFER, HUTANG (TEMPO)
    val shippingPaymentMethod: String = "TUNAI (COD)", // TUNAI (COD), QRIS/TRANSFER, HUTANG (TEMPO)
    val dueDate: Long? = null,
    val shippingDueDate: Long? = null,
    val paymentStatus: String = "LUNAS", // LUNAS, BELUM LUNAS
    val shippingPaymentStatus: String = "LUNAS", // LUNAS, BELUM LUNAS
    val invoiceDate: Long = System.currentTimeMillis(),
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "stock_mutations")
data class StockMutationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val type: String, // "IN" or "OUT"
    val quantity: Int,
    val previousStock: Int,
    val finalStock: Int,
    val referenceNumber: String = "",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "purchase_returns")
data class PurchaseReturnEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val returnNumber: String,
    val referenceNumber: String,
    val supplierName: String,
    val productId: Long,
    val productName: String,
    val quantityReturned: Int,
    val unitCost: Double,
    val totalAmount: Double,
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val journalNumber: String,
    val transactionRef: String = "",
    val accountName: String,
    val debitAmount: Double = 0.0,
    val creditAmount: Double = 0.0,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val debtAmount: Double = 0.0
)

@Entity(tableName = "discounts")
data class DiscountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // "PERCENT" or "FIXED"
    val value: Double,
    val isActive: Boolean = true
)
