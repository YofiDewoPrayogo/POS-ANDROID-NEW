import re

with open("app/src/main/java/com/yofidewo/pos/data/PosDatabase.kt", "r") as f:
    content = f.read()

old_admin = """val roleAdminId = roleDao.insertRole(RoleEntity(name = "Administrator", canEdit = true, canDelete = true, canCreate = true, canManageProducts = true, canViewReports = true, canManageStock = true, canReturnSales = true))"""
new_admin = """val roleAdminId = roleDao.insertRole(RoleEntity(name = "Administrator", canViewDashboard = true, canViewCashier = true, canViewProducts = true, canViewReports = true, canViewSettings = true))"""

old_kasir = """val roleKasirId = roleDao.insertRole(RoleEntity(name = "Kasir", canEdit = false, canDelete = false, canCreate = true, canManageProducts = false, canViewReports = false, canManageStock = false, canReturnSales = false))"""
new_kasir = """val roleKasirId = roleDao.insertRole(RoleEntity(name = "Kasir", canViewDashboard = false, canViewCashier = true, canViewProducts = false, canViewReports = false, canViewSettings = false))"""

content = content.replace(old_admin, new_admin)
content = content.replace(old_kasir, new_kasir)

with open("app/src/main/java/com/yofidewo/pos/data/PosDatabase.kt", "w") as f:
    f.write(content)

