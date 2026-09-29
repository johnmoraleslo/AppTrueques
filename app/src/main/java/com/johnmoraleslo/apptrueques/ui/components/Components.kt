package com.johnmoraleslo.apptrueques.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.johnmoraleslo.apptrueques.data.model.OfferStatus
import com.johnmoraleslo.apptrueques.ui.theme.AcceptedColor
import com.johnmoraleslo.apptrueques.ui.theme.NewColor
import com.johnmoraleslo.apptrueques.ui.theme.PendingColor
import com.johnmoraleslo.apptrueques.ui.theme.RejectedColor
import com.johnmoraleslo.apptrueques.ui.theme.UsedColor

// Muestra la imagen del artículo o un ícono si no tiene imagen
@Composable
fun ItemImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    if (imageUrl.isBlank()) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(40.dp)
            )
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier.background(Color.White)
        )
    }
}

// Etiqueta pequeña de color, ej: "Nuevo", "Usado", "Pendiente"
@Composable
fun Badge(text: String, color: Color) {
    Text(
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
fun ConditionBadge(condition: String) {
    val color = if (condition == "Nuevo") NewColor else UsedColor
    Badge(text = condition, color = color)
}

@Composable
fun StatusBadge(status: OfferStatus) {
    val color = when (status) {
        OfferStatus.PENDIENTE -> PendingColor
        OfferStatus.ACEPTADA -> AcceptedColor
        OfferStatus.RECHAZADA -> RejectedColor
    }
    Badge(text = status.label, color = color)
}
