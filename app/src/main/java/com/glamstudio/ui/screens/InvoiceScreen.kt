package com.glamstudio.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import com.glamstudio.ui.viewmodel.InvoiceListViewModel
import java.text.NumberFormat
import java.util.Locale

private fun formatCop(cents: Long): String {
    val pesos = cents / 100
    val cs = (cents % 100).toInt()
    val nf = NumberFormat.getNumberInstance(Locale("es","CO"))
    return "${nf.format(pesos)},${cs.toString().padStart(2, '0')} COP"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(showBack: Boolean = false, onBack: () -> Unit = {}, onViewReports: () -> Unit = {}) {
    val vm: InvoiceListViewModel = viewModel(factory = InvoiceListViewModel.factory(androidx.compose.ui.platform.LocalContext.current))
    val invoices by vm.invoices.collectAsState()

    Scaffold(
        topBar = {
            if (showBack) {
                TopAppBar(
                    title = { Text("Facturas") },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás") }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Listado de facturas", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Mes actual")
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(checked = false, onCheckedChange = { vm.toggleMonthOnly() })
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(invoices) { inv ->
                    Surface(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = formatCop(inv.totalCents))
                                Text(text = inv.status)
                            }
                            if (inv.status != "PAID") {
                                TextButton(onClick = { vm.markPaid(inv.id) }) { Text("Marcar pagada") }
                            }
                            if (inv.status != "VOID") {
                                TextButton(onClick = { vm.void(inv.id) }) { Text("Anular") }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onViewReports, modifier = Modifier.fillMaxWidth()) { Text("Ver reportes") }
        }
    }
}


