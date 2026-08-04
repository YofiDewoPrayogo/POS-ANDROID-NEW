import re

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "r") as f:
    content = f.read()

# Fix addRole
content = re.sub(
    r"fun addRole\(name: String, canEdit: Boolean, canDelete: Boolean, canCreate: Boolean, canManageProducts: Boolean, canViewReports: Boolean, canManageStock: Boolean, canReturnSales: Boolean\).*?\}",
    r"fun addRole(name: String, canViewDashboard: Boolean, canViewCashier: Boolean, canViewProducts: Boolean, canViewReports: Boolean, canViewSettings: Boolean) {\n        viewModelScope.launch {\n            repository.insertRole(RoleEntity(name = name, canViewDashboard = canViewDashboard, canViewCashier = canViewCashier, canViewProducts = canViewProducts, canViewReports = canViewReports, canViewSettings = canViewSettings))\n        }\n    }",
    content, flags=re.DOTALL
)

# Fix fallback adminRole
content = content.replace(
    "repository.insertRole(com.yofidewo.pos.data.RoleEntity(name = \"Administrator\", canEdit = true, canDelete = true, canCreate = true, canManageProducts = true, canViewReports = true, canManageStock = true, canReturnSales = true))",
    "repository.insertRole(com.yofidewo.pos.data.RoleEntity(name = \"Administrator\", canViewDashboard = true, canViewCashier = true, canViewProducts = true, canViewReports = true, canViewSettings = true))"
)

# Fix fallback kasirRole
content = content.replace(
    "repository.insertRole(com.yofidewo.pos.data.RoleEntity(name = \"Kasir\", canEdit = false, canDelete = false, canCreate = true, canManageProducts = false, canViewReports = false, canManageStock = false, canReturnSales = false))",
    "repository.insertRole(com.yofidewo.pos.data.RoleEntity(name = \"Kasir\", canViewDashboard = false, canViewCashier = true, canViewProducts = false, canViewReports = false, canViewSettings = false))"
)

with open("app/src/main/java/com/yofidewo/pos/ui/PosViewModel.kt", "w") as f:
    f.write(content)

