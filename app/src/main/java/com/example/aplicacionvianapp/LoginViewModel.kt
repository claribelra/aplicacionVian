package com.example.aplicacionvianapp

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object SuccessCliente : LoginState()
    object SuccessAdmin : LoginState() // Estado para administradores
    data class SuccessParqueadero(val parqueadero: Parqueadero) : LoginState()
    object SuccessNoCliente : LoginState() // Otros roles
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token
    private val _userId = MutableStateFlow<Int?>(null)
    val userId: StateFlow<Int?> = _userId

    private fun getUserIdFromToken(token: String): Int? {
        return try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
                val jsonObject = JSONObject(payload)
                if (jsonObject.has("user_id")) {
                    return jsonObject.getInt("user_id")
                }
            }
            null
        } catch (e: Exception) {
            Log.e("JWT_DECODE", "Error decoding JWT", e)
            null
        }
    }

    fun login(username: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val credentials = mapOf("username" to username, "password" to password)
                val response = RetrofitClient.instance.login(credentials)
                _token.value = response.access

                // LÓGICA MEJORADA Y DEFINITIVA:
                // 1. Decodificar el token para obtener el user_id
                val loggedInUserId = getUserIdFromToken(response.access)
                if (loggedInUserId == null) {
                    _loginState.value = LoginState.Error("Error: No se pudo leer el ID de usuario desde el token.")
                    return@launch
                }

                // 2. Obtener todos los perfiles
                val authHeader = "Bearer ${response.access}"
                val profiles = RetrofitClient.instance.getProfilesAll(authHeader)

                // 3. Buscar el perfil usando el user_id del token
                val profile = profiles.firstOrNull { it.user == loggedInUserId }

                if (profile != null) {
                    _userId.value = profile.id
                    // 4. Comprobar el rol del perfil encontrado
                    when (profile.rol.lowercase()) {
                        "cliente" -> _loginState.value = LoginState.SuccessCliente
                        "parqueadero" -> {
                            val parqueaderos = RetrofitClient.instance.getParqueaderos()
                            val parqueadero = parqueaderos.firstOrNull { it.id == profile.parqueaderoprivado }
                            if (parqueadero != null) {
                                _loginState.value = LoginState.SuccessParqueadero(parqueadero)
                            } else {
                                _loginState.value = LoginState.Error("No se encontró el parqueadero asociado a este usuario.")
                            }
                        }
                        "admin", "administrador", "staff" -> _loginState.value = LoginState.SuccessAdmin
                        else -> _loginState.value = LoginState.SuccessNoCliente
                    }
                } else {
                    _loginState.value = LoginState.Error("Error: Perfil no encontrado para el usuario autenticado.")
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _loginState.value = LoginState.Error(errorBody ?: e.message ?: "Usuario o contraseña incorrectos")
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Usuario o contraseña incorrectos")
            }
        }
    }
}
