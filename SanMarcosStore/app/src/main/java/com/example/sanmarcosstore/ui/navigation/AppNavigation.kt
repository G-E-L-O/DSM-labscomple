package com.example.sanmarcosstore.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sanmarcosstore.ui.screens.CarritoScreen
import com.example.sanmarcosstore.ui.screens.DetalleProductoScreen
import com.example.sanmarcosstore.ui.screens.PerfilScreen
import com.example.sanmarcosstore.ui.screens.TiendaScreen
import com.example.sanmarcosstore.ui.viewmodel.StoreViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

sealed class Ruta(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
) {
    data object Tienda : Ruta("tienda", "Tienda", Icons.Filled.Store)
    data object Carrito : Ruta("carrito", "Carrito", Icons.Filled.ShoppingCart)
    data object Perfil : Ruta("perfil", "Mi Perfil", Icons.Filled.Person)
}

private val pestanas = listOf(Ruta.Tienda, Ruta.Carrito, Ruta.Perfil)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: StoreViewModel = viewModel()) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    val tituloActual = when {
        currentDestination?.route == Ruta.Tienda.ruta -> "SanMarcos Store"
        currentDestination?.route == Ruta.Carrito.ruta -> "Mi Carrito"
        currentDestination?.route == Ruta.Perfil.ruta -> "Mi Perfil"
        currentDestination?.route?.startsWith("detalle/") == true -> "Detalle de Producto"
        else -> "SanMarcos Store"
    }

    Scaffold(
        topBar = {
            if (currentDestination?.route?.startsWith("detalle/") != true) {
                TopAppBar(
                    title = { Text(tituloActual) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        },
        bottomBar = {
            if (currentDestination?.route?.startsWith("detalle/") != true) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    pestanas.forEach { pestana ->
                        val seleccionada = currentDestination
                            ?.hierarchy
                            ?.any { it.route == pestana.ruta } == true

                        NavigationBarItem(
                            selected = seleccionada,
                            onClick = {
                                navController.navigate(pestana.ruta) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    pestana.icono,
                                    contentDescription = pestana.etiqueta
                                )
                            },
                            label = {
                                Text(pestana.etiqueta)
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Ruta.Tienda.ruta,
            modifier = Modifier.padding(padding)
        ) {
            composable(Ruta.Tienda.ruta) {
                TiendaScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { id ->
                        navController.navigate("detalle/$id")
                    }
                )
            }

            composable(Ruta.Carrito.ruta) {
                CarritoScreen()
            }

            composable(Ruta.Perfil.ruta) {
                PerfilScreen(viewModel = viewModel)
            }

            composable(
                "detalle/{productoId}",
                arguments = listOf(navArgument("productoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("productoId") ?: 0
                val producto = viewModel.getProductoById(id)
                DetalleProductoScreen(
                    producto = producto,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
