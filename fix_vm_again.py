import re

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    content = f.read()

# The wrongly appended text
wrong_text = """    val customers = repository.customers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeDiscounts = repository.activeDiscounts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertCustomer(customer: com.yofidewo.pos.data.CustomerEntity) = viewModelScope.launch { repository.insertCustomer(customer) }
    fun updateCustomer(customer: com.yofidewo.pos.data.CustomerEntity) = viewModelScope.launch { repository.updateCustomer(customer) }
    fun deleteCustomer(customer: com.yofidewo.pos.data.CustomerEntity) = viewModelScope.launch { repository.deleteCustomer(customer) }

    fun insertDiscount(discount: com.yofidewo.pos.data.DiscountEntity) = viewModelScope.launch { repository.insertDiscount(discount) }
    fun updateDiscount(discount: com.yofidewo.pos.data.DiscountEntity) = viewModelScope.launch { repository.updateDiscount(discount) }
    fun deleteDiscount(discount: com.yofidewo.pos.data.DiscountEntity) = viewModelScope.launch { repository.deleteDiscount(discount) }
    
    fun logout() {
        currentUser.value = null
        currentRole.value = null
    }"""

# Remove it from the end
content = content.replace(wrong_text, "")

# Add it just before the closing brace of PosViewModel
# We can find PosViewModelFactory and insert it right before that.
factory_str = "class PosViewModelFactory"
if factory_str in content:
    content = content.replace(factory_str, wrong_text + "\n}\n" + factory_str)

# Wait, if I inserted `\n}\n`, it means I close PosViewModel. But maybe PosViewModel is already closed.
# Let's see the context of PosViewModelFactory
