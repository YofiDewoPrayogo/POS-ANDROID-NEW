package com.yofidewo.pos.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object CashierPos : Screen("cashier_pos", "Kasir")
    object Products : Screen("products", "Stok")
    object Reports : Screen("reports", "Laporan")
    object Settings : Screen("settings", "Pengaturan")

    // Sub-routes / backwards compatibility
    object Dashboard : Screen("dashboard", "Dashboard")
    object CategoriesBrands : Screen("categories_brands", "Kategori & Brand")
    object ReceivingNotes : Screen("receiving_notes", "Penerimaan Stok")
    object Transactions : Screen("transactions", "Riwayat Penjualan")
    object SettingsUsers : Screen("settings_users", "Pengaturan")
    object OutletSetup : Screen("outlet_setup", "Setup Outlet")
    object SuperAdmin : Screen("super_admin", "Super Admin")
    object TableLayout : Screen("table_layout", "Denah Meja F&B")
    object KitchenDisplay : Screen("kitchen_display", "Layar Dapur")
}


