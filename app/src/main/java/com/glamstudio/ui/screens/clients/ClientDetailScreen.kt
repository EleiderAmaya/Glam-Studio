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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glamstudio.ui.viewmodel.ClientDetailViewModel

/**
 * Detalle de cliente con carga por ID y edición.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(clientId: String, onSaved: () -> Unit, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val vm: ClientDetailViewModel = viewModel(factory = ClientDetailViewModel.factory(context, clientId))

    val nombre = remember { mutableStateOf("") }
    val telefono = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val direccion = remember { mutableStateOf("") }
    val barrio = remember { mutableStateOf("") }
    val notas = remember { mutableStateOf("") }
    val activo = remember { mutableStateOf(true) }
    val vip = remember { mutableStateOf(false) }
    val editMode = remember { mutableStateOf(false) }

    LaunchedEffect(vm.client) {
        vm.client.value?.let { c ->
            nombre.value = c.fullName
            telefono.value = c.phone
            email.value = c.email ?: ""
            direccion.value = c.address ?: ""
            barrio.value = c.neighborhood ?: ""
            notas.value = c.notes ?: ""
            activo.value = c.isActive
            vip.value = c.isVip
        }
    }

    val phoneIsValid = telefono.value.length in 7..12

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de cliente") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás") } }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(value = nombre.value, onValueChange = { if (editMode.value) nombre.value = it }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth(), enabled = editMode.value)
            OutlinedTextField(
                value = telefono.value,
                onValueChange = { if (editMode.value) telefono.value = it.filter { ch -> ch.isDigit() } },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                isError = editMode.value && telefono.value.isNotBlank() && !phoneIsValid,
                supportingText = { if (editMode.value && telefono.value.isNotBlank() && !phoneIsValid) Text("7–12 dígitos") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = editMode.value
            )
            OutlinedTextField(value = email.value, onValueChange = { if (editMode.value) email.value = it }, label = { Text("Email (opcional)") }, modifier = Modifier.fillMaxWidth(), enabled = editMode.value)
            OutlinedTextField(value = direccion.value, onValueChange = { if (editMode.value) direccion.value = it }, label = { Text("Dirección principal") }, modifier = Modifier.fillMaxWidth(), enabled = editMode.value)
            OutlinedTextField(value = barrio.value, onValueChange = { if (editMode.value) barrio.value = it }, label = { Text("Barrio (opcional)") }, modifier = Modifier.fillMaxWidth(), enabled = editMode.value)
            OutlinedTextField(value = notas.value, onValueChange = { if (editMode.value) notas.value = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), enabled = editMode.value)

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Activo", modifier = Modifier.weight(1f))
                Switch(checked = activo.value, onCheckedChange = { if (editMode.value) activo.value = it }, enabled = editMode.value)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { vm.delete(onSaved) }, modifier = Modifier.weight(1f)) { Text("Eliminar") }
                Spacer(modifier = Modifier.height(0.dp).weight(0.1f))
                if (!editMode.value) {
                    Button(onClick = { editMode.value = true }, modifier = Modifier.weight(1f)) { Text("Editar") }
                } else {
                    Button(onClick = {
                        vm.update(
                            fullName = nombre.value,
                            phone = telefono.value,
                            email = email.value,
                            address = direccion.value,
                            neighborhood = barrio.value,
                            notes = notas.value,
                            isActive = activo.value,
                            isVip = vip.value
                        )
                        onSaved()
                    }, enabled = phoneIsValid && nombre.value.isNotBlank(), modifier = Modifier.weight(1f)) { Text("Guardar") }
                }
            }
        }
    }
}
