package com.glamstudio.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAppointmentScreen(
    date: LocalDate,
    onBack: () -> Unit
) {
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
        ) {
            // Este es el esqueleto de tu pantalla.
            // Más adelante aquí pondrás la lógica para buscar cliente,
            // seleccionar servicio, hora, etc.
            Text("Contenido de la pantalla de agendamiento.")
        }
    }
}