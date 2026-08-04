import re

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    lines = f.readlines()

# Let's find where 'class PosViewModelFactory' starts
factory_idx = -1
for i, line in enumerate(lines):
    if "class PosViewModelFactory" in line:
        factory_idx = i
        break

# Truncate lines from where the mess started
# Let's look backwards from factory_idx to find the closing brace of PosViewModel
close_brace_idx = -1
for i in range(factory_idx - 1, -1, -1):
    if lines[i].strip() == "}":
        close_brace_idx = i
        break

# The clean block we want to ensure is there EXACTLY ONCE
clean_block = """
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
"""

# Let's see if this clean block already exists BEFORE close_brace_idx
# To be safe, I'll remove any occurrence of "fun logout()" and "val activeDiscounts =" in the entire file except the factory.
new_lines = []
for i in range(close_brace_idx):
    if "fun logout()" in lines[i] or "fun insertDiscount" in lines[i] or "fun updateDiscount" in lines[i] or "fun deleteDiscount" in lines[i] or "val activeDiscounts" in lines[i] or "val customers" in lines[i] or "fun insertCustomer" in lines[i] or "fun updateCustomer" in lines[i] or "fun deleteCustomer" in lines[i] or "currentUser.value = null" in lines[i] or "currentRole.value = null" in lines[i]:
        continue # Skip these lines
    new_lines.append(lines[i])

# Strip trailing empty lines before the brace
while new_lines and new_lines[-1].strip() == "":
    new_lines.pop()

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.writelines(new_lines)
    f.write(clean_block)
    f.write("}\n\n")
    # write the factory and anything after it
    for i in range(factory_idx, len(lines)):
        # don't write trailing duplicate garbage
        if "val customers" in lines[i] or "val activeDiscounts" in lines[i] or "fun insertDiscount" in lines[i] or "fun logout()" in lines[i] or "currentUser.value = null" in lines[i]:
            continue
        f.write(lines[i])

