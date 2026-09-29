package com.johnmoraleslo.apptrueques.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.johnmoraleslo.apptrueques.data.model.Item
import com.johnmoraleslo.apptrueques.data.model.OfferStatus
import com.johnmoraleslo.apptrueques.data.model.TradeOffer
import com.johnmoraleslo.apptrueques.ui.components.ItemImage
import com.johnmoraleslo.apptrueques.ui.components.StatusBadge
import com.johnmoraleslo.apptrueques.ui.viewmodel.TradeViewModel

@Composable
fun OffersScreen(tradeViewModel: TradeViewModel) {
    val offers by tradeViewModel.offers.collectAsState()
    var showReceived by remember { mutableStateOf(true) }

    val visibleOffers = offers.filter { it.sentByMe != showReceived }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Mis trueques",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            FilterChip(
                selected = showReceived,
                onClick = { showReceived = true },
                label = { Text("Recibidas") }
            )
            FilterChip(
                selected = !showReceived,
                onClick = { showReceived = false },
                label = { Text("Enviadas") }
            )
        }

        if (visibleOffers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showReceived) {
                        "Nadie te ha ofrecido un trueque todavía"
                    } else {
                        "No has enviado ofertas.\nBusca algo en \"Explorar\" y propón un trueque."
                    },
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = visibleOffers, key = { it.id }) { offer ->
                    OfferCard(
                        offer = offer,
                        onAccept = { tradeViewModel.acceptOffer(offer) },
                        onReject = { tradeViewModel.rejectOffer(offer) },
                        onCancel = { tradeViewModel.cancelOffer(offer) }
                    )
                }
            }
        }
    }
}

@Composable
fun OfferCard(
    offer: TradeOffer,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (offer.sentByMe) "Para: ${offer.otherUser}" else "De: ${offer.otherUser}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(status = offer.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lo que doy  <->  lo que recibo
            Row(verticalAlignment = Alignment.CenterVertically) {
                OfferItem(label = "Das", item = offer.myItem, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Cambio",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                OfferItem(label = "Recibes", item = offer.otherItem, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "\"${offer.message}\"",
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Los botones solo aparecen mientras la oferta está pendiente
            if (offer.status == OfferStatus.PENDIENTE) {
                Spacer(modifier = Modifier.height(12.dp))
                if (offer.sentByMe) {
                    OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                        Text("Cancelar oferta")
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f)) {
                            Text("Rechazar")
                        }
                        Button(onClick = onAccept, modifier = Modifier.weight(1f)) {
                            Text("Aceptar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfferItem(
    label: String,
    item: Item,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        ItemImage(
            imageUrl = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
