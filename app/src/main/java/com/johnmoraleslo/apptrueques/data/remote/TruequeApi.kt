package com.johnmoraleslo.apptrueques.data.remote

import com.johnmoraleslo.apptrueques.data.model.Product
import com.johnmoraleslo.apptrueques.data.model.ProductResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TruequeApi {
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 60
    ): ProductResponse

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): ProductResponse

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ): Product
}

object RetrofitClient {
    // Retrofit exige que la URL base termine en "/"
    private const val BASE_URL = "https://dummyjson.com/"

    val api: TruequeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TruequeApi::class.java)
    }
}
