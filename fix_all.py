import re

# Fix DashboardScreen
with open("app/src/main/java/com/yofidewo/pos/ui/screens/DashboardScreen.kt", "r") as f:
    content = f.read()

# Remove the incorrectly prepended imports
lines = content.split('\n')
pkg_idx = next(i for i, line in enumerate(lines) if line.startswith('package '))
correct_imports = lines[:pkg_idx]
rest = lines[pkg_idx:]

new_content = '\n'.join(rest[:1]) + '\n' + '\n'.join(correct_imports) + '\n' + '\n'.join(rest[1:])
new_content = new_content.replace(
    "import java.time.LocalDate", 
    ""
)
new_content = new_content.replace(
    "import androidx.compose.material.icons.automirrored.filled.TrendingUp",
    "import java.time.LocalDate\nimport androidx.compose.material.icons.automirrored.filled.TrendingUp"
)

# Remove the duplicated LocalDate import
new_content = re.sub(r'import java\.time\.LocalDate\n+', 'import java.time.LocalDate\n', new_content)
with open("app/src/main/java/com/yofidewo/pos/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(new_content)

# Fix PosViewModel
with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    vm_content = f.read()

# Replace getTransactionItems with getItemsForTransactionSync
# Replace items.forEach with for (item in items)
old_return = """    fun returnTransaction(transaction: TransactionEntity) {
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
    }"""

new_return = """    fun returnTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            val items = repository.getItemsForTransactionSync(transaction.id)
            for (item in items) {
                val product = repository.getProductById(item.productId)
                if (product != null) {
                    repository.updateProduct(product.copy(stock = product.stock + item.quantity))
                }
            }
            repository.deleteTransaction(transaction)
        }
    }"""
vm_content = vm_content.replace(old_return, new_return)

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(vm_content)
