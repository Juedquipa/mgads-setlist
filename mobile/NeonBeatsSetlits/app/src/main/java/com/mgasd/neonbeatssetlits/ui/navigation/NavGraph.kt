package com.mgasd.neonbeatssetlits.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mgasd.neonbeatssetlits.ui.screens.admin.*
import com.mgasd.neonbeatssetlits.ui.screens.cliente.*
import com.mgasd.neonbeatssetlits.ui.screens.mesero.*
import com.mgasd.neonbeatssetlits.viewmodel.AdminViewModel
import com.mgasd.neonbeatssetlits.viewmodel.ClienteViewModel
import com.mgasd.neonbeatssetlits.viewmodel.MeseroViewModel

sealed class Screen(val route: String) {
    object ClientFlow : Screen("cliente_flow")
    object WaiterFlow : Screen("mesero_flow")
    object AdminFlow : Screen("admin_flow")
    
    // Cliente Screens
    object Splash : Screen("splash")
    object ScanQR : Screen("scan_qr")
    object TableIdentified : Screen("table_identified")
    object EnterPin : Screen("enter_pin")
    object HomeClient : Screen("home_cliente")
    object SearchSongs : Screen("buscar_canciones")
    object PlayQueue : Screen("cola_reproduccion")
    object MyRequests : Screen("mis_solicitudes")
    object MyTable : Screen("mi_mesa")

    // Mesero Screens
    object LoginWaiter : Screen("login_mesero")
    object HomeWaiter : Screen("home_mesero")
    object GenerateCode : Screen("generacion_codigo")

    // Admin Screens
    object LoginAdmin : Screen("login_admin")
    object DashboardDj : Screen("dashboard_dj")
    object QueueManagement : Screen("queue_management")
    object Approvals : Screen("aprobaciones")
}

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    clienteViewModel: ClienteViewModel = viewModel(),
    meseroViewModel: MeseroViewModel = viewModel(),
    adminViewModel: AdminViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ClientFlow.route
    ) {
        // Subgrafo del Cliente
        navigation(
            startDestination = Screen.Splash.route,
            route = Screen.ClientFlow.route
        ) {
            composable(Screen.Splash.route) {
                A1_SplashScreen(
                    onNavigateToScanner = {
                        navController.navigate(Screen.ScanQR.route)
                    },
                    onNavigateToMeseroLogin = {
                        navController.navigate(Screen.WaiterFlow.route)
                    }
                )
            }
            composable(Screen.ScanQR.route) {
                A2_EscaneoQR(
                    viewModel = clienteViewModel,
                    onBack = { navController.popBackStack() },
                    onMesaIdentificada = {
                        navController.navigate(Screen.TableIdentified.route)
                    }
                )
            }
            composable(Screen.TableIdentified.route) {
                MesaIdentificadaScreen(
                    viewModel = clienteViewModel,
                    onEnterOrderCode = { navController.navigate(Screen.EnterPin.route) },
                    onViewQueue = { navController.navigate(Screen.HomeClient.route) }
                )
            }
            composable(Screen.EnterPin.route) {
                IngresoPinScreen(
                    viewModel = clienteViewModel,
                    onBack = { navController.popBackStack() },
                    onPinValidated = { 
                        navController.navigate(Screen.HomeClient.route) {
                            popUpTo(Screen.TableIdentified.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.HomeClient.route) {
                HomeClienteScreen(
                    viewModel = clienteViewModel,
                    onSearchTrack = { navController.navigate(Screen.SearchSongs.route) },
                    onRequestsClick = { navController.navigate(Screen.MyRequests.route) },
                    onBillsClick = { navController.navigate(Screen.MyTable.route) },
                    onQueueClick = { navController.navigate(Screen.PlayQueue.route) },
                    onHomeClick = { /* Already here */ }
                )
            }
            composable(Screen.SearchSongs.route) {
                BuscarCancionesScreen(
                    viewModel = clienteViewModel,
                    onHomeClick = { navController.navigate(Screen.HomeClient.route) },
                    onRequestsClick = { navController.navigate(Screen.MyRequests.route) },
                    onBillsClick = { navController.navigate(Screen.MyTable.route) }
                )
            }
            composable(Screen.PlayQueue.route) {
                ColaReproduccionScreen(
                    viewModel = clienteViewModel,
                    onHomeClick = { navController.navigate(Screen.HomeClient.route) },
                    onRequestsClick = { navController.navigate(Screen.MyRequests.route) },
                    onBillsClick = { navController.navigate(Screen.MyTable.route) }
                )
            }
            composable(Screen.MyRequests.route) {
                MisSolicitudesScreen(
                    viewModel = clienteViewModel,
                    onHomeClick = { navController.navigate(Screen.HomeClient.route) },
                    onRequestsClick = { /* Already here */ },
                    onBillsClick = { navController.navigate(Screen.MyTable.route) }
                )
            }
            composable(Screen.MyTable.route) {
                MiMesaScreen(
                    viewModel = clienteViewModel,
                    onHomeClick = { navController.navigate(Screen.HomeClient.route) },
                    onRequestsClick = { navController.navigate(Screen.MyRequests.route) },
                    onBillsClick = { /* Already here */ }
                )
            }
        }

        // Subgrafo del Mesero
        navigation(
            startDestination = Screen.LoginWaiter.route,
            route = Screen.WaiterFlow.route
        ) {
            composable(Screen.LoginWaiter.route) {
                B1_LoginMeseroScreen(
                    viewModel = meseroViewModel,
                    onNavigateToDashboard = {
                        navController.navigate(Screen.HomeWaiter.route) {
                            popUpTo(Screen.LoginWaiter.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.HomeWaiter.route) {
                B2_HomeMeseroScreen(
                    viewModel = meseroViewModel,
                    onNavigateToGenerateCode = {
                        navController.navigate(Screen.GenerateCode.route)
                    },
                    onNavigateToProfile = { /* TODO */ },
                    onNavigateToRequests = { /* TODO */ }
                )
            }
            composable(Screen.GenerateCode.route) {
                B3_GeneracionDeCodigoScreen(
                    viewModel = meseroViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Subgrafo del Administrador
        navigation(
            startDestination = Screen.LoginAdmin.route,
            route = Screen.AdminFlow.route
        ) {
            composable(Screen.LoginAdmin.route) {
                AdminLoginScreen(
                    viewModel = adminViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.DashboardDj.route) {
                            popUpTo(Screen.LoginAdmin.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.DashboardDj.route) {
                DashboardDjScreen(
                    viewModel = adminViewModel
                )
            }
            composable(Screen.QueueManagement.route) {
                QueueManagementScreen(
                    viewModel = adminViewModel
                )
            }
            composable(Screen.Approvals.route) {
                AprobacionesScreen(
                    viewModel = adminViewModel
                )
            }
        }
    }
}
