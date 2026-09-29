package com.johnmoraleslo.apptrueques.data.model

// ---------- Respuesta de la API (https://dummyjson.com/products) ----------

data class ProductResponse(
    val products: List<Product>
)

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val thumbnail: String,
    val brand: String?
)

// ---------- Modelo que usa la app ----------

// Un artículo publicado para hacer trueque (puede ser de otra persona o mío)
data class Item(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val imageUrl: String,
    val condition: String,   // "Nuevo" o "Usado"
    val owner: String,
    val wantsInReturn: String // Lo que la persona busca a cambio
)

// La API no tiene dueño ni estado del producto, así que los "inventamos"
// a partir del id para que siempre salgan iguales.
private val owners = listOf("Laura M.", "Carlos R.", "Valentina G.", "Andrés P.", "Sofía T.", "Juan D.")
private val wishes = listOf(
    "Libros o juegos de mesa",
    "Ropa en buen estado",
    "Accesorios de celular",
    "Lo que me ofrezcas",
    "Artículos de cocina",
    "Audífonos o parlantes"
)

fun Product.toItem(): Item {
    return Item(
        id = id,
        title = title,
        description = description,
        category = category,
        imageUrl = thumbnail,
        condition = if (id % 3 == 0) "Nuevo" else "Usado",
        owner = owners[id % owners.size],
        wantsInReturn = wishes[id % wishes.size]
    )
}

// ---------- Ofertas de trueque ----------

enum class OfferStatus(val label: String) {
    PENDIENTE("Pendiente"),
    ACEPTADA("Aceptada"),
    RECHAZADA("Rechazada")
}

data class TradeOffer(
    val id: Int,
    val myItem: Item,        // Lo que yo doy
    val otherItem: Item,     // Lo que recibo
    val otherUser: String,
    val message: String,
    val sentByMe: Boolean,   // true = la envié yo, false = me la enviaron
    val status: OfferStatus = OfferStatus.PENDIENTE
)
