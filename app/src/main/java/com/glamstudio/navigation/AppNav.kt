package com.glamstudio.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.glamstudio.ui.screens.CalendarScreen
import com.glamstudio.ui.screens.ClientsScreen
import com.glamstudio.ui.screens.HomeScreen
import com.glamstudio.ui.screens.InvoiceScreen
import com.glamstudio.ui.screens.NewClientScreen
import com.glamstudio.ui.screens.NewServiceScreen
import com.glamstudio.ui.screens.ServicesScreen
import com.glamstudio.ui.screens.ReportsScreen
import com.glamstudio.ui.screens.ClientDetailScreen
import com.glamstudio.ui.screens.ServiceDetailScreen
import com.glamstudio.ui.screens.AppointmentDetailScreen

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Clients : Route("clients")
    data object Services : Route("services")
    data object Calendar : Route("calendar")
    data object Billing : Route("billing")
    data object Reports : Route("reports")

    // secundarios
    data object NewClient : Route("clients/new")
    data object NewService : Route("services/new")
    data object Invoice : Route("billing/invoice")
    data object ClientDetail : Route("clients/detail")
    data object ServiceDetail : Route("services/detail")
    data object AppointmentDetail : Route("appointments/detail")
}

data class BottomItem(
    val route: Route,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val bottomItems = listOf(
    BottomItem(Route.Home, "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    BottomItem(Route.Clients, "Clientes", Icons.Filled.Group, Icons.Outlined.Group),
    BottomItem(Route.Services, "Servicios", Icons.Filled.ContentCut, Icons.Outlined.ContentCut),
    BottomItem(Route.Calendar, "Calendario", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomItem(Route.Billing, "Facturación", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->
                    val selected = currentDestination?.route == item.route.path
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            val popped = navController.popBackStack(item.route.path, false)
                            if (!popped) {
                                navController.navigate(item.route.path) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors()
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.path,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Route.Home.path) {
                HomeScreen(
                    onFabClick = { navController.navigate(Route.Calendar.path) },
                    onReportsClick = { navController.navigate(Route.Reports.path) },
                    onAppointmentClick = { navController.navigate(Route.AppointmentDetail.path) }
                )
            }
            composable(Route.Clients.path) {
                ClientsScreen(
                    onAddClick = { navController.navigate(Route.NewClient.path) },
                    onItemClick = { navController.navigate(Route.ClientDetail.path) }
                )
            }
            composable(Route.Services.path) {
                ServicesScreen(
                    onAddClick = { navController.navigate(Route.NewService.path) },
                    onItemClick = { navController.navigate(Route.ServiceDetail.path) }
                )
            }
            composable(Route.Calendar.path) {
                CalendarScreen(
                    onGenerateInvoice = { navController.navigate(Route.Invoice.path) },
                    onAppointmentClick = { navController.navigate(Route.AppointmentDetail.path) }
                )
            }
            composable(Route.Billing.path) { InvoiceScreen(showBack = false, onViewReports = { navController.navigate(Route.Reports.path) }) }
            composable(Route.Reports.path) { ReportsScreen(onBack = { navController.popBackStack() }) }

            // secundarios
            composable(Route.NewClient.path) { NewClientScreen(onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() }) }
            composable(Route.NewService.path) { NewServiceScreen(onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() }) }
            composable(Route.Invoice.path) { InvoiceScreen(showBack = true, onBack = { navController.popBackStack() }, onViewReports = { navController.navigate(Route.Reports.path) }) }
            composable(Route.ClientDetail.path) { ClientDetailScreen(onBack = { navController.popBackStack() }) }
            composable(Route.ServiceDetail.path) { ServiceDetailScreen(onBack = { navController.popBackStack() }) }
            composable(Route.AppointmentDetail.path) { AppointmentDetailScreen(onBack = { navController.popBackStack() }) }
        }
    }
}


