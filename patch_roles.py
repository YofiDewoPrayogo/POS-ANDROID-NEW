import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "r") as f:
    content = f.read()

# Replace variables in RolesManagementContent
old_vars = """    var editingRole by remember { mutableStateOf<RoleEntity?>(null) }
    
    var name by remember { mutableStateOf("") }
    var canEdit by remember { mutableStateOf(false) }
    var canDelete by remember { mutableStateOf(false) }
    var canCreate by remember { mutableStateOf(false) }
    var canManageProducts by remember { mutableStateOf(false) }
    var canViewReports by remember { mutableStateOf(false) }
    var canManageStock by remember { mutableStateOf(false) }
    var canReturnSales by remember { mutableStateOf(false) }"""

new_vars = """    var editingRole by remember { mutableStateOf<RoleEntity?>(null) }
    
    var name by remember { mutableStateOf("") }
    var canViewDashboard by remember { mutableStateOf(false) }
    var canViewCashier by remember { mutableStateOf(false) }
    var canViewProducts by remember { mutableStateOf(false) }
    var canViewReports by remember { mutableStateOf(false) }
    var canViewSettings by remember { mutableStateOf(false) }"""

content = content.replace(old_vars, new_vars)

# We also need to find where editingRole is updated and replace that
old_edit_action = """                                            editingRole = role
                                            name = role.name
                                            canEdit = role.canEdit
                                            canDelete = role.canDelete
                                            canCreate = role.canCreate
                                            canManageProducts = role.canManageProducts
                                            canViewReports = role.canViewReports
                                            canManageStock = role.canManageStock
                                            canReturnSales = role.canReturnSales"""

new_edit_action = """                                            editingRole = role
                                            name = role.name
                                            canViewDashboard = role.canViewDashboard
                                            canViewCashier = role.canViewCashier
                                            canViewProducts = role.canViewProducts
                                            canViewReports = role.canViewReports
                                            canViewSettings = role.canViewSettings"""

content = content.replace(old_edit_action, new_edit_action)

# And clear action
old_clear_action = """                                            editingRole = null
                                            name = ""
                                            canEdit = false
                                            canDelete = false
                                            canCreate = false
                                            canManageProducts = false
                                            canViewReports = false
                                            canManageStock = false
                                            canReturnSales = false"""

new_clear_action = """                                            editingRole = null
                                            name = ""
                                            canViewDashboard = false
                                            canViewCashier = false
                                            canViewProducts = false
                                            canViewReports = false
                                            canViewSettings = false"""
content = content.replace(old_clear_action, new_clear_action)

# Save action
old_save_action = """                                        if (editingRole == null) {
                                            viewModel.addRole(
                                                name = name,
                                                canEdit = canEdit,
                                                canDelete = canDelete,
                                                canCreate = canCreate,
                                                canManageProducts = canManageProducts,
                                                canViewReports = canViewReports,
                                                canManageStock = canManageStock,
                                                canReturnSales = canReturnSales
                                            )
                                        } else {
                                            viewModel.updateRole(
                                                id = editingRole!!.id,
                                                name = name,
                                                canEdit = canEdit,
                                                canDelete = canDelete,
                                                canCreate = canCreate,
                                                canManageProducts = canManageProducts,
                                                canViewReports = canViewReports,
                                                canManageStock = canManageStock,
                                                canReturnSales = canReturnSales
                                            )
                                        }"""

new_save_action = """                                        if (editingRole == null) {
                                            viewModel.addRole(
                                                name = name,
                                                canViewDashboard = canViewDashboard,
                                                canViewCashier = canViewCashier,
                                                canViewProducts = canViewProducts,
                                                canViewReports = canViewReports,
                                                canViewSettings = canViewSettings
                                            )
                                        } else {
                                            viewModel.updateRole(
                                                id = editingRole!!.id,
                                                name = name,
                                                canViewDashboard = canViewDashboard,
                                                canViewCashier = canViewCashier,
                                                canViewProducts = canViewProducts,
                                                canViewReports = canViewReports,
                                                canViewSettings = canViewSettings
                                            )
                                        }"""
content = content.replace(old_save_action, new_save_action)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write(content)

