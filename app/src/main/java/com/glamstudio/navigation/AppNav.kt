package com.glamstudio.navigation

/**
 * Guía rápida de navegación (Compose + Navigation)
 *
 * Conceptos:
 * - Route: rutas tipadas para cada pantalla. Centraliza y evita strings sueltos.
 * - NavHost: registro de pantallas (`composable`) y ruta de inicio.
 * - Bottom navigation: items declarados en `bottomItems`. Cada item apunta a una `Route` principal.
 *
 * Nueva pantalla principal (con tab en bottom bar):
 * 1) Crea `MiNuevaScreen` en `com.glamstudio.ui.screens`.
 * 2) Agrega `data object MiNueva : Route("mi_nueva")` en `Route`.
 * 3) Registra en `NavHost`: `composable(Route.MiNueva.path) { MiNuevaScreen(...) }`.
 * 4) Añade un `BottomItem` a `bottomItems` con iconos y label.
 *
 * Pantalla secundaria (flujo sin tab):
 * 1) Crea el composable, p. ej. `DetalleXScreen`.
 * 2) Añade `data object DetalleX : Route("x/detail")` en `Route`.
 * 3) Registra su `composable` en el `NavHost` y navega con `navController.navigate(Route.DetalleX.path)`.
 *
 * Tips:
 * - Usa callbacks en las pantallas (lambdas) y resuelve la navegación aquí.
 * - Rutas cortas y consistentes. Para parámetros: define `clients/detail/{id}` y args en el `NavHost`.
 */
import androidx.compose.animation.AnimatedContentScope
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
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.glamstudio.ui.screens.ScheduleAppointmentScreen
import java.time.LocalDate

/**
 * Rutas tipadas de la app.
 *
 * Principales: aparecen en la barra inferior.
 * Secundarias: pantallas a las que se llega por flujo (crear/editar/detalle).
 *
 * Para rutas con parámetros, usa placeholders (e.g. "clients/detail/{id}") y define los argumentos
 * al registrar el `composable` en el `NavHost`.
 */
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
    data object ClientDetail : Route("clients/detail/{id}") {
        fun create(id: String) = "clients/detail/$id"
    }
    data object ServiceDetail : Route("services/detail/{id}") {
        fun create(id: String) = "services/detail/$id"
    }
    data object AppointmentDetail : Route("appointments/detail/{id}") {
        fun create(id: String) = "appointments/detail/$id"
    }
    object ScheduleAppointmentScreen : Route("schedule/{date}") {
        fun createRoute(date: LocalDate) = "schedule/${date.toString()}"
    }
}

/**
 * Modelo de item para el bottom navigation.
 * - route: ruta principal asociada
 * - label: texto visible
 * - selectedIcon/unselectedIcon: iconos para estados
 */
data class BottomItem(
    val route: Route,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

// Items visibles en la barra inferior. Agrega o quita según las pantallas principales.
private val bottomItems = listOf(
    BottomItem(Route.Home, "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    BottomItem(Route.Clients, "Clientes", Icons.Filled.Group, Icons.Outlined.Group),
    BottomItem(Route.Services, "Servicios", Icons.Filled.ContentCut, Icons.Outlined.ContentCut),
    BottomItem(Route.Calendar, "Calendario", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomItem(Route.Billing, "Facturas", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * Punto de entrada de la UI:
 * - Declara `Scaffold` con barra inferior.
 * - Gestiona `NavController` y registra rutas en el `NavHost`.
 *
 * Reutilización:
 * - Las pantallas exponen callbacks (onClick...) y la navegación se decide aquí.
 * - Para añadir pantallas nuevas: define `Route`, registra `composable` y, si es principal, añade `BottomItem`.
 */
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
                            // Navegación con preservación de estado:
                            // 1) Si la ruta ya existe en el back stack, volvemos a ella.
                            // 2) Si no, navegamos evitando duplicados y restaurando estado.
                            val popped = navController.popBackStack(item.route.path, false)
                            if (!popped) {
                                navController.navigate(item.route.path) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
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
            // Pantallas principales
            composable(Route.Home.path) {
                HomeScreen(
                    onFabClick = { navController.navigate(Route.Calendar.path) },
                    onReportsClick = { navController.navigate(Route.Reports.path) },
                    onAppointmentClick = { id -> navController.navigate(Route.AppointmentDetail.create(id)) }
                )
            }
            composable(Route.Clients.path) {
                ClientsScreen(
                    onAddClick = { navController.navigate(Route.NewClient.path) },
                    onItemClick = { client -> navController.navigate(Route.ClientDetail.create(client.id)) }
                )
            }
            composable(Route.Services.path) {
                ServicesScreen(
                    onAddClick = { navController.navigate(Route.NewService.path) },
                    onItemClick = { service -> navController.navigate(Route.ServiceDetail.create(service.id)) }
                )
            }
            composable(Route.Calendar.path) {
                CalendarScreen(
                    onDaySelected = { date -> navController.navigate(Route.ScheduleAppointmentScreen.createRoute(date)) },
                    onGenerateInvoice = { navController.navigate(Route.Invoice.path) },
                    onAppointmentClick = { id -> navController.navigate(Route.AppointmentDetail.create(id)) }
                )
            }
            
            composable(Route.Billing.path) { InvoiceScreen(showBack = false, onViewReports = { navController.navigate(Route.Reports.path) }) }
            composable(Route.Reports.path) { ReportsScreen(onBack = { navController.popBackStack() }) }

            // Pantallas secundarias (flujo)
            composable(Route.NewClient.path) { NewClientScreen(onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() }) }
            composable(Route.NewService.path) { NewServiceScreen(onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() }) }
            composable(Route.Invoice.path) { InvoiceScreen(showBack = true, onBack = { navController.popBackStack() }, onViewReports = { navController.navigate(Route.Reports.path) }) }

            composable(Route.ClientDetail.path, arguments = listOf(navArgument("id") { type = NavType.StringType })) { backStack ->
                val id = backStack.arguments?.getString("id") ?: return@composable
                ClientDetailScreen(onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() })
            }
            composable(Route.ServiceDetail.path, arguments = listOf(navArgument("id") { type = NavType.StringType })) { backStack ->
                val id = backStack.arguments?.getString("id") ?: return@composable
                ServiceDetailScreen(onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() })
            }
            composable(Route.AppointmentDetail.path, arguments = listOf(navArgument("id") { type = NavType.StringType })) { backStack ->
                val id = backStack.arguments?.getString("id") ?: return@composable
                AppointmentDetailScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = Route.ScheduleAppointmentScreen.path,
                arguments = listOf(navArgument("date") { type = NavType.StringType })
            ) { backStackEntry ->
                val dateString = backStackEntry.arguments?.getString("date")
                if (dateString != null) {
                    val selectedDate = LocalDate.parse(dateString)
                    ScheduleAppointmentScreen(date = selectedDate, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
