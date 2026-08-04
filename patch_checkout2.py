import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CashierPosScreen.kt", "r") as f:
    content = f.read()

# Fix discount calculation
old_discount_logic = """    val discountAmount = discountInput.toDoubleOrNull() ?: 0.0
    val totalAfterDiscountUsd = (totalUsd - discountAmount).coerceAtLeast(0.0)
    val totalInCurr = totalAfterDiscountUsd * curr.exchangeRate"""

new_discount_logic = """    val discountAmount = discountInput.toDoubleOrNull() ?: 0.0
    val totalInCurrBeforeDiscount = totalUsd * curr.exchangeRate
    val totalInCurr = (totalInCurrBeforeDiscount - discountAmount).coerceAtLeast(0.0)
    val totalAfterDiscountUsd = totalInCurr / curr.exchangeRate"""

content = content.replace(old_discount_logic, new_discount_logic)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CashierPosScreen.kt", "w") as f:
    f.write(content)

