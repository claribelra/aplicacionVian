package com.example.aplicacionvianapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object SuccessCliente : LoginState()
    object SuccessNoCliente : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token
    private val _userId = MutableStateFlow<Int?>(null)
    val userId: StateFlow<Int?> = _userId

    fun login(username: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val credentials = mapOf("username" to username, "password" to password)
                val response = RetrofitClient.instance.login(credentials)
                _token.value = response.access
                val authHeader = "Bearer ${response.access}"
                val profiles = RetrofitClient.instance.getProfilesAll(authHeader)
                val profile = profiles.firstOrNull { it.nombres.equals(username, ignoreCase = true) }
                if (profile != null) {
                    _userId.value = profile.id // Actualiza el userId con el id del perfil
                    if (profile.rol == "cliente") {
                        _loginState.value = LoginState.SuccessCliente
                    } else {
                        _loginState.value = LoginState.SuccessNoCliente
                    }
                } else {
                    _loginState.value = LoginState.Error("Usuario o contraseña incorrectos")
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
