import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "r") as f:
    content = f.read()

old_list = """                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canCreate, onCheckedChange = { canCreate = it }); Text("Bisa Buat Data") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canEdit, onCheckedChange = { canEdit = it }); Text("Bisa Edit Data") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canDelete, onCheckedChange = { canDelete = it }); Text("Bisa Hapus Data") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canManageProducts, onCheckedChange = { canManageProducts = it }); Text("Kelola Produk") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewReports, onCheckedChange = { canViewReports = it }); Text("Lihat Laporan") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canManageStock, onCheckedChange = { canManageStock = it }); Text("Kelola Stok") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canReturnSales, onCheckedChange = { canReturnSales = it }); Text("Retur Penjualan") } }"""

new_list = """                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewDashboard, onCheckedChange = { canViewDashboard = it }); Text("Akses Dashboard") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewCashier, onCheckedChange = { canViewCashier = it }); Text("Akses Kasir") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewProducts, onCheckedChange = { canViewProducts = it }); Text("Akses Produk (Maintenance Item)") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewReports, onCheckedChange = { canViewReports = it }); Text("Akses Laporan") } }
                        item { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = canViewSettings, onCheckedChange = { canViewSettings = it }); Text("Akses Pengaturan") } }"""

content = content.replace(old_list, new_list)

old_save = """                                viewModel.updateRole(editingRole!!.copy(name = name, canEdit = canEdit, canDelete = canDelete, canCreate = canCreate, canManageProducts = canManageProducts, canViewReports = canViewReports, canManageStock = canManageStock, canReturnSales = canReturnSales))
                            } else {
                                viewModel.addRole(name, canEdit, canDelete, canCreate, canManageProducts, canViewReports, canManageStock, canReturnSales)"""

new_save = """                                viewModel.updateRole(editingRole!!.copy(name = name, canViewDashboard = canViewDashboard, canViewCashier = canViewCashier, canViewProducts = canViewProducts, canViewReports = canViewReports, canViewSettings = canViewSettings))
                            } else {
                                viewModel.addRole(name, canViewDashboard, canViewCashier, canViewProducts, canViewReports, canViewSettings)"""

content = content.replace(old_save, new_save)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write(content)

