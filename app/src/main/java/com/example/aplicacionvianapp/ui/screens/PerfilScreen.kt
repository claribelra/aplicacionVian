package com.example.aplicacionvianapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacionvianapp.R
import com.example.aplicacionvianapp.Profile
import com.example.aplicacionvianapp.UserViewModel
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PerfilScreen(token: String, onEditPerfil: () -> Unit = {}) {
    val userViewModel: UserViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(token) as T
        }
    })
    val profile by userViewModel.profile.collectAsState()
    val reservas by userViewModel.reservas.collectAsState()
    val opiniones by userViewModel.opiniones.collectAsState()
    val error by userViewModel.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(24.dp))
            // Mostrar error si existe
            if (error != null) {
                Text(error!!, color = Color.Red, fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(24.dp))
            }
            // Cuadro superior con datos del usuario
            if (profile != null && error == null) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Imagen de perfil (placeholder)
                        Image(
                            painter = painterResource(id = R.drawable.ic_user_placeholder),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.size(72.dp).clip(CircleShape)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${profile?.nombres ?: "Sin nombre"} ${profile?.apellidos ?: ""}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(profile?.telefono ?: "Sin teléfono", fontSize = 16.sp)
                            Text(profile?.rol ?: "Sin rol", fontSize = 16.sp)
                        }
                        IconButton(onClick = onEditPerfil) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar perfil")
                        }
                    }
                }
            } else if (error != null) {
                Text(error ?: "Error desconocido", color = Color.Red, fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(24.dp))
            } else {
                Text("Cargando perfil...", fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            Spacer(Modifier.height(32.dp))
            // DEBUG: Mostrar el token recibido en la pantalla de perfil
            Text(
                text = "Token actual: $token",
                color = Color.Blue,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(8.dp))
            // Historial de reservas
            Text("Historial de reservas", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 24.dp))
            if (reservas.isNullOrEmpty()) {
                Text("No tienes ninguna reserva", fontSize = 16.sp, modifier = Modifier.padding(start = 24.dp, top = 8.dp))
            } else {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    reservas?.forEach { reserva ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                            Image(painter = painterResource(id = R.drawable.ic_location), contentDescription = "Icono reserva", modifier = Modifier.size(48.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(reserva.nombreZona ?: "Sin zona", fontSize = 16.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
            // Opiniones
            Text("Tus opiniones", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 24.dp))
            if (opiniones.isNullOrEmpty()) {
                Text("No tienes opiniones", fontSize = 16.sp, modifier = Modifier.padding(start = 24.dp, top = 8.dp))
            } else {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    opiniones?.forEach { opinion ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
                            Image(
                                painter = painterResource(id = opinion.imagenResId ?: R.drawable.ic_user_placeholder),
                                contentDescription = "Foto parqueadero",
                                modifier = Modifier.size(48.dp).clip(CircleShape)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(opinion.nombreParqueadero ?: "Sin nombre", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Row {
                                    repeat(opinion.estrellas ?: 0) {
                                        Image(painter = painterResource(id = R.drawable.ic_star), contentDescription = "Estrella", modifier = Modifier.size(20.dp))
                                    }
                                }
                                Text(opinion.comentario ?: "Sin comentario", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
            // ...puedes agregar el footer aquí si lo necesitas...
        }
    }
}

// Modelos para reservas y opiniones
// Puedes moverlos a sus propios archivos si ya existen

data class Reserva(val nombreZona: String)
data class Opinion(val nombreParqueadero: String, val estrellas: Int, val comentario: String, val imagenResId: Int)
