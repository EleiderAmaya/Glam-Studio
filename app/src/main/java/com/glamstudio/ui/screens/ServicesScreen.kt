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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glamstudio.data.entity.ServiceEntity
import com.glamstudio.ui.theme.BorderLight
import com.glamstudio.ui.viewmodel.ServicesViewModel

/**
 * Lista de servicios respaldada por Room.
 */
@Composable
fun ServicesScreen(onAddClick: () -> Unit, onItemClick: (ServiceEntity) -> Unit = {}) {
    val context = LocalContext.current
    val vm: ServicesViewModel = viewModel(factory = ServicesViewModel.factory(context))

    val query = remember { mutableStateOf("") }
    val servicios by vm.services.collectAsState()

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
            onValueChange = {
                query.value = it
                vm.onQueryChange(it)
            },
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
                            Text(text = s.name, fontWeight = FontWeight.Medium)
                            Text(text = "${'$'}{s.durationMinutes} min", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(text = "${'$'}${'$'}{s.priceCents / 100} .${'$'}{(s.priceCents % 100).toString().padStart(2, '0')} COP", fontWeight = FontWeight.Medium)
                }
                }
            }
        }
    }
}


