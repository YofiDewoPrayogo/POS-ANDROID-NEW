import re

with open("app/src/main/java/com/yofidewo/pos/data/Entities.kt", "r") as f:
    content = f.read()

old_role = """data class RoleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // "Admin", "Kasir", etc.
    val canEdit: Boolean = false,
    val canDelete: Boolean = false,
    val canCreate: Boolean = false,
    val canManageProducts: Boolean = false,
    val canViewReports: Boolean = false,
    val canManageStock: Boolean = false,
    val canReturnSales: Boolean = false
)"""

new_role = """data class RoleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val canViewDashboard: Boolean = false,
    val canViewCashier: Boolean = false,
    val canViewProducts: Boolean = false,
    val canViewReports: Boolean = false,
    val canViewSettings: Boolean = false
)"""

content = content.replace(old_role, new_role)

with open("app/src/main/java/com/yofidewo/pos/data/Entities.kt", "w") as f:
    f.write(content)

