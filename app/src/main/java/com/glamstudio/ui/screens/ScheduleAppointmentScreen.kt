package com.glamstudio.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAppointmentScreen(date: LocalDate, onBack: () -> Unit) {
    var isClientMenuExpanded by remember { mutableStateOf(false) }
    var selectedClientText by remember { mutableStateOf("Seleccionar Cliente") }
    val allClients = listOf("Eleider", "Ana García", "Carlos Pérez", "Sandra Rodríguez", "David Martínez") // lista de clientes Simulada
    val sortedClients = allClients.sorted() // Ordenarla alfabéticamente
    var selectedServices by remember { mutableStateOf(setOf<Service>()) }
    val allServices = listOf(
        Service(id = "1", name = "Manicura", description = "Cuidado completo de uñas y manos.", durationInMinutes = 30, price = 25000.0),
        Service(id = "2", name = "Pedicura", description = "Cuidado completo de uñas y pies.", durationInMinutes = 45, price = 35000.0),
        Service(id = "3", name = "Corte de Pelo", description = "Corte y peinado.", durationInMinutes = 60, price = 40000.0)
    )
    var isServiceMenuExpanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Formateamos la fecha para que se vea bonita en español
                    val formatter =
                        DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
                    Text(text = "Agendar para ${date.format(formatter)}")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Cliente
            ExposedDropdownMenuBox(
                expanded = isClientMenuExpanded,
                onExpandedChange = { isClientMenuExpanded = it }, // Él gestiona la expansión/cierre
                modifier = Modifier.fillMaxWidth()
            ) {
                // Este OutlinedTextField se ve como un campo de texto, pero no se puede editar.
                // Solo responde a los clics.
                OutlinedTextField(
                    value = selectedClientText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cliente") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isClientMenuExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isClientMenuExpanded, // El menú se muestra si esto es 'true'
                    onDismissRequest = { isClientMenuExpanded = false }, // Se oculta si el usuario pulsa fuera
                ) {
                    sortedClients.forEach { client ->
                        DropdownMenuItem(
                            text = { Text(client) },
                            onClick = {
                                selectedClientText = client // Actualiza el texto del campo
                                isClientMenuExpanded = false // Cierra el menú
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = isServiceMenuExpanded,
                onExpandedChange = { isServiceMenuExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (selectedServices.isEmpty()) "Seleccionar Servicios" else "${selectedServices.size} servicios seleccionados",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Servicios") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isServiceMenuExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isServiceMenuExpanded,
                    onDismissRequest = { isServiceMenuExpanded = false } // Se cierra al pulsar fuera
                ) {
                    allServices.forEach { service ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedServices.contains(service),
                                        onCheckedChange = null // La lógica va en el onClick principal
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = service.name)
                                }
                            },
                            onClick = {
                                if (selectedServices.contains(service)) {
                                    selectedServices = selectedServices - service
                                } else {
                                    selectedServices = selectedServices + service
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
