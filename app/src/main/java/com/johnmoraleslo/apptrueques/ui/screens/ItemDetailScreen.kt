package com.johnmoraleslo.apptrueques.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.johnmoraleslo.apptrueques.data.model.Item
import com.johnmoraleslo.apptrueques.data.repository.ItemRepository
import com.johnmoraleslo.apptrueques.ui.components.ConditionBadge
import com.johnmoraleslo.apptrueques.ui.components.ItemImage
import com.johnmoraleslo.apptrueques.ui.viewmodel.TradeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: Int,
    tradeViewModel: TradeViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val myItems by tradeViewModel.myItems.collectAsState()

    var item by remember { mutableStateOf<Item?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showOfferDialog by remember { mutableStateOf(false) }

    LaunchedEffect(itemId) {
        ItemRepository().getItemById(itemId)
            .onSuccess { item = it }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del artículo") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        val currentItem = item

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (currentItem == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No se pudo cargar el artículo")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                ItemImage(
                    imageUrl = currentItem.imageUrl,
                    contentDescription = currentItem.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentItem.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ConditionBadge(condition = currentItem.condition)
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DetailRow(label = "Publicado por:", value = currentItem.owner)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            DetailRow(label = "Categoría:", value = currentItem.category)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            DetailRow(label = "Busca a cambio:", value = currentItem.wantsInReturn)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = currentItem.description)

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showOfferDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Proponer trueque")
                    }
                }
            }

            if (showOfferDialog) {
                OfferDialog(
                    myItems = myItems,
                    onDismiss = { showOfferDialog = false },
                    onConfirm = { selectedItem, message ->
                        tradeViewModel.sendOffer(selectedItem, currentItem, message)
                        showOfferDialog = false
                        Toast.makeText(context, "¡Oferta enviada a ${currentItem.owner}!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// Ventana para escoger cuál de mis artículos quiero ofrecer
@Composable
fun OfferDialog(
    myItems: List<Item>,
    onDismiss: () -> Unit,
    onConfirm: (Item, String) -> Unit
) {
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var message by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Qué ofreces a cambio?") },
        text = {
            if (myItems.isEmpty()) {
                Text("Todavía no tienes artículos. Publica uno en la pestaña \"Mis cosas\".")
            } else {
                Column {
                    myItems.forEach { myItem ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedItem == myItem,
                                    onClick = { selectedItem = myItem }
                                )
                        ) {
                            RadioButton(
                                selected = selectedItem == myItem,
                                onClick = { selectedItem = myItem }
                            )
                            Text(text = myItem.title)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Mensaje (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selectedItem?.let { onConfirm(it, message) } },
                enabled = selectedItem != null
            ) {
                Text("Enviar oferta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
