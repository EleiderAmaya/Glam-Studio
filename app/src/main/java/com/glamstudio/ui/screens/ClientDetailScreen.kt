package com.glamstudio.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun ClientDetailScreen(onSaved: () -> Unit, onBack: () -> Unit = {}) {
    val nombre = remember { mutableStateOf("") }
    val telefono = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val direccion = remember { mutableStateOf("") }
    val barrio = remember { mutableStateOf("") }
    val notas = remember { mutableStateOf("") }
    val activo = remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de cliente") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás") } }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(value = nombre.value, onValueChange = { nombre.value = it }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = telefono.value, onValueChange = { telefono.value = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Email (opcional)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = direccion.value, onValueChange = { direccion.value = it }, label = { Text("Dirección principal") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = barrio.value, onValueChange = { barrio.value = it }, label = { Text("Barrio (opcional)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = notas.value, onValueChange = { notas.value = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Estado: Activo", modifier = Modifier.weight(1f))
                Switch(checked = activo.value, onCheckedChange = { activo.value = it })
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { /* eliminar */ }, modifier = Modifier.weight(1f)) { Text("Eliminar") }
                Spacer(modifier = Modifier.height(0.dp).weight(0.1f))
                Button(onClick = onSaved, modifier = Modifier.weight(1f)) { Text("Actualizar") }
            }
        }
    }
}


