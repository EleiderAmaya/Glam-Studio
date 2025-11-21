package com.glamstudio.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glamstudio.ui.theme.Primary
import com.glamstudio.ui.viewmodel.CalendarViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(onDaySelected: (date: LocalDate) -> Unit, onGenerateInvoice: () -> Unit, onAppointmentClick: (String) -> Unit = {}) {
    var currentDisplayedDate by remember { mutableStateOf(LocalDate.now()) }
    val daysInMonth = currentDisplayedDate.lengthOfMonth()
    val dias = (1..daysInMonth).toList()

    val today = LocalDate.now()
    var selectedDayOfMonth by remember { mutableStateOf(if (today.month == currentDisplayedDate.month && today.year == currentDisplayedDate.year) today.dayOfMonth else 1) }

    val context = LocalContext.current
    val vm: CalendarViewModel = viewModel(factory = CalendarViewModel.factory(context))

    LaunchedEffect(currentDisplayedDate) {
        vm.setMonth(currentDisplayedDate)
        selectedDayOfMonth = if (today.month == currentDisplayedDate.month && today.year == currentDisplayedDate.year) today.dayOfMonth else 1
    }

    val occupancy by vm.occupancyByDay.collectAsState()
    val todayAppointments by vm.appointmentsWithClientForDay(LocalDate.now()).collectAsState(initial = emptyList())

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { currentDisplayedDate = currentDisplayedDate.minusMonths(1) }) { Icon(Icons.Default.ArrowBackIos, contentDescription = "Mes anterior") }
            Text(
                text = "${currentDisplayedDate.month.name} ${currentDisplayedDate.year}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = { currentDisplayedDate = currentDisplayedDate.plusMonths(1) }) { Icon(Icons.Default.ArrowForwardIos, contentDescription = "Mes siguiente") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("D","L","M","M","J","V","S").forEach { d ->
                Text(text = d, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val firstDay = currentDisplayedDate.withDayOfMonth(1)
        val dow = firstDay.dayOfWeek.value // 1=Lunes..7=Domingo
        val leadingEmpty = dow % 7 // con cabecera empezando en Domingo
        val celdas = List(leadingEmpty) { 0 } + dias

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(celdas.size) { index ->
                if (index < leadingEmpty) {
                    Box(modifier = Modifier.height(44.dp)) {}
                } else {
                    val dia = celdas[index]
                    val date = LocalDate.of(currentDisplayedDate.year, currentDisplayedDate.month, dia)
                    val ratio = occupancy[dia] ?: 0f
                    val isPast = date.isBefore(today)
                    val color = when {
                        isPast -> Color(0xFFBDBDBD) // atenuado
                        ratio >= 1f -> Color(0xFFE53935)
                        ratio >= 0.5f -> Color(0xFFFFA726)
                        else -> Color(0xFF43A047)
                    }
                    DiaCell(
                        dia = dia,
                        color = color,
                        seleccionado = dia == selectedDayOfMonth,
                    ) {
                        selectedDayOfMonth = dia
                        onDaySelected(date)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Citas de hoy", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        Surface(shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE6DBE0))) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                if (todayAppointments.isEmpty()) {
                    Text("Sin citas", color = Color.Gray)
                } else {
                    todayAppointments.forEach { appt ->
                        val time = DateTimeFormatter.ofPattern("hh:mm a", Locale("es","ES")).format(
                            java.time.Instant.ofEpochMilli(appt.startEpochMs).atZone(ZoneId.systemDefault()).toLocalTime()
                        )
                        Text("Cita de hoy • ${appt.clientName} • $time", modifier = Modifier.clickable { onAppointmentClick(appt.id) })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onGenerateInvoice, modifier = Modifier.fillMaxWidth()) { Text("Generar factura") }
    }
}

@Composable
private fun DiaCell(
    dia: Int,
    color: Color,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val border = if (seleccionado) BorderStroke(2.dp, Primary) else null

    Surface(
        shape = CircleShape,
        border = border,
        modifier = Modifier
            .padding(2.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
                .background(
                    color.copy(alpha = if (color == Color.Transparent) 0f else 0.15f),
                    CircleShape
                )
        ) {
            if (dia > 0) {
                Text(text = dia.toString(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


