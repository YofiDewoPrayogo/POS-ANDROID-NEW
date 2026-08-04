import re

with open("app/src/main/java/com/yofidewo/pos/data/PosRepository.kt", "r") as f:
    content = f.read()

content = content.replace(
    "suspend fun getItemsForTransactionSync(transactionId: Long): List<TransactionItemEntity> =\n        db.transactionDao().getItemsForTransactionSync(transactionId)",
    "suspend fun getItemsForTransactionSync(transactionId: Long): List<TransactionItemEntity> =\n        db.transactionDao().getItemsForTransactionSync(transactionId)\n    suspend fun deleteTransaction(transaction: TransactionEntity) = db.transactionDao().deleteTransaction(transaction)"
)
with open("app/src/main/java/com/yofidewo/pos/data/PosRepository.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/yofidewo/pos/data/Daos.kt", "r") as f:
    daos = f.read()
if "deleteTransaction" not in daos:
    daos = daos.replace(
        "suspend fun insertTransactionItems(items: List<TransactionItemEntity>)",
        "suspend fun insertTransactionItems(items: List<TransactionItemEntity>)\n\n    @androidx.room.Delete\n    suspend fun deleteTransaction(transaction: TransactionEntity)"
    )
with open("app/src/main/java/com/yofidewo/pos/data/Daos.kt", "w") as f:
    f.write(daos)

