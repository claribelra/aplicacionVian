package com.example.aplicacionvianapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Data class para representar una única valoración
data class Valoracion(
    val id: Int,
    val rating: Int,
    val comment: String,
    val author: String
)

class ComentariosViewModel : ViewModel() {

    private val _valoraciones = MutableStateFlow<List<Valoracion>>(emptyList())
    val valoraciones: StateFlow<List<Valoracion>> = _valoraciones

    private val apiService: ApiService by lazy {
        RetrofitClient.instance
    }

    fun loadValoraciones(parqueaderoId: String, token: String) {
        viewModelScope.launch {
            try {
                // Asumimos que tienes un endpoint como "getValoraciones" en tu ApiService
                // que devuelve la lista de valoraciones para un parqueadero específico.
                // val valoracionesList = apiService.getValoraciones(parqueaderoId, "Bearer $token")
                // _valoraciones.value = valoracionesList

                // Mientras tanto, usamos datos de ejemplo:
                _valoraciones.value = listOf(
                    Valoracion(1, 5, "Buen servicio", "cliente"),
                    Valoracion(2, 4, "Un poco lleno, pero bien.", "otro_usuario"),
                    Valoracion(3, 5, "Excelente ubicación.", "visitante")
                )

            } catch (e: Exception) {
                Log.e("ComentariosViewModel", "Error al cargar las valoraciones: ", e)
            }
        }
    }
}
