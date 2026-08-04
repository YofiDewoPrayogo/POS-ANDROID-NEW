import re

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    content = f.read()

return_func = """
    fun returnTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            val items = repository.getTransactionItems(transaction.id)
            items.forEach { item ->
                val product = repository.getProductById(item.productId)
                if (product != null) {
                    repository.updateProduct(product.copy(stock = product.stock + item.quantity))
                }
            }
            repository.deleteTransaction(transaction)
        }
    }
"""

content = content.replace("fun clearCart() {", return_func + "\n    fun clearCart() {")

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(content)
