package com.example.aplicacionvianapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.aplicacionvianapp.Reserva
import com.example.aplicacionvianapp.Opinion

class UserViewModel(private val token: String) : ViewModel() {
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile
    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas
    private val _opiniones = MutableStateFlow<List<Opinion>>(emptyList())
    val opiniones: StateFlow<List<Opinion>> = _opiniones
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            if (token.isBlank()) {
                _error.value = "No se recibió el token de sesión. Inicia sesión nuevamente."
                return@launch
            }
            val authHeader = "Bearer $token"
            try {
                // Obtener el perfil del usuario autenticado
                val perfiles = RetrofitClient.instance.getProfiles(-1, authHeader) // Usa el userId si lo tienes
                if (perfiles.isNotEmpty()) {
                    val perfilId = perfiles.first().id
                    val perfil = RetrofitClient.instance.getPerfil(authHeader, perfilId)
                    _profile.value = perfil
                    _error.value = null
                } else {
                    _profile.value = null
                    _error.value = "No se encontró perfil para el usuario."
                    return@launch
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error al consultar perfil: ${e.message}")
                _profile.value = null
                _error.value = "No se pudo cargar el perfil. Verifica tu conexión o inicia sesión nuevamente."
                return@launch
            }
            try {
                val reservasUsuario = RetrofitClient.instance.getReservas(_profile.value?.user ?: -1, authHeader)
                _reservas.value = reservasUsuario
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error al consultar reservas: ${e.message}")
                _reservas.value = emptyList()
            }
            try {
                val opinionesUsuario = RetrofitClient.instance.getOpiniones(_profile.value?.user ?: -1, authHeader)
                _opiniones.value = opinionesUsuario
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error al consultar opiniones: ${e.message}")
                _opiniones.value = emptyList()
            }
        }
    }
}
