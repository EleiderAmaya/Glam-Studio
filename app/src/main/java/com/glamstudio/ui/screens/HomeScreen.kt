package com.glamstudio.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glamstudio.ui.theme.BorderLight
import com.glamstudio.ui.theme.Primary

data class ProximaCita(val nombre: String, val detalle: String)

/**
 * Dashboard inicial con:
 * - Lista compacta de próximas citas.
 * - KPIs rápidos reutilizables mediante `KpiCard`.
 * - FAB para acción primaria (agenda).
 *
 * Reutilización:
 * - Para añadir nuevas secciones, crea composables pequeños (p. ej. `KpiCard`, `ResumenXCard`) y colócalos aquí.
 * - `onFabClick`, `onReportsClick`, `onAppointmentClick` se pasan desde navegación; aquí solo emitimos eventos.
 * - Sustituye la lista demo `citas` por datos reales (ViewModel/StateFlow) sin tocar la navegación.
 */
@Composable
fun HomeScreen(onFabClick: () -> Unit, onReportsClick: () -> Unit = {}, onAppointmentClick: () -> Unit = {}) {
    val citas = listOf(
        ProximaCita("Cliente X", "Corte + Secado • 10:00 AM"),
        ProximaCita("Cliente Y", "Coloración • 11:30 AM"),
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hola, Belinda",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Próximas citas",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(citas) { cita ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onAppointmentClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar placeholder
                        androidx.compose.foundation.Canvas(modifier = Modifier.height(48.dp).fillMaxWidth(0.15f)) {
                            drawCircle(color = Primary)
                        }
                        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(text = cita.nombre, fontWeight = FontWeight.SemiBold)
                            Text(text = cita.detalle, color = Color(0xFF896175), fontSize = 12.sp)
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
                KpiCard(titulo = "Ingresos hoy", valor = "$120.000", modifier = Modifier.weight(1f))
                KpiCard(titulo = "Citas hoy", valor = "2/5", modifier = Modifier.weight(1f))
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

/**
 * Tarjeta simple para KPIs. Reutiliza este patrón para métricas en cualquier pantalla.
 *
 * Ejemplo:
 * ```
 * KpiCard(titulo = "Ticket promedio", valor = "$38.000")
 * ```
 */
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


