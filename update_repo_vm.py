with open("app/src/main/java/com/yofidewo/pos/data/PosRepository.kt", "r") as f:
    content = f.read()

repo_add = """
    // Customers
    val customers: Flow<List<CustomerEntity>> = db.customerDao().getAllCustomers()
    suspend fun insertCustomer(customer: CustomerEntity): Long = db.customerDao().insertCustomer(customer)
    suspend fun updateCustomer(customer: CustomerEntity) = db.customerDao().updateCustomer(customer)
    suspend fun deleteCustomer(customer: CustomerEntity) = db.customerDao().deleteCustomer(customer)

    // Discounts
    val activeDiscounts: Flow<List<DiscountEntity>> = db.discountDao().getActiveDiscounts()
    suspend fun insertDiscount(discount: DiscountEntity): Long = db.discountDao().insertDiscount(discount)
    suspend fun updateDiscount(discount: DiscountEntity) = db.discountDao().updateDiscount(discount)
    suspend fun deleteDiscount(discount: DiscountEntity) = db.discountDao().deleteDiscount(discount)
"""
content = content.replace("class PosRepository(private val db: PosDatabase) {", "class PosRepository(private val db: PosDatabase) {" + repo_add)
with open("app/src/main/java/com/yofidewo/pos/data/PosRepository.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    content = f.read()

vm_add = """
    val customers: StateFlow<List<com.yofidewo.pos.data.CustomerEntity>> = repository.customers.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val activeDiscounts: StateFlow<List<com.yofidewo.pos.data.DiscountEntity>> = repository.activeDiscounts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insertCustomer(customer: com.yofidewo.pos.data.CustomerEntity) = viewModelScope.launch { repository.insertCustomer(customer) }
    fun updateCustomer(customer: com.yofidewo.pos.data.CustomerEntity) = viewModelScope.launch { repository.updateCustomer(customer) }
    fun deleteCustomer(customer: com.yofidewo.pos.data.CustomerEntity) = viewModelScope.launch { repository.deleteCustomer(customer) }

    fun insertDiscount(discount: com.yofidewo.pos.data.DiscountEntity) = viewModelScope.launch { repository.insertDiscount(discount) }
    fun updateDiscount(discount: com.yofidewo.pos.data.DiscountEntity) = viewModelScope.launch { repository.updateDiscount(discount) }
    fun deleteDiscount(discount: com.yofidewo.pos.data.DiscountEntity) = viewModelScope.launch { repository.deleteDiscount(discount) }
"""
content = content.replace("val users: StateFlow<List<UserEntity>> = repository.users.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())", "val users: StateFlow<List<UserEntity>> = repository.users.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())" + vm_add)
with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(content)
