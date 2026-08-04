import re

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    content = f.read()

old_add_role = """    fun addRole(name: String, canEdit: Boolean, canDelete: Boolean, canCreate: Boolean, canManageProducts: Boolean, canViewReports: Boolean, canManageStock: Boolean, canReturnSales: Boolean) {
        viewModelScope.launch {
            repository.insertRole(RoleEntity(
                name = name,
                canEdit = canEdit,
                canDelete = canDelete,
                canCreate = canCreate,
                canManageProducts = canManageProducts,
                canViewReports = canViewReports,
                canManageStock = canManageStock,
                canReturnSales = canReturnSales
            ))
            loadUsersAndRoles()
        }
    }"""

new_add_role = """    fun addRole(name: String, canViewDashboard: Boolean, canViewCashier: Boolean, canViewProducts: Boolean, canViewReports: Boolean, canViewSettings: Boolean) {
        viewModelScope.launch {
            repository.insertRole(RoleEntity(
                name = name,
                canViewDashboard = canViewDashboard,
                canViewCashier = canViewCashier,
                canViewProducts = canViewProducts,
                canViewReports = canViewReports,
                canViewSettings = canViewSettings
            ))
            loadUsersAndRoles()
        }
    }"""

content = content.replace(old_add_role, new_add_role)

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(content)

