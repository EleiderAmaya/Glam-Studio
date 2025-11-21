package com.glamstudio.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glamstudio.ui.viewmodel.InvoiceDetailViewModel
import java.text.NumberFormat
import java.util.Locale

private fun formatCop(cents: Long): String {
    val pesos = cents / 100
    val cs = (cents % 100).toInt()
    val nf = NumberFormat.getNumberInstance(Locale("es","CO"))
    return "${nf.format(pesos)},${cs.toString().padStart(2, '0')} COP"
}

private fun statusEs(status: String): String = when (status) {
    "PAID" -> "Pagada"
    "VOID" -> "Anulada"
    "ISSUED" -> "Emitida"
    else -> status
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(invoiceId: String, onBack: () -> Unit = {}) {
    val vm: InvoiceDetailViewModel = viewModel(factory = InvoiceDetailViewModel.factory(androidx.compose.ui.platform.LocalContext.current, invoiceId))
    val invoice by vm.invoice.collectAsState()
    val items by vm.items.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de factura", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás") } }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text = invoice?.let { statusEs(it.status) } ?: "", modifier = Modifier.weight(1f))
                if (invoice != null && invoice!!.status != "PAID") {
                    TextButton(onClick = { vm.markPaid() }) { Text("Marcar pagada") }
                }
                if (invoice != null && invoice!!.status != "VOID") {
                    TextButton(onClick = { vm.void() }) { Text("Anular") }
                }
            }
            Text(text = invoice?.let { formatCop(it.totalCents) } ?: "")
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(items) { it ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = it.description ?: "Item", modifier = Modifier.weight(1f))
                        Text(text = formatCop(it.unitPriceCents))
                    }
                }
            }
        }
    }
}
