package com.example.sanmarcosstore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sanmarcosstore.model.Producto
import com.example.sanmarcosstore.ui.components.ProductoItem
import com.example.sanmarcosstore.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaScreen(
    viewModel: StoreViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var mostrarFormulario by remember { mutableStateOf(false) }
    val productos by viewModel.productos.collectAsState()
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    val categorias = remember(productos) {
        listOf("Todos") + productos.map { it.categoria }.distinct()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar producto") },
                placeholder = { Text("Ej: Cafe, Chocolate...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null)
                },
                supportingText = { Text("Escribe para filtrar") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { categoria ->
                    FilterChip(
                        selected = categoriaSeleccionada == categoria,
                        onClick = { categoriaSeleccionada = categoria },
                        label = { Text(categoria) }
                    )
                }
            }

            if (mostrarFormulario) {
                FormularioRapido()
            }

            val productosFiltrados = productos.filter {
                (it.nombre.contains(busqueda, ignoreCase = true) ||
                        it.categoria.contains(busqueda, ignoreCase = true)) &&
                        (categoriaSeleccionada == "Todos" || it.categoria == categoriaSeleccionada)
            }

            if (productosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No se encontraron productos",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productosFiltrados, key = { it.id }) { producto ->
                        ProductoItem(
                            producto = producto,
                            onToggleFavorite = {
                                viewModel.toggleFavorite(producto.id)
                            },
                            onClick = { onNavigateToDetail(producto.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { mostrarFormulario = !mostrarFormulario },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar producto")
        }
    }
}

@Composable
private fun FormularioRapido() {
    var nombreProducto by remember { mutableStateOf("") }
    val esError = nombreProducto.length > 30

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Agregar producto rapido",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nombreProducto,
                onValueChange = { nombreProducto = it },
                label = { Text("Nombre del producto") },
                supportingText = {
                    if (esError) {
                        Text(
                            "Maximo 30 caracteres",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("${nombreProducto.length}/30")
                    }
                },
                trailingIcon = {
                    if (esError) {
                        Icon(
                            Icons.Filled.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = esError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(onClick = {}, modifier = Modifier.weight(1f)) {
                    Text("Guardar")
                }

                FilledTonalButton(onClick = {}, modifier = Modifier.weight(1f)) {
                    Text("Borrador")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ElevatedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                    Text("Vista previa")
                }

                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                    Text("Limpiar")
                }
            }

            TextButton(onClick = {}) {
                Text("Cancelar")
            }
        }
    }
}
