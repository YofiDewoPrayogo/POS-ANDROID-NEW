import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CashierPosScreen.kt", "r") as f:
    content = f.read()

# Make QTY text clickable and show a dialog
cart_item_row_new = """@Composable
fun CartItemRow(
    item: CartItem,
    viewModel: PosViewModel,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onChangeQty: (Int) -> Unit
) {
    var showQtyDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = viewModel.formatMoney(item.product.sellPrice),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurang", modifier = Modifier.size(18.dp))
            }
            
            Text(
                text = "${item.quantity}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clickable { showQtyDialog = true }
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            IconButton(
                onClick = onAdd,
                enabled = item.quantity < item.product.stock,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(18.dp))
            }
        }
    }

    if (showQtyDialog) {
        var qtyInput by remember { mutableStateOf(item.quantity.toString()) }
        AlertDialog(
            onDismissRequest = { showQtyDialog = false },
            title = { Text("Ubah Qty") },
            text = {
                OutlinedTextField(
                    value = qtyInput,
                    onValueChange = { qtyInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    val newQty = qtyInput.toIntOrNull() ?: item.quantity
                    if (newQty > 0 && newQty <= item.product.stock) {
                        onChangeQty(newQty)
                    }
                    showQtyDialog = false
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showQtyDialog = false }) { Text("Batal") }
            }
        )
    }
}
"""

content = re.sub(
    r"@Composable\nfun CartItemRow.*?fun CurrencySelector",
    cart_item_row_new + "\n@Composable\nfun CurrencySelector",
    content,
    flags=re.DOTALL
)

# Also update the call site in CartPanel
content = content.replace(
    """                    CartItemRow(
                        item = item,
                        viewModel = viewModel,
                        onAdd = { viewModel.updateCartQuantity(item.product.id, item.quantity + 1) },
                        onRemove = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) }
                    )""",
    """                    CartItemRow(
                        item = item,
                        viewModel = viewModel,
                        onAdd = { viewModel.updateCartQuantity(item.product.id, item.quantity + 1) },
                        onRemove = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) },
                        onChangeQty = { newQty -> viewModel.updateCartQuantity(item.product.id, newQty) }
                    )"""
)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/CashierPosScreen.kt", "w") as f:
    f.write(content)
