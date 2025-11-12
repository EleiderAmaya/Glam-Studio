package com.glamstudio.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Detalle de cliente.
 *
 * Reutilización:
 * - Para navegar con ID, define ruta con parámetro (e.g. "clients/detail/{id}") y lee args en el `composable`.
 * - Carga el cliente en un ViewModel (por id) y muestra estados: cargando, error, datos.
 * - Acciones (editar, eliminar, agendar) deben exponerse como callbacks o navegar desde `AppRoot`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de cliente") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("(Placeholder) Nombre del cliente")
            Text("Contacto, etiquetas, historial…")
        }
    }
}


