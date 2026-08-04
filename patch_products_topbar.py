import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ProductsScreen.kt", "r") as f:
    content = f.read()

content = content.replace("fun ProductsScreen(viewModel: PosViewModel)", "fun ProductsScreen(viewModel: PosViewModel, onBack: () -> Unit = {})")
content = content.replace("import androidx.compose.material.icons.filled.Add", "import androidx.compose.material.icons.filled.Add\nimport androidx.compose.material.icons.filled.ArrowBack")

scaffold_top = """    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produk & Stok", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {"""

content = content.replace("    Scaffold(\n        floatingActionButton = {", scaffold_top)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ProductsScreen.kt", "w") as f:
    f.write(content)

