package com.glamstudio.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.glamstudio.data.entity.ClientEntity
import com.glamstudio.ui.theme.BorderLight
import com.glamstudio.ui.theme.Primary
import com.glamstudio.ui.viewmodel.ClientsViewModel

/**
 * Lista de clientes respaldada por Room.
 */
@Composable
fun ClientsScreen(onAddClick: () -> Unit, onItemClick: (ClientEntity) -> Unit = {}) {
    val context = LocalContext.current
    val vm: ClientsViewModel = viewModel(factory = ClientsViewModel.factory(context))

    val query = remember { mutableStateOf("") }
    val clientes by vm.clients.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Clientes",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onAddClick) {
                    Icon(painterResource(android.R.drawable.ic_input_add), contentDescription = "Nuevo cliente")
                }
            }

            OutlinedTextField(
                value = query.value,
                onValueChange = {
                    query.value = it
                    vm.onQueryChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar cliente…") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(clientes) { c ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderLight),
                        onClick = { onItemClick(c) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.height(56.dp).fillMaxWidth(0.15f)) {
                                drawCircle(color = Primary)
                            }
                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(text = c.fullName, fontWeight = FontWeight.SemiBold)
                                if (c.isVip) {
                                    Text(
                                        text = "VIP",
                                        color = Primary,
                                        fontSize = 11.sp,
                                        modifier = Modifier
                                            .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(999.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Icon(imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
