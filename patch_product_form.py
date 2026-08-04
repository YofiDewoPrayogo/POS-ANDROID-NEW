import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ProductsScreen.kt", "r") as f:
    content = f.read()

old_stock_section = """                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = stockStr,
                            onValueChange = { stockStr = it },
                            label = { Text("Stok Awal") },"""

new_dropdown_section = """                item {
                    var expandedCat by remember { mutableStateOf(false) }
                    var expandedBrand by remember { mutableStateOf(false) }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = expandedCat,
                            onExpandedChange = { expandedCat = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = categories.find { it.id == selectedCatId }?.name ?: "Pilih Kategori",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Kategori") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.name) },
                                        onClick = { selectedCatId = cat.id; expandedCat = false }
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = expandedBrand,
                            onExpandedChange = { expandedBrand = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = brands.find { it.id == selectedBrandId }?.name ?: "Pilih Brand",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Brand") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBrand) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expandedBrand, onDismissRequest = { expandedBrand = false }) {
                                brands.forEach { brand ->
                                    DropdownMenuItem(
                                        text = { Text(brand.name) },
                                        onClick = { selectedBrandId = brand.id; expandedBrand = false }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant).clickable { Toast.makeText(context, "Fitur Upload Gambar belum aktif", Toast.LENGTH_SHORT).show() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Image, contentDescription = "Gambar Produk")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Tambahkan Gambar Produk", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = stockStr,
                            onValueChange = { stockStr = it },
                            label = { Text("Stok Awal") },"""

content = content.replace(old_stock_section, new_dropdown_section)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ProductsScreen.kt", "w") as f:
    f.write(content)

