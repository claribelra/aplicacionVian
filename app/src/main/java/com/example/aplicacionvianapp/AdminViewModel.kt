package com.example.aplicacionvianapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles

    private val apiService: ApiService by lazy {
        RetrofitClient.instance
    }

    fun loadProfiles(token: String) {
        viewModelScope.launch {
            try {
                val profileList = apiService.getProfilesAll("Bearer $token")
                _profiles.value = profileList
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error al cargar los perfiles: ", e)
            }
        }
    }
}
