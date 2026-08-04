import re

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "canEdit = false; canDelete = false; canCreate = false; canManageProducts = false; canViewReports = false; canManageStock = false; canReturnSales = false",
    "canViewDashboard = false; canViewCashier = false; canViewProducts = false; canViewReports = false; canViewSettings = false"
)

content = content.replace(
    "canEdit = role.canEdit; canDelete = role.canDelete; canCreate = role.canCreate; canManageProducts = role.canManageProducts; canViewReports = role.canViewReports; canManageStock = role.canManageStock; canReturnSales = role.canReturnSales",
    "canViewDashboard = role.canViewDashboard; canViewCashier = role.canViewCashier; canViewProducts = role.canViewProducts; canViewReports = role.canViewReports; canViewSettings = role.canViewSettings"
)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt", "w") as f:
    f.write(content)

