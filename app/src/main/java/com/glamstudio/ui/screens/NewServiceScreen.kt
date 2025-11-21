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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glamstudio.ui.viewmodel.NewServiceViewModel

/**
 * Formulario de creación/edición de servicio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewServiceScreen(onSaved: () -> Unit, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val vm: NewServiceViewModel = viewModel(factory = NewServiceViewModel.factory(context))

    val nombre = remember { mutableStateOf("") }
    val descripcion = remember { mutableStateOf("") }
    val duracion = remember { mutableStateOf("") }
    val precio = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo servicio") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás") }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(value = nombre.value, onValueChange = { nombre.value = it }, label = { Text("Nombre del servicio") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = descripcion.value, onValueChange = { descripcion.value = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = duracion.value, onValueChange = { duracion.value = it }, label = { Text("Duración (min)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = precio.value, onValueChange = { precio.value = it }, label = { Text("Precio (COP)") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    vm.save(
                        name = nombre.value,
                        description = descripcion.value,
                        durationMinutes = duracion.value.toIntOrNull() ?: 0,
                        priceInput = precio.value
                    )
                    onSaved()
                }, modifier = Modifier.weight(1f).padding(horizontal = 30.dp)) { Text("Guardar") }
            }
        }
    }
}


