// ...existing code...
    init {
        viewModelScope.launch {
            if (token.isBlank()) {
                _error.value = "No se recibió el token de sesión. Inicia sesión nuevamente."
                return@launch
            }
            val authHeader = "Bearer $token"
            try {
                // Ahora obtenemos el perfil del usuario autenticado usando el nuevo endpoint
                val perfilActual = RetrofitClient.instance.getPerfilActual(authHeader)
                _profile.value = perfilActual
                _error.value = null
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
// ...existing code...
