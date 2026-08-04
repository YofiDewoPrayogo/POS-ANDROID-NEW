import re

with open("app/src/main/java/com/yofidewo/pos/data/Entities.kt", "r") as f:
    content = f.read()

trx = """data class TransactionEntity(
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
)"""

content = re.sub(r'data class TransactionEntity\([\s\S]*?val timestamp: Long = System\.currentTimeMillis\(\)\n\)', trx, content)

with open("app/src/main/java/com/yofidewo/pos/data/Entities.kt", "w") as f:
    f.write(content)

