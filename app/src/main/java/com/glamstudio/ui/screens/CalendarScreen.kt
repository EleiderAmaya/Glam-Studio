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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.glamstudio.ui.theme.Primary
import java.time.LocalDate

enum class Ocupacion { LIBRE, MEDIO, COMPLETO }

/**
 * Calendario mensual simplificado con selección de semana y lista de citas del día.
 *
 * Reutilización:
 * - Reemplaza `dias/ocupacionDemo` por tu modelo real (p. ej. generado desde un repositorio).
 * - Usa `onGenerateInvoice` para disparar el flujo de facturación desde una cita.
 * - Usa `onAppointmentClick` para navegar a detalle de cita.
 *
 * Extensión:
 * - Para parámetros (mes/año), expón `@Composable fun CalendarScreen(year: Int, month: Int, ...)`.
 * - Para disponibilidad por día, mapea `Ocupacion` según tus reglas de negocio.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(onGenerateInvoice: () -> Unit, onAppointmentClick: () -> Unit = {}) {
    /* esta variable que guarda el estado de la fecha actual seleccionada */
    var currentDisplayedDate by remember { mutableStateOf(LocalDate.now()) }
    // Demo: 31 días, con algunos ocupados/medios
    val dias = (1..31).toList()
    val ocupacionDemo = remember {
        mapOf(
            5 to Ocupacion.COMPLETO,
            9 to Ocupacion.MEDIO,
            12 to Ocupacion.LIBRE,
            15 to Ocupacion.COMPLETO,
            18 to Ocupacion.MEDIO,
            22 to Ocupacion.LIBRE,
            27 to Ocupacion.MEDIO
        )
    }
    val semanaSeleccionada = remember { mutableStateOf(2) } // 0-index, resalta una semana de ejemplo

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically/*, modifier = Modifier.fillMaxWidth()*/) {
            IconButton(onClick = {
                // La lógica para cambiar de mes hacia atras.
                currentDisplayedDate = currentDisplayedDate.minusMonths(1)
            }) {
                Icon(Icons.Default.ArrowBackIos, contentDescription = "Mes anterior")
            }
            Text(
                text = "${currentDisplayedDate.month.name} ${currentDisplayedDate.year}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = {
                // La lógica para cambiar de mes hacia adelante.
                currentDisplayedDate = currentDisplayedDate.plusMonths(1)
            }) {
                Icon(Icons.Default.ArrowForwardIos, contentDescription = "Mes siguiente")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("D","L","M","M","J","V","S").forEach { d ->
                Text(text = d, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Grilla mensual 7 columnas; la primera fila deja 3 espacios (ejemplo)
        val leadingEmpty = 3
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
                    val fila = index / 7
                    val seleccionado = fila == semanaSeleccionada.value
                    DiaCell(dia = dia, ocupacion = ocupacionDemo[dia], seleccionado = seleccionado) {
                        // Al pulsar un día, mover la selección a su semana
                        semanaSeleccionada.value = index / 7
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
                Text("10:00 AM – Corte de cabello • Cliente X", modifier = Modifier.clickable { onAppointmentClick() })
                Text("13:00 PM – Manicura • Cliente Y", modifier = Modifier.clickable { onAppointmentClick() })
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onGenerateInvoice, modifier = Modifier.fillMaxWidth()) { Text("Generar factura") }
    }
}

@Composable
private fun DiaCell(
    dia: Int,
    ocupacion: Ocupacion?,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val color = when (ocupacion) {
        Ocupacion.COMPLETO -> Color(0xFFE53935) // rojo
        Ocupacion.MEDIO -> Color(0xFFFFA726) // naranja
        Ocupacion.LIBRE -> Color(0xFF43A047) // verde
        null -> Color.Transparent
    }
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
                ) // Indicador de ocupación
        ) {
            if (dia > 0) {
                Text(text = dia.toString(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


