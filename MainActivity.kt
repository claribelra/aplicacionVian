import com.example.aplicacionvianapp.ui.screens.FavoritosScreen
import com.example.aplicacionvianapp.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.aplicacionvianapp.ui.screens.OpinionesScreen
import com.example.aplicacionvianapp.ui.screens.PerfilScreen

// Inicialización de ApiService
val apiService = Retrofit.Builder()
    .baseUrl("https://TU_BACKEND_URL/") // Reemplaza por la URL real de tu backend
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(ApiService::class.java)

// Ejemplo: userId y token obtenidos tras login o perfil
val userId = /* obtener el id del usuario logueado */
val token = /* obtener el token de autenticación */

NavHost(navController = navController, startDestination = "home") {
    composable("home") {
        HomeScreen(navController)
    }
    composable("openstreetmap") {
        OpenStreetMapScreen()
    }
    composable("registro") {
        RegistroScreen(onIniciarSesion = { navController.navigate("login") })
    }
    composable("bienvenida") {
        BienvenidaScreen()
    }
    composable("opiniones") {
        OpinionesScreen(onBack = { navController.popBackStack() })
    }
    composable("perfil/{token}") { backStackEntry ->
        val token = backStackEntry.arguments?.getString("token") ?: ""
        PerfilScreen(token = token)
    }
    composable("favoritos") {
        FavoritosScreen(
            parqueaderosViewModel = parqueaderosViewModel,
            userId = userId,
            token = token,
            apiService = apiService
        )
    }
}
// Para navegar a FavoritosScreen desde cualquier parte:
// navController.navigate("favoritos/$userId/$token")
