package com.example.aplicacionvianapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ParqueaderosViewModel : ViewModel() {
    private val _parqueaderos = MutableStateFlow<List<Parqueadero>>(emptyList())
    val parqueaderos: StateFlow<List<Parqueadero>> = _parqueaderos

    private val _favoritos = MutableStateFlow<Set<Int>>(emptySet())
    val favoritos: StateFlow<Set<Int>> = _favoritos

    fun setParqueaderos(lista: List<Parqueadero>) {
        _parqueaderos.value = lista
    }

    fun toggleFavorito(parqueaderoId: Int) {
        _favoritos.update { favs ->
            if (favs.contains(parqueaderoId)) favs - parqueaderoId else favs + parqueaderoId
        }
    }

    fun esFavorito(parqueaderoId: Int): Boolean {
        return _favoritos.value.contains(parqueaderoId)
    }

    fun getFavoritos(): List<Parqueadero> {
        return _parqueaderos.value.filter { _favoritos.value.contains(it.id) }
    }

    suspend fun agregarFavoritoEnBackend(parqueaderoId: Int, token: String, apiService: ApiService) {
        try {
            val body = FavoritoBody(usuario = 15, parqueadero = parqueaderoId)
            val response = apiService.agregarFavorito(token, body)
            // Si la respuesta es exitosa, actualiza favoritos
            _favoritos.update { favs -> favs + parqueaderoId }
        } catch (e: Exception) {
            // Manejo de error (puedes mostrar un mensaje en la UI)
            if (e is retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                android.util.Log.e("Favoritos", "Error backend: $errorBody")
            }
        }
    }

    suspend fun cargarFavoritosDesdeBackend(userId: Int, token: String, apiService: ApiService) {
        try {
            val favoritosBackend = apiService.getFavoritos(userId, token)
            val idsFavoritos = favoritosBackend.map { it.parqueadero }.toSet()
            _favoritos.value = idsFavoritos
        } catch (e: Exception) {
            // Manejo de error (puedes mostrar un mensaje en la UI)
            android.util.Log.e("Favoritos", "Error al cargar favoritos: ${e.message}")
        }
    }
}
