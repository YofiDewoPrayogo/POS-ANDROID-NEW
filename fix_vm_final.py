import re

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    content = f.read()

# First remove the wrongly appended stuff at the end
wrong_block = """    val customers = repository.customers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
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

content = content.replace(wrong_block, "")

# Insert right before class PosViewModelFactory
insert_block = """
    val customers = repository.customers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
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
    }
}
class PosViewModelFactory"""

content = content.replace("}\n\nclass PosViewModelFactory", insert_block)
content = content.replace("}\nclass PosViewModelFactory", insert_block)

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(content)

