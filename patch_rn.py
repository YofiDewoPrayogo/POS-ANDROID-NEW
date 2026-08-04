import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ReceivingNotesScreen.kt", "r") as f:
    content = f.read()

# Replace the Card content inside ReceivingNotesScreen
old_card_content = """                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = note.referenceNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Produk: ${note.productName} (+${note.quantityReceived} pcs)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                                    Text(text = "Pemasok: ${note.supplierName} • Gudang: ${note.warehouseName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Tanggal: ${dateFormatter.format(Date(note.timestamp))}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                }
                                Text(text = "Total Biaya: ${viewModel.formatMoney(note.unitCost * note.quantityReceived)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }"""

new_card_content = """                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = note.referenceNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Text(text = dateFormatter.format(Date(note.timestamp)), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                
                                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    Text(text = "Produk: ${note.productName} (+${note.quantityReceived} pcs)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text(text = "Pemasok: ${note.supplierName}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Gudang: ${note.warehouseName}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                
                                Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Total Biaya (${note.quantityReceived} x ${viewModel.formatMoney(note.unitCost)})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = viewModel.formatMoney(note.unitCost * note.quantityReceived), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }"""

content = content.replace(old_card_content, new_card_content)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ReceivingNotesScreen.kt", "w") as f:
    f.write(content)

