package com.glamstudio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glamstudio.data.entity.ServiceEntity
import com.glamstudio.ui.viewmodel.AppointmentDetailViewModel
import java.text.NumberFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

private fun formatCop(cents: Long): String {
    val pesos = cents / 100
    val cs = (cents % 100).toInt()
    val nf = NumberFormat.getNumberInstance(Locale("es","CO"))
    return "${nf.format(pesos)},${cs.toString().padStart(2, '0')} COP"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(appointmentId: String, onBack: () -> Unit = {}, onInvoiceCreated: (String) -> Unit = {}) {
    val context = LocalContext.current
    val vm: AppointmentDetailViewModel = viewModel(factory = AppointmentDetailViewModel.factory(context, appointmentId))

    val appointment by vm.appointment.collectAsState()
    val clientName by vm.clientName.collectAsState()
    val allServices by vm.allServices.collectAsState()
    val selectedServices by vm.selectedServices.collectAsState()

    var notes by remember { mutableStateOf("") }
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(appointment) {
        notes = appointment?.notes ?: ""
    }

    var selectedStartTime by remember { mutableStateOf<LocalTime?>(null) }
    val timeSlots = remember {
        val slots = mutableListOf<LocalTime>()
        var t = LocalTime.of(10, 0)
        val end = LocalTime.of(18, 0)
        while (t <= end) { slots.add(t); t = t.plusMinutes(30) }
        slots
    }

    var isServiceMenuExpanded by remember { mutableStateOf(false) }
    var isTimeMenuExpanded by remember { mutableStateOf(false) }

    var showExtraDialog by remember { mutableStateOf(false) }
    var extraPesosText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de cita", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás") } }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHost) }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text(text = "Cliente", fontWeight = FontWeight.SemiBold)
            Text(text = clientName)
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = isServiceMenuExpanded, onExpandedChange = { isServiceMenuExpanded = it }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = if (selectedServices.isEmpty()) "Seleccionar Servicios" else "${selectedServices.size} servicios seleccionados",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Servicios") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isServiceMenuExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isServiceMenuExpanded,
                    onDismissRequest = { isServiceMenuExpanded = false },
                ) {
                    allServices.forEach { service ->
                        val checked = selectedServices.any { it.id == service.id }
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = checked, onCheckedChange = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(service.name)
                                }
                            },
                            onClick = {
                                val newSel: List<ServiceEntity> = if (checked) selectedServices.filter { it.id != service.id } else selectedServices + service
                                vm.setSelectedServices(newSel)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = isTimeMenuExpanded, onExpandedChange = { isTimeMenuExpanded = it }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedStartTime?.format(DateTimeFormatter.ofPattern("hh:mm a", Locale("es", "ES")))
                        ?: appointment?.let { LocalTime.ofSecondOfDay((it.startEpochMs / 1000 % 86400 + 86400) % 86400) }?.format(DateTimeFormatter.ofPattern("hh:mm a", Locale("es","ES")))
                        ?: "Seleccionar hora",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora de inicio") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTimeMenuExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isTimeMenuExpanded,
                    onDismissRequest = { isTimeMenuExpanded = false },
                ) {
                    timeSlots.forEach { time ->
                        DropdownMenuItem(
                            text = { Text(time.format(DateTimeFormatter.ofPattern("hh:mm a", Locale("es","ES")))) },
                            onClick = { selectedStartTime = time; isTimeMenuExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.cancel {
                    scope.launch { snackbarHost.showSnackbar("Cita cancelada") }
                    onBack()
                } }, modifier = Modifier.weight(1f)) { Text("Cancelar cita") }
                Button(onClick = {
                    vm.finalize { }
                    vm.generateInvoice(null) { id -> onInvoiceCreated(id) }
                }, modifier = Modifier.weight(1f)) { Text("Finalizar cita") }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val appt = appointment ?: return@Button
                    val start = selectedStartTime ?: LocalTime.ofSecondOfDay((appt.startEpochMs / 1000 % 86400 + 86400) % 86400)
                    val date = java.time.Instant.ofEpochMilli(appt.dateEpochMs).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    vm.reschedule(date, start, notes) { ok ->
                        scope.launch { snackbarHost.showSnackbar(if (ok) "Cambios guardados" else "Horario en conflicto") }
                    }
                }, modifier = Modifier.weight(1f)) { Text("Guardar cambios") }
                Button(onClick = { showExtraDialog = true }, modifier = Modifier.weight(1f)) { Text("Generar factura") }
            }

            if (showExtraDialog) {
                val baseTotal = selectedServices.sumOf { it.priceCents }
                AlertDialog(
                    onDismissRequest = { showExtraDialog = false },
                    title = { Text("Factura: total ${formatCop(baseTotal)} (+ extra opcional)") },
                    text = {
                        OutlinedTextField(
                            value = extraPesosText,
                            onValueChange = { extraPesosText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Extra en pesos (COP)") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val extra = extraPesosText.toLongOrNull()
                            vm.generateInvoice(extra) { id -> showExtraDialog = false; onInvoiceCreated(id) }
                        }) { Text("Crear") }
                    },
                    dismissButton = { TextButton(onClick = { showExtraDialog = false }) { Text("Cancelar") } }
                )
            }
        }
    }
}


