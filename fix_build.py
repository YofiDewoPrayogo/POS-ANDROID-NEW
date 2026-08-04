import re

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("currentRole?.canManageProducts", "currentRole?.canViewProducts")
content = content.replace("currentRole?.canViewReports", "currentRole?.canViewReports") # Already matches
# Re-apply the whole block properly
old_block = """        if (currentRole?.canManageProducts == true || currentRole?.name == "Administrator") {
            add(NavItem(Screen.Products, androidx.compose.material.icons.Icons.Default.Inventory))
        }"""
new_block = """        if (currentRole?.canViewProducts == true || currentRole?.name == "Administrator") {
            add(NavItem(Screen.Products, androidx.compose.material.icons.Icons.Default.Inventory))
        }"""
content = content.replace(old_block, new_block)

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ReportsTransactionsScreen.kt", "r") as f:
    content2 = f.read()

# Replace canReturnSales with canViewDashboard or something similar, or just allow admin
content2 = content2.replace("currentRole?.canReturnSales == true", "currentRole?.name == \"Administrator\"")

with open("app/src/main/java/com/yofidewo/pos/ui/screens/ReportsTransactionsScreen.kt", "w") as f:
    f.write(content2)

