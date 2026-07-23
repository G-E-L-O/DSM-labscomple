package com.illareklab.demodata.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.illareklab.demodata.DemoDataApp
import com.illareklab.demodata.ui.screens.*
import com.illareklab.demodata.ui.viewmodel.*

import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*

sealed class Ruta(val ruta: String, val etiqueta: String, val icono: ImageVector) {

    object Gps    : Ruta("gps",    "GNSS",         Icons.Default.MyLocation)
    object Perfil : Ruta("perfil", "Perfil",       Icons.Default.AccountCircle)

    object Login         : Ruta("login",         "Login",        Icons.Default.Lock)
    object Media         : Ruta("media",         "Cámara",       Icons.Default.PhotoCamera)
    object Audio         : Ruta("audio",         "Audio",        Icons.Default.Mic)
    object Notifications : Ruta("notifications", "Alertas",      Icons.Default.Notifications)
    object Sync          : Ruta("sync",          "Sync",         Icons.Default.Sync)
    object History       : Ruta("history",       "Histo.",       Icons.AutoMirrored.Filled.List)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(gpsViewModel: GpsViewModel, sessionViewModel: SessionViewModel) {
    val contextApp = LocalContext.current.applicationContext as DemoDataApp
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    val isLoggedIn by sessionViewModel.isLoggedIn.collectAsState()

    val mediaViewModel: MediaViewModel = viewModel(factory = MediaViewModelFactory(contextApp.mediaRepository, contextApp.fileStorage))
    val audioViewModel: AudioViewModel = viewModel(factory = AudioViewModel.Factory(contextApp, contextApp.audioRepository, contextApp.fileStorage))
    val syncViewModel: SyncViewModel = viewModel(factory = SyncViewModel.Factory(contextApp, contextApp.gpsRepository, contextApp.mediaRepository, contextApp.audioRepository))
    val historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory(contextApp.gpsRepository, contextApp.mediaRepository, contextApp.audioRepository, contextApp.fileStorage))

    val tabs = listOf(
        Ruta.Gps,
        Ruta.Media,
        Ruta.Audio,
        Ruta.Notifications,
        Ruta.Sync,
        Ruta.History,
        Ruta.Perfil
    )

    Scaffold(
        topBar = {
            if (isLoggedIn && currentDestination?.route != Ruta.Login.ruta) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentDestination?.route) {
                                Ruta.Gps.ruta -> "Lab 04: GNSS Dual"
                                Ruta.Media.ruta -> "Lab 05: Captura Multimedia"
                                Ruta.Audio.ruta -> "Lab 05: Grabadora"
                                Ruta.Notifications.ruta -> "Lab 05: Alertas Diferidas"
                                Ruta.Sync.ruta -> "Lab 05: Estado Base Datos"
                                Ruta.History.ruta -> "Lab 05: Historial Polimórfico"
                                else -> "Panel del Estudiante"
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                )
            }
        },
        bottomBar = {
            if (isLoggedIn && currentDestination?.route != Ruta.Login.ruta) {
                NavigationBar {
                    tabs.forEach { tab ->
                        val seleccionada = currentDestination?.hierarchy?.any { it.route == tab.ruta } == true
                        NavigationBarItem(
                            selected = seleccionada,
                            alwaysShowLabel = false,
                            onClick = {
                                navController.navigate(tab.ruta) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icono, contentDescription = tab.etiqueta) },
                            label = {
                                Text(
                                    text = tab.etiqueta,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        val startRoute = if (isLoggedIn) Ruta.Gps.ruta else Ruta.Login.ruta

        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Ruta.Login.ruta) {
                LoginScreen { user, pass, onResult ->
                    sessionViewModel.login(user, pass) { exito ->
                        onResult(exito)
                        if (exito) {
                            navController.navigate(Ruta.Gps.ruta) {
                                popUpTo(Ruta.Login.ruta) { inclusive = true }
                            }
                        }
                    }
                }
            }

            composable(Ruta.Gps.ruta) {
                GpsScreen(viewModel = gpsViewModel)
            }

            composable(Ruta.Perfil.ruta) {
                ProfileScreen(
                    sessionVm = sessionViewModel,
                    gpsVm = gpsViewModel,
                    onLogout = {
                        sessionViewModel.logout()
                        navController.navigate(Ruta.Login.ruta) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Ruta.Media.ruta) {
                MediaScreen(mediaViewModel = mediaViewModel)
            }

            composable(Ruta.Audio.ruta) {
                AudioScreen(audioViewModel = audioViewModel)
            }

            composable(Ruta.Notifications.ruta) {
                NotificationsScreen(syncViewModel = syncViewModel)
            }

            composable(Ruta.Sync.ruta) {
                SyncScreen(syncViewModel = syncViewModel)
            }

            composable(Ruta.History.ruta) {
                HistoryScreen(historyViewModel = historyViewModel)
            }
        }
    }
}
