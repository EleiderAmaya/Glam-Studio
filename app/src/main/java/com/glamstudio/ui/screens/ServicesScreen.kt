package com.glamstudio.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.glamstudio.ui.theme.BorderLight

data class Servicio(val nombre: String, val duracion: String, val precio: String)

@Composable
fun ServicesScreen(onAddClick: () -> Unit, onItemClick: (Servicio) -> Unit = {}) {
    val query = remember { mutableStateOf("") }
    val servicios = listOf(
        Servicio("Corte + Secado", "45 min", "$35.000"),
        Servicio("Coloración Raíz", "90 min", "$80.000"),
        Servicio("Keratina", "180 min", "$250.000"),
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Servicios",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onAddClick) {
                Icon(painterResource(android.R.drawable.ic_input_add), contentDescription = "Nuevo servicio")
            }
        }

        OutlinedTextField(
            value = query.value,
            onValueChange = { query.value = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar servicio…") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(servicios) { s ->
                Surface(
                    border = BorderStroke(1.dp, BorderLight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    onClick = { onItemClick(s) }
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = s.nombre, fontWeight = FontWeight.Medium)
                            Text(text = s.duracion, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(text = s.precio, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}


