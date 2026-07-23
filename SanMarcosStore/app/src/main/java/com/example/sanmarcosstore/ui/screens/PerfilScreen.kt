package com.example.sanmarcosstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.sanmarcosstore.ui.viewmodel.StoreViewModel

data class OpcionPerfil(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector
)

private val opcionesPerfil = listOf(
    OpcionPerfil(1, "Mis pedidos", "Historial", Icons.Filled.Receipt),
    OpcionPerfil(2, "Mis favoritos", "Productos guardados", Icons.Filled.Favorite),
    OpcionPerfil(3, "Direcciones", "Envios", Icons.Filled.LocationOn),
    OpcionPerfil(4, "Configuracion", "Ajustes", Icons.Filled.Settings)
)

@Composable
fun PerfilScreen(viewModel: StoreViewModel) {
    val modoOscuro by viewModel.darkMode.collectAsState()
    var notificaciones by remember { mutableStateOf(true) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Foto de perfil",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            "Angelo Alama",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            "angelo.alama@unmsm.edu.pe",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            "Estudiante desde 2021",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EstadisticaCard("12", "Pedidos", Modifier.weight(1f))
                EstadisticaCard("5", "Favoritos", Modifier.weight(1f))
                EstadisticaCard("3", "Cupones", Modifier.weight(1f))
            }
        }

        item {
            Text(
                "Mi cuenta",
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(opcionesPerfil, key = { it.id }) { opcion ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                ListItem(
                    headlineContent = { Text(opcion.titulo) },
                    supportingContent = { Text(opcion.descripcion) },
                    leadingContent = {
                        Icon(
                            opcion.icono,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Ir a ${opcion.titulo}",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )
            }
        }

        item {
            Text(
                "Preferencias",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Modo oscuro") },
                        supportingContent = { Text("Cambia el aspecto") },
                        trailingContent = {
                            Switch(
                                checked = modoOscuro,
                                onCheckedChange = { viewModel.toggleDarkMode(it) }
                            )
                        }
                    )

                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Notificaciones") },
                        supportingContent = { Text("Alertas de promociones") },
                        trailingContent = {
                            Switch(
                                checked = notificaciones,
                                onCheckedChange = { notificaciones = it }
                            )
                        }
                    )
                }
            }
        }

        item {
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null)

                Spacer(modifier = Modifier.width(8.dp))

                Text("Cerrar sesion")
            }
        }
    }
}

@Composable
private fun EstadisticaCard(
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                valor,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Text(
                etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}
