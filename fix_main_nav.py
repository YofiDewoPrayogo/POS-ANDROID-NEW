import re

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "r") as f:
    content = f.read()

correct_logic = """
    val currentRole by viewModel.currentRole.collectAsState()
    
    val navItems = buildList {
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
    }
"""

content = re.sub(r'val currentRole by viewModel\.currentRole\.collectAsState\(\)[\s\S]*?NavItem\(Screen\.Settings, Icons\.Default\.Settings\)\n    \)', correct_logic.strip(), content)

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "w") as f:
    f.write(content)
