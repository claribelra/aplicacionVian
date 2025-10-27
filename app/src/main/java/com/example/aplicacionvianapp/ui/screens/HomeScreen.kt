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
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacionvianapp.LoginViewModel
import com.example.aplicacionvianapp.ThemeViewModel
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelStoreOwner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModelStoreOwner: ViewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
    themeViewModel: ThemeViewModel
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val loginViewModel: LoginViewModel = viewModel(viewModelStoreOwner)
    val tokenState = loginViewModel.token.collectAsState()
    val token = tokenState.value ?: ""
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.fillMaxHeight().background(MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_vianapp),
                            contentDescription = "Logo Vianapp",
                            modifier = Modifier.size(80.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationDrawerItem(
                        label = { Text("Perfil", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (token.isNotBlank()) {
                                navController.navigate("perfil")
                            }
                        }
                    )
                    if (token.isBlank()) {
                        Text("Debes iniciar sesión para ver tu perfil", color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(start = 24.dp, top = 4.dp))
                    }
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    NavigationDrawerItem(
                        label = { Text("Favoritos", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("favoritos")
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Menu, contentDescription = "Cerrar sesión") }, // Considerar cambiar este icono
                        selected = false,
                        onClick = {
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
                    Text(
                        text = "Vianapp © 2025",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
                    )
                }
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val mapImage = if (isDarkMode) R.drawable.fondo_mapa_dark else R.drawable.fondo_mapa
                Image(
                    painter = painterResource(id = mapImage),
                    contentDescription = "Mapa de fondo",
                    modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp).align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(50))
                            .size(48.dp)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
                    }
                    IconButton(
                        onClick = { themeViewModel.toggleTheme() },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(50))
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.Nightlight,
                            contentDescription = "Cambiar Tema",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
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
                        color = MaterialTheme.colorScheme.surface,
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
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "no tienes ninguna reserva",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Selecciona tu destino, ajusta la hora y el parqueo será en segundos.",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
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
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "¿Donde quieres estacionar tu vehículo?",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
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
                        color = MaterialTheme.colorScheme.onSurface,
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .clickable { navController.navigate("opiniones") }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Opiniones de nuestros clientes",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
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
