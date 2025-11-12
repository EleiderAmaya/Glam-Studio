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
 * Detalle de cita.
 *
 * Reutilización:
 * - Para navegar con parámetros: "appointments/detail/{appointmentId}" y carga en ViewModel.
 * - Acciones comunes: reprogramar, cancelar, convertir en factura (usa callbacks).
 * - Muestra secciones: cliente, servicio, hora, notas; extrae cada una si crecen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de cita") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Cliente, servicio, hora, acciones…")
        }
    }
}


