with open("app/src/main/java/com/yofidewo/pos/data/PosRepository.kt", "r") as f:
    content = f.read()

repo_add = """    // Receiving Notes
    val receivingNotes: Flow<List<ReceivingNoteEntity>> = db.receivingNoteDao().getAllReceivingNotes()
    suspend fun addReceivingNote(note: ReceivingNoteEntity) {
        db.receivingNoteDao().insertReceivingNote(note)
        // Add stock to product
        db.productDao().updateProductStock(note.productId, note.quantityReceived)
    }

    // Customers
    val customers: Flow<List<CustomerEntity>> = db.customerDao().getAllCustomers()
    suspend fun insertCustomer(customer: CustomerEntity) = db.customerDao().insertCustomer(customer)
    suspend fun updateCustomer(customer: CustomerEntity) = db.customerDao().updateCustomer(customer)
    suspend fun deleteCustomer(customer: CustomerEntity) = db.customerDao().deleteCustomer(customer)

    // Discounts
    val activeDiscounts: Flow<List<DiscountEntity>> = db.discountDao().getActiveDiscounts()
    val allDiscounts: Flow<List<DiscountEntity>> = db.discountDao().getAllDiscounts()
    suspend fun insertDiscount(discount: DiscountEntity) = db.discountDao().insertDiscount(discount)
    suspend fun updateDiscount(discount: DiscountEntity) = db.discountDao().updateDiscount(discount)
    suspend fun deleteDiscount(discount: DiscountEntity) = db.discountDao().deleteDiscount(discount)
"""

content = content.replace("    // Receiving Notes\n    val receivingNotes: Flow<List<ReceivingNoteEntity>> = db.receivingNoteDao().getAllReceivingNotes()\n    suspend fun addReceivingNote(note: ReceivingNoteEntity) {\n        db.receivingNoteDao().insertReceivingNote(note)\n        // Add stock to product\n        db.productDao().updateProductStock(note.productId, note.quantityReceived)\n    }", repo_add)

with open("app/src/main/java/com/yofidewo/pos/data/PosRepository.kt", "w") as f:
    f.write(content)
