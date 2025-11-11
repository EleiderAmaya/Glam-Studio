package com.glamstudio.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(showBack: Boolean = false, onBack: () -> Unit = {}, onViewReports: () -> Unit = {}) {
    val corte = remember { mutableStateOf(true) }
    val coloracion = remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            if (showBack) {
                TopAppBar(
                    title = { Text("Facturación") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text("Factura", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))

            Text("Servicios realizados", style = MaterialTheme.typography.titleMedium)
            Surface(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    RowCheckPrecio("Corte + Secado", "$35.000", checked = corte.value) { corte.value = it }
                    RowCheckPrecio("Coloración Raíz", "$80.000", checked = coloracion.value) { coloracion.value = it }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Surface(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("Subtotal: $115.000 COP")
                    Text("Total a pagar: $115.000 COP", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { /* generar */ }, modifier = Modifier.fillMaxWidth()) { Text("Generar factura") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* marcar pagada */ }, modifier = Modifier.fillMaxWidth()) { Text("Marcar como pagada") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* anular */ }, modifier = Modifier.fillMaxWidth()) { Text("Anular factura") }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onViewReports, modifier = Modifier.fillMaxWidth()) { Text("Ver reportes") }
        }
    }
}

@Composable
private fun RowCheckPrecio(titulo: String, precio: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(text = titulo, modifier = Modifier.weight(1f))
        Text(text = precio, modifier = Modifier.padding(end = 8.dp))
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}


