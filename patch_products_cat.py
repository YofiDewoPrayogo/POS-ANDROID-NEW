import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ProductsScreen.kt", "r") as f:
    content = f.read()

# Add a state for showing add category dialog
add_state = """    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }"""

content = content.replace("    var showAddDialog by remember { mutableStateOf(false) }\n    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }", add_state)

# Add the button in the UI next to search bar or below it.
# Actually let's just add it as an action button at the top
search_ui = """                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari produk...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { showAddCategoryDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kategori")
                }"""

content = re.sub(r'OutlinedTextField\([\s\S]*?modifier = Modifier\.weight\(1f\)\n                \)', search_ui, content, count=1)

# Add the Add Category Dialog at the end of the file
cat_dialog = """
@Composable
fun AddCategoryDialog(viewModel: PosViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Tambah Kategori", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Kategori") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi (Opsional)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (name.isNotBlank()) {
                            viewModel.insertCategory(com.yofidewo.pos.data.CategoryEntity(name = name, description = description))
                            onDismiss()
                        }
                    }) { Text("Simpan") }
                }
            }
        }
    }
}
"""

content = content + cat_dialog

# Now trigger the dialog if state is true
trigger = """
    if (showAddCategoryDialog) {
        AddCategoryDialog(viewModel = viewModel, onDismiss = { showAddCategoryDialog = false })
    }
}
"""
content = content.replace("    }\n}\n\n@Composable\nfun AddCategoryDialog", trigger + "\n@Composable\nfun AddCategoryDialog")

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ProductsScreen.kt", "w") as f:
    f.write(content)

