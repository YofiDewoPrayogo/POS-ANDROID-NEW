import re

# Fix Daos.kt
with open("app/src/main/java/com/yofidewo/pos/data/Daos.kt", "r") as f:
    daos = f.read()
daos = daos.replace("fun getActiveDiscounts(): kotlinx.coroutines.flow.Flow<List<DiscountEntity>>", "fun getActiveDiscounts(): kotlinx.coroutines.flow.Flow<List<DiscountEntity>>\n\n    @Query(\"SELECT * FROM discounts\")\n    fun getAllDiscounts(): kotlinx.coroutines.flow.Flow<List<DiscountEntity>>")
with open("app/src/main/java/com/yofidewo/pos/data/Daos.kt", "w") as f:
    f.write(daos)

# Fix ProductsScreen.kt
with open("app/src/main/java/com/yofidewo/pos/ui/screens/ProductsScreen.kt", "r") as f:
    prod = f.read()

# I need to move if (showAddCategoryDialog) from the end of ProductFormDialog to the end of ProductsScreen
# ProductsScreen ends with:
#     if (showAddDialog) {
#         ProductFormDialog(
#             viewModel = viewModel,
#             productToEdit = productToEdit,
#             categories = categories,
#             brands = brands,
#             warehouses = warehouses,
#             onDismiss = { showAddDialog = false }
#         )
#     }
# } // End of ProductsScreen
# I'll just use sed to replace the wrong injection and add it properly.
prod = prod.replace("    if (showAddCategoryDialog) {\n        AddCategoryDialog(viewModel = viewModel, onDismiss = { showAddCategoryDialog = false })\n    }\n}", "}\n")
prod = prod.replace("            onDismiss = { showAddDialog = false }\n        )\n    }", "            onDismiss = { showAddDialog = false }\n        )\n    }\n    if (showAddCategoryDialog) {\n        AddCategoryDialog(viewModel = viewModel, onDismiss = { showAddCategoryDialog = false })\n    }")
with open("app/src/main/java/com/yofidewo/pos/ui/screens/ProductsScreen.kt", "w") as f:
    f.write(prod)

# Fix missing `insertCategory` in PosViewModel.kt if it doesn't exist
# Wait, did I add insertCategory in PosViewModel?
# Let's see if PosViewModel has insertCategory.
