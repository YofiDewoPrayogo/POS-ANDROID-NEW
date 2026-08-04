import re

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "r") as f:
    content = f.read()

nav_logic = """
    val currentRole by viewModel.currentRole.collectAsState()
    
    val navItems = buildList {
        if (currentRole?.name == "Administrator") {
            add(NavItem(Screen.Dashboard, Icons.Default.Dashboard))
        }
        add(NavItem(Screen.CashierPos, Icons.Default.PointOfSale))
        
        if (currentRole?.canManageProducts == true || currentRole?.name == "Administrator") {
            add(NavItem(Screen.Products, Icons.Default.Inventory))
        }
        
        add(NavItem(Screen.Reports, Icons.Default.Receipt))
        
        if (currentRole?.name == "Administrator") {
            add(NavItem(Screen.Settings, Icons.Default.Settings))
        }
    }
"""

content = re.sub(r'    val navItems = listOf\([\s\S]*?\)', nav_logic.strip(), content)

with open("app/src/main/java/com/yofidewo/pos/MainActivity.kt", "w") as f:
    f.write(content)
