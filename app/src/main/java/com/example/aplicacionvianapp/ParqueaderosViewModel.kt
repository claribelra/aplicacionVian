package com.example.aplicacionvianapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ParqueaderosViewModel : ViewModel() {
    private val _parqueaderos = MutableStateFlow<List<Parqueadero>>(emptyList())
    val parqueaderos: StateFlow<List<Parqueadero>> = _parqueaderos

    private val _favoritos = MutableStateFlow<Set<Int>>(emptySet())
    val favoritos: StateFlow<Set<Int>> = _favoritos

    // 1. Centralizar la instancia de ApiService
    private val apiService: ApiService by lazy {
        RetrofitClient.instance
    }

    // 2. Refactorizar la función de carga
    fun cargarParqueaderos() {
        // Solo carga la lista si está vacía para evitar llamadas de red innecesarias.
        if (_parqueaderos.value.isEmpty()) {
            viewModelScope.launch {
                try {
                    val listaParqueaderos = apiService.getParqueaderos()
                    _parqueaderos.value = listaParqueaderos
                } catch (e: Exception) {
                    Log.e("ParqueaderosViewModel", "Error al cargar parqueaderos: ${e.message}")
                }
            }
        }
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

    fun getParqueaderoById(parqueaderoId: String): Flow<Parqueadero?> {
        val id = parqueaderoId.toIntOrNull()
        return parqueaderos.map { listaDeParqueaderos ->
            listaDeParqueaderos.firstOrNull { it.id == id }
        }
    }

    fun actualizarParqueadero(
        token: String,
        parqueaderoId: String,
        datosActualizados: Map<String, String>
    ) {
        viewModelScope.launch {
            try {
                val parqueaderoActualizado = apiService.updateParqueadero(
                    token = "Bearer $token",
                    parqueaderoId = parqueaderoId,
                    body = datosActualizados
                )
                _parqueaderos.update { listaActual ->
                    listaActual.map {
                        if (it.id.toString() == parqueaderoId) parqueaderoActualizado else it
                    }
                }
            } catch (e: Exception) {
                Log.e("ParqueaderosViewModel", "Error al actualizar el parqueadero: ${e.message}")
            }
        }
    }

    fun agregarFavoritoEnBackend(userId: Int, parqueaderoId: Int, token: String) {
        viewModelScope.launch {
            try {
                val body = FavoritoBody(usuario = userId, parqueadero = parqueaderoId)
                val response = apiService.agregarFavorito("Bearer $token", body)
                _favoritos.update { favs -> favs + parqueaderoId }
            } catch (e: Exception) {
                if (e is retrofit2.HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e("Favoritos", "Error backend: $errorBody")
                }
            }
        }
    }

    fun cargarFavoritosDesdeBackend(userId: Int, token: String) {
        viewModelScope.launch {
            try {
                val favoritosBackend = apiService.getFavoritos(userId, "Bearer $token")
                val idsFavoritos = favoritosBackend.map { it.parqueadero }.toSet()
                _favoritos.value = idsFavoritos
            } catch (e: Exception) {
                Log.e("Favoritos", "Error al cargar favoritos: ${e.message}")
            }
        }
    }
}
