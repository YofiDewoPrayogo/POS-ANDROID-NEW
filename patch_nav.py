import re

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "r") as f:
    content = f.read()

old_nav = """        val navItems = buildList {
        if (currentRole?.name == "Administrator") {
            add(NavItem(Screen.Dashboard, androidx.compose.material.icons.Icons.Default.Dashboard))
        }
        add(NavItem(Screen.CashierPos, androidx.compose.material.icons.Icons.Default.PointOfSale))
        if (currentRole?.canManageProducts == true || currentRole?.name == "Administrator") {
            add(NavItem(Screen.Products, androidx.compose.material.icons.Icons.Default.Inventory))
        }
        add(NavItem(Screen.Reports, androidx.compose.material.icons.Icons.Default.Receipt))
        if (currentRole?.name == "Administrator") {
            add(NavItem(Screen.Settings, androidx.compose.material.icons.Icons.Default.Settings))
        }
    }"""

new_nav = """        val navItems = buildList {
        if (currentRole?.canViewDashboard == true || currentRole?.name == "Administrator") {
            add(NavItem(Screen.Dashboard, androidx.compose.material.icons.Icons.Default.Dashboard))
        }
        if (currentRole?.canViewCashier == true || currentRole?.name == "Administrator") {
            add(NavItem(Screen.CashierPos, androidx.compose.material.icons.Icons.Default.PointOfSale))
        }
        if (currentRole?.canViewProducts == true || currentRole?.name == "Administrator") {
            add(NavItem(Screen.Products, androidx.compose.material.icons.Icons.Default.Inventory))
        }
        if (currentRole?.canViewReports == true || currentRole?.name == "Administrator") {
            add(NavItem(Screen.Reports, androidx.compose.material.icons.Icons.Default.Receipt))
        }
        if (currentRole?.canViewSettings == true || currentRole?.name == "Administrator") {
            add(NavItem(Screen.Settings, androidx.compose.material.icons.Icons.Default.Settings))
        }
    }"""

content = content.replace(old_nav, new_nav)

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "w") as f:
    f.write(content)

