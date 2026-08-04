import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CashierPosScreen.kt", "r") as f:
    content = f.read()

old_call = """                                    viewModel.processCheckout(
                                        customerName = customerName.ifBlank { "Guest" },
                                        paymentMethod = paymentMethod,
                                        paidAmount = paidDouble,
                                        notes = notes,
                                        onSuccess = { showReceipt = true }
                                    )"""

new_call = """                                    viewModel.processCheckout(
                                        customerName = customerName.ifBlank { "Guest" },
                                        paymentMethod = paymentMethod,
                                        paidAmount = paidDouble,
                                        notes = notes,
                                        discountAmount = discountAmount / curr.exchangeRate,
                                        onSuccess = { showReceipt = true }
                                    )"""

content = content.replace(old_call, new_call)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CashierPosScreen.kt", "w") as f:
    f.write(content)

