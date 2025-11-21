package com.glamstudio.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glamstudio.ui.theme.BorderLight
import com.glamstudio.ui.theme.Primary
import com.glamstudio.ui.viewmodel.HomeViewModel
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun formatCopFromCents(cents: Long): String {
    val pesos = cents / 100
    val cs = (cents % 100).toInt()
    val nf = NumberFormat.getNumberInstance(Locale("es", "CO"))
    return "${nf.format(pesos)},${cs.toString().padStart(2, '0')}"
}

@Composable
fun HomeScreen(onFabClick: () -> Unit, onReportsClick: () -> Unit = {}, onAppointmentClick: () -> Unit = {}) {
    val context = LocalContext.current
    val vm: HomeViewModel = viewModel(factory = HomeViewModel.factory(context))
    val citasHoy by vm.todayAppointments.collectAsState()
    val citasManana by vm.tomorrowAppointments.collectAsState()
    val confirmed by vm.todayConfirmedIncome.collectAsState()
    val expected by vm.todayExpectedIncome.collectAsState()
    val metrics by vm.monthMetrics.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Glam Studio",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Próximas citas (hoy)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(citasHoy) { cita ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onAppointmentClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.height(48.dp).fillMaxWidth(0.15f)) { drawCircle(color = Primary) }
                        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            val time = DateTimeFormatter.ofPattern("hh:mm a", Locale("es","ES")).format(
                                Instant.ofEpochMilli(cita.startEpochMs).atZone(ZoneId.systemDefault()).toLocalTime()
                            )
                            Text(text = cita.clientName, fontWeight = FontWeight.SemiBold)
                            Text(text = time, color = Color(0xFF896175), fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Próximas citas (mañana)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(citasManana) { cita ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onAppointmentClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.height(48.dp).fillMaxWidth(0.15f)) { drawCircle(color = Primary) }
                        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            val time = DateTimeFormatter.ofPattern("hh:mm a", Locale("es","ES")).format(
                                Instant.ofEpochMilli(cita.startEpochMs).atZone(ZoneId.systemDefault()).toLocalTime()
                            )
                            Text(text = cita.clientName, fontWeight = FontWeight.SemiBold)
                            Text(text = time, color = Color(0xFF896175), fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "KPIs rápidos",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(titulo = "Ingresos hoy (confirmados)", valor = "${formatCopFromCents(confirmed)}", modifier = Modifier.weight(1f))
                val porLlegar = (expected - confirmed).coerceAtLeast(0)
                KpiCard(titulo = "Ingresos por llegar", valor = "${formatCopFromCents(porLlegar)}", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(titulo = "Agendadas (mes)", valor = "${metrics.scheduled}", modifier = Modifier.weight(1f))
                KpiCard(titulo = "Canceladas (mes)", valor = "${metrics.cancelled}", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(titulo = "Ticket prom. (mes)", valor = "${formatCopFromCents(metrics.avgTicketCents)}", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                FloatingActionButton(onClick = onFabClick, containerColor = Primary) {
                    Icon(painterResource(android.R.drawable.ic_input_add), contentDescription = "Agendar cita", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun KpiCard(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
    ) {
        Column(modifier = modifier.padding(12.dp)) {
            Text(text = titulo, color = Color(0xFF896175), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = valor, style = MaterialTheme.typography.headlineSmall)
        }
    }
}


