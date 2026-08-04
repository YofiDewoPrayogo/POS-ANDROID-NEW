import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CategoriesBrandsScreen.kt", "r") as f:
    content = f.read()

# Add edit category variables
content = content.replace(
    "var showAddDialog by remember { mutableStateOf(false) }",
    "var showAddDialog by remember { mutableStateOf(false) }\n    var categoryToEdit by remember { mutableStateOf<CategoryEntity?>(null) }",
    1
)

# Replace AddCategoryDialog call
content = content.replace(
    "AddCategoryDialog(viewModel = viewModel, onDismiss = { showAddDialog = false })",
    "AddCategoryDialog(viewModel = viewModel, categoryToEdit = categoryToEdit, onDismiss = { showAddDialog = false; categoryToEdit = null })"
)

# Add Edit button for categories
content = content.replace(
    """                        IconButton(onClick = { viewModel.deleteCategory(cat) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }""",
    """                        Row {
                            IconButton(onClick = { categoryToEdit = cat; showAddDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deleteCategory(cat) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }"""
)

# Update AddCategoryDialog signature and logic
content = content.replace(
    "fun AddCategoryDialog(viewModel: PosViewModel, onDismiss: () -> Unit) {",
    "fun AddCategoryDialog(viewModel: PosViewModel, categoryToEdit: CategoryEntity? = null, onDismiss: () -> Unit) {"
)
content = content.replace(
    """    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }""",
    """    var name by remember { mutableStateOf(categoryToEdit?.name ?: "") }
    var code by remember { mutableStateOf(categoryToEdit?.code ?: "") }
    var desc by remember { mutableStateOf(categoryToEdit?.description ?: "") }"""
)
content = content.replace(
    """                        viewModel.addCategory(name, code, desc)
                        onDismiss()""",
    """                        if (categoryToEdit != null) {
                            viewModel.updateCategory(categoryToEdit!!.copy(name = name, code = code, description = desc))
                        } else {
                            viewModel.addCategory(name, code, desc)
                        }
                        onDismiss()"""
)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CategoriesBrandsScreen.kt", "w") as f:
    f.write(content)
