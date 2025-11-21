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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glamstudio.ui.viewmodel.ServiceDetailViewModel

/**
 * Detalle de servicio con carga por ID y edición.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(serviceId: String, onSaved: () -> Unit, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val vm: ServiceDetailViewModel = viewModel(factory = ServiceDetailViewModel.factory(context, serviceId))

    val nombre = remember { mutableStateOf("") }
    val descripcion = remember { mutableStateOf("") }
    val duracion = remember { mutableStateOf("") }
    val precio = remember { mutableStateOf("") }
    val editMode = remember { mutableStateOf(false) }

    LaunchedEffect(vm.service) {
        vm.service.value?.let { s ->
            nombre.value = s.name
            descripcion.value = s.description ?: ""
            duracion.value = s.activeDuration().toString()
            precio.value = (s.priceCents / 100).toString()
        }
    }

    val durationOk = duracion.value.toIntOrNull()?.let { it in 5..(12 * 60) } ?: false
    val priceOk = precio.value.toLongOrNull()?.let { it >= 0 } ?: false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de servicio", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás") } }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(value = nombre.value, onValueChange = { if (editMode.value) nombre.value = it }, label = { Text("Nombre del servicio") }, modifier = Modifier.fillMaxWidth(), readOnly = !editMode.value)
            OutlinedTextField(value = descripcion.value, onValueChange = { if (editMode.value) descripcion.value = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), readOnly = !editMode.value)
            OutlinedTextField(value = duracion.value, onValueChange = { if (editMode.value) duracion.value = it.filter { ch -> ch.isDigit() } }, label = { Text("Duración (min)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = editMode.value && !durationOk, readOnly = !editMode.value)
            OutlinedTextField(value = precio.value, onValueChange = { if (editMode.value) precio.value = it.filter { ch -> ch.isDigit() } }, label = { Text("Precio (COP)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = editMode.value && !priceOk, readOnly = !editMode.value)

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { vm.delete(onSaved) }, modifier = Modifier.weight(1f)) { Text("Eliminar") }
                Spacer(modifier = Modifier.height(0.dp).weight(0.1f))
                if (!editMode.value) {
                    Button(onClick = { editMode.value = true }, modifier = Modifier.weight(1f)) { Text("Editar") }
                } else {
                    Button(onClick = {
                        val d = duracion.value.toIntOrNull() ?: 0
                        val p = precio.value.toLongOrNull() ?: -1
                        if (d > 0 && p >= 0) {
                            vm.update(
                                name = nombre.value,
                                description = descripcion.value,
                                durationMinutes = d,
                                pricePesos = p
                            )
                            onSaved()
                        }
                    }, enabled = durationOk && priceOk && nombre.value.isNotBlank(), modifier = Modifier.weight(1f)) { Text("Guardar") }
                }
            }
        }
    }
}

// helper to check numeric range
private fun durationOk(s: String): Boolean {
    val v = s.toIntOrNull() ?: return false
    return v in 5..(12 * 60)
}


