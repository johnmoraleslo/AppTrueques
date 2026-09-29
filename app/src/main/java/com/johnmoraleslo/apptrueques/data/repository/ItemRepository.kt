package com.johnmoraleslo.apptrueques.data.repository

import com.johnmoraleslo.apptrueques.data.model.Item
import com.johnmoraleslo.apptrueques.data.model.toItem
import com.johnmoraleslo.apptrueques.data.remote.RetrofitClient

// Trae de internet los artículos que publican otras personas
class ItemRepository {
    private val api = RetrofitClient.api

    suspend fun getItems(query: String? = null): Result<List<Item>> {
        return try {
            val response = if (query.isNullOrBlank()) {
                api.getProducts()
            } else {
                api.searchProducts(query)
            }
            Result.success(response.products.map { it.toItem() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItemById(id: Int): Result<Item> {
        return try {
            Result.success(api.getProductById(id).toItem())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
