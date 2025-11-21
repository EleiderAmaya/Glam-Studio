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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glamstudio.data.entity.ClientEntity
import com.glamstudio.data.entity.ServiceEntity
import com.glamstudio.ui.viewmodel.ScheduleViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAppointmentScreen(date: LocalDate, onBack: () -> Unit) {
    val context = LocalContext.current
    val vm: ScheduleViewModel = viewModel(factory = ScheduleViewModel.factory(context))
    val scope = rememberCoroutineScope()

    val clients by vm.clients.collectAsState(initial = emptyList())
    val services by vm.services.collectAsState(initial = emptyList())

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    var isClientMenuExpanded by remember { mutableStateOf(false) }
    var selectedClient: ClientEntity? by remember { mutableStateOf(null) }

    var selectedServices by remember { mutableStateOf(setOf<ServiceEntity>()) }
    var isServiceMenuExpanded by remember { mutableStateOf(false) }

    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    val timeSlots = remember {
        val slots = mutableListOf<LocalTime>()
        var time = LocalTime.of(10, 0)
        val endTime = LocalTime.of(18, 0)
        while (time <= endTime) {
            slots.add(time)
            time = time.plusMinutes(30)
        }
        slots
    }

    val isFormComplete = selectedClient != null && selectedServices.isNotEmpty() && startTime != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = isClientMenuExpanded,
                onExpandedChange = { isClientMenuExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedClientText = selectedClient?.fullName ?: "Seleccionar Cliente"
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
                    expanded = isClientMenuExpanded,
                    onDismissRequest = { isClientMenuExpanded = false },
                ) {
                    clients.sortedBy { it.fullName }.forEach { client ->
                        DropdownMenuItem(
                            text = { Text(client.fullName) },
                            onClick = {
                                selectedClient = client
                                isClientMenuExpanded = false
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
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isServiceMenuExpanded,
                    onDismissRequest = { isServiceMenuExpanded = false }
                ) {
                    services.forEach { service ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedServices.contains(service),
                                        onCheckedChange = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = service.name)
                                }
                            },
                            onClick = {
                                selectedServices = if (selectedServices.contains(service)) selectedServices - service else selectedServices + service
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            var isTimeMenuExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = isTimeMenuExpanded,
                onExpandedChange = { isTimeMenuExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = startTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Seleccionar Hora",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora de Inicio") },
                    leadingIcon = { Icon(Icons.Filled.AccessTime, contentDescription = "Selector de hora") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTimeMenuExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isTimeMenuExpanded,
                    onDismissRequest = { isTimeMenuExpanded = false },
                ) {
                    timeSlots.forEach { time ->
                        DropdownMenuItem(
                            text = { Text(time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale("es", "ES")))) },
                            onClick = {
                                startTime = time
                                isTimeMenuExpanded = false
                            }
                        )
                    }
                }
            }
            if (startTime != null) {
                val totalDurationInMinutes = selectedServices.sumOf { it.durationMinutes }.toLong()
                val endTime = startTime!!.plusMinutes(totalDurationInMinutes)

                Text(
                    text = "Hora de fin estimada: ${endTime.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val clientId = selectedClient?.id
                    val start = startTime
                    if (clientId != null && start != null && selectedServices.isNotEmpty()) {
                        scope.launch {
                            val ok = vm.saveAppointment(
                                clientId = clientId,
                                date = date,
                                startTime = start,
                                selectedServices = selectedServices.toList()
                            )
                            if (ok) {
                                onBack()
                            } else {
                                snackbarHostState.showSnackbar("Ya existe una cita en ese horario")
                            }
                        }
                    }
                },
                enabled = isFormComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Confirmar Cita", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
