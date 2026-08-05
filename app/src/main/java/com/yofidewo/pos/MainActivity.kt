package com.yofidewo.pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yofidewo.pos.ui.PosViewModel
import com.yofidewo.pos.ui.PosViewModelFactory
import com.yofidewo.pos.ui.components.CustomLogo
import com.yofidewo.pos.ui.components.InvoiceDialog
import com.yofidewo.pos.ui.navigation.Screen
import com.yofidewo.pos.ui.screens.*
import com.yofidewo.pos.ui.theme.PosTheme

data class NavItem(
    val screen: Screen,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {
    private val viewModel: PosViewModel by viewModels {
        PosViewModelFactory((application as PosApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PosTheme {
                val currentUser by viewModel.currentUser.collectAsState()
                
                if (currentUser != null) {
                    MainAppLayout(viewModel = viewModel)
                } else {
                    LoginScreen(viewModel = viewModel, onLoginSuccess = { /* Automatically handled by state */ })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout(viewModel: PosViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.CashierPos.route
    val selectedInvoiceTx by viewModel.selectedTransactionForInvoice.collectAsState()

    val currentUser by viewModel.currentUser.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val outletName by viewModel.outletName.collectAsState()
    val isActivated by viewModel.isActivated.collectAsState()
    val customLogoBitmap by viewModel.customLogoBitmap.collectAsState()

    val showHeader = currentRoute == Screen.Dashboard.route || currentRoute == Screen.Settings.route

    val navItems = buildList {
        if (currentRole?.name == "Administrator") {
            add(NavItem(Screen.Dashboard, Icons.Default.Dashboard))
        }
        add(NavItem(Screen.CashierPos, Icons.Default.PointOfSale))
        if (currentRole?.canViewProducts == true || currentRole?.name == "Administrator") {
            add(NavItem(Screen.Products, Icons.Default.Inventory))
        }
        add(NavItem(Screen.Reports, Icons.Default.Receipt))
        if (currentRole?.name == "Administrator") {
            add(NavItem(Screen.Settings, Icons.Default.Settings))
        }
    }

    Scaffold(
        topBar = {
            if (showHeader) {
                Surface(
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp,
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Outlet Logo & Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CustomLogo(modifier = Modifier.size(36.dp), bitmap = customLogoBitmap)
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        outletName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    if (isActivated) {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Lisensi Aktif",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                                Text(
                                    if (isActivated) "WarungKu POS • PRO" else "WarungKu POS • Online",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Right: User Logo / Avatar & User Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    currentUser?.name ?: "Kasir Utama",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    currentRole?.name ?: "User",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF334155)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    (currentUser?.name?.take(1) ?: "U").uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                shadowElevation = 12.dp,
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
            ) {
                NavigationBar(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.screen.route ||
                                (item.screen == Screen.Reports && (currentRoute == Screen.Dashboard.route || currentRoute == Screen.Transactions.route)) ||
                                (item.screen == Screen.Products && (currentRoute == Screen.CategoriesBrands.route || currentRoute == Screen.ReceivingNotes.route)) ||
                                (item.screen == Screen.Settings && currentRoute == Screen.SettingsUsers.route)

                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B),
                                indicatorColor = Color(0xFF1E293B)
                            ),
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.screen.route) {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.screen.title, modifier = Modifier.size(22.dp)) },
                            label = { Text(item.screen.title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.CashierPos.route
            ) {
                composable(Screen.CashierPos.route) {
                    CashierPosScreen(viewModel = viewModel)
                }
                composable(Screen.Products.route) {
                    ProductsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                }
                composable(Screen.Reports.route) {
                    ReportsTransactionsScreen(
                        viewModel = viewModel,
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsUsersScreen(viewModel = viewModel)
                }

                // Internal/legacy sub-routes
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable(Screen.CategoriesBrands.route) {
                    CategoriesBrandsScreen(viewModel = viewModel)
                }
                composable(Screen.ReceivingNotes.route) {
                    ReceivingNotesScreen(viewModel = viewModel)
                }
                composable(Screen.Transactions.route) {
                    ReportsTransactionsScreen(
                        viewModel = viewModel,
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable(Screen.SettingsUsers.route) {
                    SettingsUsersScreen(viewModel = viewModel)
                }
                composable(Screen.SuperAdmin.route) {
                    com.yofidewo.pos.ui.screens.SuperAdminScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                }
                composable(Screen.TableLayout.route) {
                    TableLayoutScreen(viewModel = viewModel)
                }
            }
        }

        // Global Invoice Modal Dialog when a transaction is completed or selected
        selectedInvoiceTx?.let { tx ->
            InvoiceDialog(
                transaction = tx,
                viewModel = viewModel,
                onDismiss = { viewModel.selectedTransactionForInvoice.value = null }
            )
        }

        // Global Loading Animation Overlay
        val isGlobalLoading by viewModel.isLoading.collectAsState()
        if (isGlobalLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F172A),
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(36.dp))
                        Text("Memproses Data...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
