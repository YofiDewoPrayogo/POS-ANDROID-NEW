import re

# Daos.kt
with open("app/src/main/java/com/yofidewo/pos/data/Daos.kt", "r") as f:
    daos = f.read()
daos = daos.replace("    fun getAllTransactions(): Flow<List<TransactionEntity>>", "    fun getAllTransactions(): Flow<List<TransactionEntity>>\n\n    @Update\n    suspend fun updateTransaction(transaction: TransactionEntity)")
with open("app/src/main/java/com/yofidewo/pos/data/Daos.kt", "w") as f:
    f.write(daos)

# PosRepository.kt
with open("app/src/main/java/com/yofidewo/pos/data/PosRepository.kt", "r") as f:
    repo = f.read()
repo = repo.replace("    val transactions: Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()", "    val transactions: Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()\n    suspend fun updateTransaction(transaction: TransactionEntity) = db.transactionDao().updateTransaction(transaction)")
with open("app/src/main/java/com/yofidewo/pos/data/PosRepository.kt", "w") as f:
    f.write(repo)

# PosViewModel.kt
with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    vm = f.read()
vm = vm.replace("    val transactions = repository.transactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())", "    val transactions = repository.transactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())\n    fun updateTransaction(transaction: com.yofidewo.pos.data.TransactionEntity) = viewModelScope.launch { repository.updateTransaction(transaction) }")
with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(vm)

