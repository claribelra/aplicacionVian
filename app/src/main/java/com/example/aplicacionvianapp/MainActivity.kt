package com.example.aplicacionvianapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aplicacionvianapp.ui.screens.*
import com.example.aplicacionvianapp.ui.theme.AplicacionvianappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeRepository = remember { ThemeRepository(this) }
            val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(themeRepository))
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            AplicacionvianappTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val viewModelStoreOwner = this@MainActivity
                val loginViewModel: LoginViewModel = viewModel(viewModelStoreOwner)
                val tokenState = loginViewModel.token.collectAsState()
                val token = tokenState.value ?: ""
                val userIdState = loginViewModel.userId.collectAsState()
                val userId = userIdState.value ?: 0
                val startDestination = if (token.isNotBlank()) "home" else "login"
                val parqueaderosViewModel: ParqueaderosViewModel = viewModel(viewModelStoreOwner)

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(navController = navController, startDestination = startDestination)
                        }
                        composable("home") {
                            HomeScreen(
                                navController = navController,
                                viewModelStoreOwner = viewModelStoreOwner,
                                themeViewModel = themeViewModel
                            )
                        }
                        composable("admin_panel") {
                            AdminPanelScreen(navController = navController)
                        }
                        composable("admin_users") {
                            UsuariosAdminScreen(navController = navController, token = token)
                        }
                        composable("admin_parqueaderos") {
                            ParqueaderosAdminScreen(navController = navController)
                        }
                        composable("admin_comentarios") { 
                            ComentariosAdminScreen(navController = navController)
                        }
                        composable(
                            "admin_valoraciones/{parqueaderoId}", 
                            arguments = listOf(navArgument("parqueaderoId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val parqueaderoId = backStackEntry.arguments?.getString("parqueaderoId")
                            if (parqueaderoId != null) {
                                ValoracionesScreen(navController = navController, parqueaderoId = parqueaderoId, token = token)
                            }
                        }
                        composable(
                            "cliente_valoraciones/{parqueaderoId}",
                            arguments = listOf(navArgument("parqueaderoId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val parqueaderoId = backStackEntry.arguments?.getString("parqueaderoId")
                            if (parqueaderoId != null) {
                                ValoracionesClienteScreen(navController = navController, parqueaderoId = parqueaderoId, token = token)
                            }
                        }
                        composable("openstreetmap") {
                            OpenStreetMapScreen(
                                navController = navController,
                                parqueaderosViewModel = parqueaderosViewModel,
                                token = token,
                                userId = userId
                            )
                        }
                        composable("bienvenida") {
                            BienvenidaScreen(themeViewModel = themeViewModel)
                        }
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                onLoginSuccess = { navController.navigate("home") },
                                viewModelStoreOwner = viewModelStoreOwner,
                                themeViewModel = themeViewModel
                            )
                        }
                        composable("registro") {
                           RegistroScreen(onIniciarSesion = { navController.navigate("login") })
                        }
                        composable("favoritos") {
                            FavoritosScreen(
                                parqueaderosViewModel = parqueaderosViewModel,
                                userId = userId,
                                token = token
                            )
                        }
                        composable("perfil") {
                            PerfilScreen(token, onEditPerfil = { /* acción de editar perfil */ })
                        }
                        composable("opiniones") {
                            OpinionesScreen()
                        }
                        composable("tarifas") {
                            TarifasScreen()
                        }
                        composable(
                            "parqueaderoOwnerMap/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val parqueaderoId = backStackEntry.arguments?.getString("id")
                            if (parqueaderoId != null) {
                                ParqueaderoOwnerMapScreen(
                                    navController = navController,
                                    parqueaderoId = parqueaderoId,
                                    parqueaderosViewModel = parqueaderosViewModel
                                )
                            }
                        }
                        composable(
                            "infoParqueadero/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val parqueaderoId = backStackEntry.arguments?.getString("id")
                            if (parqueaderoId != null) {
                                InformacionAdicionalScreen(
                                    navController = navController,
                                    parqueaderoId = parqueaderoId,
                                    parqueaderosViewModel = parqueaderosViewModel,
                                    token = token
                                )
                            } else {
                                Surface(modifier = Modifier.fillMaxSize()) {
                                    Text("Error: ID de parqueadero no encontrado.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
