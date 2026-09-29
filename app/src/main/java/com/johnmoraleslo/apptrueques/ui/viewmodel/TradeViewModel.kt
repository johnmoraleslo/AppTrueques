package com.johnmoraleslo.apptrueques.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.johnmoraleslo.apptrueques.data.model.Item
import com.johnmoraleslo.apptrueques.data.model.OfferStatus
import com.johnmoraleslo.apptrueques.data.model.TradeOffer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Maneja mis artículos y las ofertas de trueque.
// Los datos se guardan en memoria, así que se reinician al cerrar la app.
class TradeViewModel : ViewModel() {

    private val phoneItem = Item(
        id = 1001,
        title = "iPhone 5s",
        description = "Celular en buen estado, tiene un rayón pequeño en la parte de atrás. Incluye cargador.",
        category = "smartphones",
        imageUrl = "https://cdn.dummyjson.com/product-images/smartphones/iphone-5s/thumbnail.webp",
        condition = "Usado",
        owner = "Yo",
        wantsInReturn = "Audífonos o una tablet"
    )

    private val bookItem = Item(
        id = 1002,
        title = "Libro Cien años de soledad",
        description = "Edición de bolsillo, lo leí una sola vez.",
        category = "libros",
        imageUrl = "",
        condition = "Usado",
        owner = "Yo",
        wantsInReturn = "Otro libro"
    )

    private val _myItems = MutableStateFlow(listOf(phoneItem, bookItem))
    val myItems: StateFlow<List<Item>> = _myItems

    // Una oferta de ejemplo para que la pestaña "Recibidas" no empiece vacía
    private val _offers = MutableStateFlow(
        listOf(
            TradeOffer(
                id = 1,
                myItem = phoneItem,
                otherItem = Item(
                    id = 101,
                    title = "Apple AirPods Max Silver",
                    description = "Audífonos inalámbricos",
                    category = "mobile-accessories",
                    imageUrl = "https://cdn.dummyjson.com/product-images/mobile-accessories/apple-airpods-max-silver/thumbnail.webp",
                    condition = "Usado",
                    owner = "Juan D.",
                    wantsInReturn = "Un celular"
                ),
                otherUser = "Juan D.",
                message = "Hola, te cambio mis audífonos por tu celular, ¿te sirve?",
                sentByMe = false
            )
        )
    )
    val offers: StateFlow<List<TradeOffer>> = _offers

    private var nextItemId = 1003
    private var nextOfferId = 2

    fun addItem(
        title: String,
        description: String,
        category: String,
        condition: String,
        wantsInReturn: String,
        imageUrl: String
    ) {
        val newItem = Item(
            id = nextItemId++,
            title = title.trim(),
            description = description.trim(),
            category = category.trim().ifBlank { "otros" },
            imageUrl = imageUrl.trim(),
            condition = condition,
            owner = "Yo",
            wantsInReturn = wantsInReturn.trim().ifBlank { "Lo que me ofrezcas" }
        )
        _myItems.value = _myItems.value + newItem
    }

    fun deleteItem(item: Item) {
        _myItems.value = _myItems.value.filter { it.id != item.id }
        // Si borro el artículo, se borran las ofertas pendientes donde aparecía
        _offers.value = _offers.value.filter {
            !(it.myItem.id == item.id && it.status == OfferStatus.PENDIENTE)
        }
    }

    fun sendOffer(myItem: Item, otherItem: Item, message: String) {
        val offer = TradeOffer(
            id = nextOfferId++,
            myItem = myItem,
            otherItem = otherItem,
            otherUser = otherItem.owner,
            message = message.trim().ifBlank { "¡Hola! ¿Te interesa un trueque?" },
            sentByMe = true
        )
        _offers.value = listOf(offer) + _offers.value
    }

    fun cancelOffer(offer: TradeOffer) {
        _offers.value = _offers.value.filter { it.id != offer.id }
    }

    fun acceptOffer(offer: TradeOffer) {
        // Mi artículo ya se cambió, así que sale de "Mis cosas"
        _myItems.value = _myItems.value.filter { it.id != offer.myItem.id }

        _offers.value = _offers.value.map {
            when {
                it.id == offer.id -> it.copy(status = OfferStatus.ACEPTADA)
                // Las demás ofertas pendientes por ese mismo artículo se rechazan
                it.myItem.id == offer.myItem.id && it.status == OfferStatus.PENDIENTE ->
                    it.copy(status = OfferStatus.RECHAZADA)
                else -> it
            }
        }
    }

    fun rejectOffer(offer: TradeOffer) {
        _offers.value = _offers.value.map {
            if (it.id == offer.id) it.copy(status = OfferStatus.RECHAZADA) else it
        }
    }
}
