package com.example.aplicacionvianapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 1. Definir el DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// 2. Crear el Repositorio que maneja el DataStore
class ThemeRepository(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    val isDarkMode: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODE] ?: false // Devuelve false si no hay nada guardado
        }
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit {
            it[IS_DARK_MODE] = isDark
        }
    }
}

// 3. Crear el ViewModel que usa el Repositorio
class ThemeViewModel(private val repository: ThemeRepository) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = repository.isDarkMode

    fun toggleTheme() {
        viewModelScope.launch {
            val currentMode = isDarkMode.value
            repository.setDarkMode(!currentMode)
        }
    }
}

// 4. Crear un Factory para poder inyectar el Repositorio en el ViewModel
class ThemeViewModelFactory(private val repository: ThemeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThemeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}