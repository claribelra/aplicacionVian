package com.example.aplicacionvianapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.example.aplicacionvianapp.ui.theme.AplicacionvianappTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicacionvianapp.ui.screens.HomeScreen
import com.example.aplicacionvianapp.ui.screens.OpenStreetMapScreen
import com.example.aplicacionvianapp.ui.screens.RegistroScreen
import com.example.aplicacionvianapp.ui.screens.BienvenidaScreen
import com.example.aplicacionvianapp.ui.screens.OpinionesScreen
import com.example.aplicacionvianapp.ui.screens.LoginScreen
import com.example.aplicacionvianapp.ui.screens.FavoritosScreen
import com.example.aplicacionvianapp.ui.screens.PerfilScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelStoreOwner
import retrofit2.Retrofit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicacionvianappTheme {
                val navController = rememberNavController()
                val viewModelStoreOwner = this@MainActivity
                val loginViewModel: LoginViewModel = viewModel(viewModelStoreOwner)
                val tokenState = loginViewModel.token.collectAsState()
                val token = tokenState.value ?: ""
                val userIdState = loginViewModel.userId.collectAsState()
                val userId = userIdState.value ?: 0
                val startDestination = if (token.isNotBlank()) "home" else "login"
                val parqueaderosViewModel: ParqueaderosViewModel = viewModel(viewModelStoreOwner)
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://TU_BACKEND_URL/") // Reemplaza por la URL real
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build()
                val apiService = retrofit.create(ApiService::class.java)
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("home") {
                            HomeScreen(navController, viewModelStoreOwner)
                        }
                        composable("openstreetmap") {
                            OpenStreetMapScreen(
                                navController = navController,
                                parqueaderosViewModel = parqueaderosViewModel,
                                token = token
                            )
                        }
                        composable("bienvenida") {
                            BienvenidaScreen()
                        }
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = { navController.navigate("home") },
                                viewModelStoreOwner = viewModelStoreOwner
                            )
                        }
                        composable("favoritos") {
                            FavoritosScreen(
                                parqueaderosViewModel = parqueaderosViewModel,
                                userId = userId,
                                token = token,
                                apiService = apiService
                            )
                        }
                        composable("perfil") {
                            PerfilScreen(token, onEditPerfil = { /* acción de editar perfil */ })
                        }
                        composable("opiniones") {
                            OpinionesScreen()
                        }
                    }
                }
            }
        }
    }
}
