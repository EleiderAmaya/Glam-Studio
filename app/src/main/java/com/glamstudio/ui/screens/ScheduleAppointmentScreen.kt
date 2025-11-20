package com.glamstudio.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
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
    val allClients = listOf("Eleider", "Ana García", "Carlos Pérez", "Sofía López", "David Martínez") // lista de clientes Simulada
    val sortedClients = allClients.sorted() // Ordenarla alfabéticamente
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

            // --- Pieza 1: Selector de Cliente ---
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
                    modifier = Modifier.menuAnchor().fillMaxWidth()
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
        }
    }
}
