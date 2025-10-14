package com.example.aplicacionvianapp

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.DELETE

interface ApiService {
    @GET("parqueaderos/")
    suspend fun getParqueaderos(): List<Parqueadero>

    @GET("perfiles/")
    suspend fun getProfiles(@Query("user") userId: Int, @Header("Authorization") token: String): List<Profile>

    @POST("token/")
    suspend fun login(@Body credentials: Map<String, String>): LoginResponse

    @GET("perfiles/")
    suspend fun getProfilesAll(@Header("Authorization") token: String): List<Profile>

    @GET("reservas/")
    suspend fun getReservas(@Query("user") userId: Int, @Header("Authorization") token: String): List<Reserva>

    @GET("opiniones/")
    suspend fun getOpiniones(@Query("user") userId: Int, @Header("Authorization") token: String): List<Opinion>

    @GET("perfiles/{id}/")
    suspend fun getPerfil(
        @Header("Authorization") token: String,
        @Path("id") perfilId: Int
    ): Profile

    @GET("perfil/")
    suspend fun getPerfilActual(@Header("Authorization") token: String): Profile

    @POST("favoritos/")
    suspend fun agregarFavorito(
        @Header("Authorization") token: String,
        @Body body: FavoritoBody
    ): FavoritoResponse

    @DELETE("favoritos/{id}/")
    suspend fun quitarFavorito(
        @Header("Authorization") token: String,
        @Path("id") parqueaderoId: Int
    ): FavoritoResponse

    @GET("favoritos/")
    suspend fun getFavoritos(
        @Query("usuario") userId: Int,
        @Header("Authorization") token: String
    ): List<FavoritoBody>
}
