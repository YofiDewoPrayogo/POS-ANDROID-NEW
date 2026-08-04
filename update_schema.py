import re

with open("app/src/main/java/com/yofidewo/pos/data/Entities.kt", "a") as f:
    f.write("""
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
""")

with open("app/src/main/java/com/yofidewo/pos/data/Entities.kt", "r") as f:
    content = f.read()

# Add customerId and paymentStatus to TransactionEntity
content = re.sub(
    r'val paymentMethod: String,',
    r'val paymentMethod: String,\n    val customerId: Long? = null,\n    val paymentStatus: String = "PAID",',
    content
)

with open("app/src/main/java/com/yofidewo/pos/data/Entities.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/yofidewo/pos/data/Daos.kt", "r") as f:
    content = f.read()

daos = """
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscount(discount: DiscountEntity): Long
    @Update
    suspend fun updateDiscount(discount: DiscountEntity)
    @Delete
    suspend fun deleteDiscount(discount: DiscountEntity)
}
"""
content = content + daos
with open("app/src/main/java/com/yofidewo/pos/data/Daos.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/yofidewo/pos/data/PosDatabase.kt", "r") as f:
    content = f.read()

content = content.replace("version = 4,", "version = 5,")
content = content.replace("ReceivingNoteEntity::class", "ReceivingNoteEntity::class,\n        CustomerEntity::class,\n        DiscountEntity::class")
content = content.replace("abstract fun currencyDao(): CurrencyDao", "abstract fun currencyDao(): CurrencyDao\n    abstract fun customerDao(): CustomerDao\n    abstract fun discountDao(): DiscountDao")

with open("app/src/main/java/com/yofidewo/pos/data/PosDatabase.kt", "w") as f:
    f.write(content)
