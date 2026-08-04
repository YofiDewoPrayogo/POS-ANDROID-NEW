sed -i 's/val name: String,/val name: String,\n    val pin: String = "1234",/' app/src/main/java/com/yofidewo/pos/data/Entities.kt
sed -i 's/val role: String, \/\/ "Admin" or "Kasir"/val roleId: Long,/' app/src/main/java/com/yofidewo/pos/data/Entities.kt
sed -i 's/val code: String, \/\/ Barcode\/SKU/val code: String,\n    val barcode: String = "",/' app/src/main/java/com/yofidewo/pos/data/Entities.kt
