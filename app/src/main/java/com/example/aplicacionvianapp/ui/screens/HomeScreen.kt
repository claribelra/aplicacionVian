package com.example.aplicacionvianapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.aplicacionvianapp.R
import com.example.aplicacionvianapp.ui.components.BottomBar
import com.example.aplicacionvianapp.ui.components.HomeIconButton
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacionvianapp.LoginViewModel
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelStoreOwner

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModelStoreOwner: ViewModelStoreOwner = androidx.compose.ui.platform.LocalContext.current as ViewModelStoreOwner
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val loginViewModel: LoginViewModel = viewModel(viewModelStoreOwner)
    val tokenState = loginViewModel.token.collectAsState()
    val token = tokenState.value ?: ""

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(260.dp)
                    .background(Color.White)
            ) {
                // Encabezado con logo/avatar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1976D2))
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_vianapp),
                        contentDescription = "Logo Vianapp",
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Opción Perfil
                ListItem(
                    headlineContent = { Text("Perfil", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2)) },
                    leadingContent = {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color(0xFF1976D2))
                    },
                    modifier = Modifier.clickable {
                        scope.launch { drawerState.close() }
                        if (token.isNotBlank()) {
                            navController.navigate("perfil")
                        } else {
                            // Aquí podrías mostrar un mensaje visual, pero la advertencia ya está abajo
                        }
                    }
                )
                if (token.isBlank()) {
                    Text(
                        "Debes iniciar sesión para ver tu perfil",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 24.dp, top = 4.dp)
                    )
                }
                HorizontalDivider()
                // Opción Favoritos
                ListItem(
                    headlineContent = { Text("Favoritos", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50)) },
                    leadingContent = {
                        Icon(Icons.Default.Favorite, contentDescription = "Favoritos", tint = Color(0xFF4CAF50))
                    },
                    modifier = Modifier.clickable {
                        scope.launch { drawerState.close() }
                        navController.navigate("favoritos")
                    }
                )
                HorizontalDivider()
                // Opción Cerrar sesión
                ListItem(
                    headlineContent = { Text("Cerrar sesión", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F)) },
                    leadingContent = {
                        Icon(Icons.Default.Menu, contentDescription = "Cerrar sesión", tint = Color(0xFFD32F2F))
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                // Pie de página opcional
                Text(
                    text = "Vianapp © 2025",
                    color = Color(0xFFB0B0B0),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
                )
            }
        },
        // El contenido principal de la pantalla
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE5E5E5))
            ) {
                // Botón de menú siempre visible y destacado
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 32.dp)
                        .background(Color.White, shape = RoundedCornerShape(50))
                        .size(48.dp)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color(0xFF1976D2), modifier = Modifier.size(32.dp))
                }
                Image(
                    painter = painterResource(id = R.drawable.fondo_mapa),
                    contentDescription = "Mapa de fondo",
                    modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.Center
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo_vianapp),
                        contentDescription = "Logo Vianapp",
                        modifier = Modifier.size(140.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_car),
                                contentDescription = null,
                                tint = Color(0xFF181A2A),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "no tienes ninguna reserva",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF181A2A)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Selecciona tu destino, ajusta la hora y el parqueo será en segundos.",
                                fontSize = 16.sp,
                                color = Color(0xFF181A2A),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_parking),
                                    contentDescription = null,
                                    tint = Color(0xFF181A2A),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "¿Donde quieres estacionar tu vehículo?",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF181A2A)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.weight(1f).clickable {
                                        navController.navigate("openstreetmap")
                                    }
                                ) {
                                    Box(modifier = Modifier.height(40.dp), contentAlignment = Alignment.CenterStart) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_location),
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp).padding(start = 8.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF5CA8FF),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_search),
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Gestión de parqueos",
                        fontSize = 14.sp,
                        color = Color(0xFF181A2A),
                        modifier = Modifier.padding(start = 24.dp, top = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HomeIconButton("Vehículos", R.drawable.ic_car)
                        HomeIconButton("Actividad", R.drawable.ic_activity)
                        HomeIconButton("Favoritos", R.drawable.ic_favorite)
                        HomeIconButton("Metodos de pago", R.drawable.ic_payment)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Estamos en los mejores sectores de Bogotá",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF181A2A)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = null,
                            tint = Color(0xFF181A2A),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .clickable { navController.navigate("opiniones") }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Opiniones de nuestros clientes",
                                fontSize = 18.sp,
                                color = Color(0xFF181A2A),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    BottomBar()
                }
            }
        }
    )
}
