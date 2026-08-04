import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ReportsTransactionsScreen.kt", "r") as f:
    content = f.read()

# Add currentRole to TransactionsHistoryContent
content = content.replace(
    "val users by viewModel.users.collectAsState()",
    "val users by viewModel.users.collectAsState()\n    val currentRole by viewModel.currentRole.collectAsState()"
)

# Add Return Button
original_card_end = """                                Text(
                                    text = "Lihat Struk",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }"""

new_card_end = """                                Text(
                                    text = "Lihat Struk",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (currentRole?.canReturnSales == true) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    TextButton(
                                        onClick = { viewModel.returnTransaction(tx) },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) {
                                        Text("Retur Penjualan", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                    }
                                }
                            }
                        }"""

content = content.replace(original_card_end, new_card_end)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ReportsTransactionsScreen.kt", "w") as f:
    f.write(content)
