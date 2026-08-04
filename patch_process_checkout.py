import re

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    content = f.read()

old_fun = """    fun processCheckout(
        customerName: String,
        paidAmount: Double,
        paymentMethod: String,
        notes: String,
        onSuccess: (TransactionEntity) -> Unit
    ) {"""

new_fun = """    fun processCheckout(
        customerName: String,
        paidAmount: Double,
        paymentMethod: String,
        notes: String,
        discountAmount: Double = 0.0,
        onSuccess: (TransactionEntity) -> Unit
    ) {"""

content = content.replace(old_fun, new_fun)

old_trans = """            val transaction = TransactionEntity(
                invoiceNumber = invoiceNum,
                userId = currUser.id,
                cashierName = currUser.name,
                customerName = customerName.ifBlank { "Guest" },
                totalAmount = totalUsd,
                amountPaid = paidAmount / curr.exchangeRate,
                paymentMethod = paymentMethod,
                paymentStatus = if (paidAmount == 0.0) "PIUTANG" else "LUNAS",
                notes = notes,
                currencyCode = curr.code,
                exchangeRate = curr.exchangeRate
            )"""

new_trans = """            val totalAfterDiscount = totalUsd - discountAmount
            val transaction = TransactionEntity(
                invoiceNumber = invoiceNum,
                userId = currUser.id,
                cashierName = currUser.name,
                customerName = customerName.ifBlank { "Guest" },
                subTotalAmount = totalUsd,
                discountAmount = discountAmount,
                totalAmount = totalAfterDiscount,
                amountPaid = paidAmount / curr.exchangeRate,
                paymentMethod = paymentMethod,
                paymentStatus = if (paidAmount == 0.0) "PIUTANG" else "LUNAS",
                notes = notes,
                currencyCode = curr.code,
                exchangeRate = curr.exchangeRate
            )"""

content = content.replace(old_trans, new_trans)

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(content)

