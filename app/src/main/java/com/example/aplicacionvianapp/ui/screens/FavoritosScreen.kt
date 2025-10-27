package com.example.aplicacionvianapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicacionvianapp.ParqueaderosViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun FavoritosScreen(
    parqueaderosViewModel: ParqueaderosViewModel = viewModel(),
    userId: Int,
    token: String
) {
    // LLAMADA CORREGIDA: Ya no se pasa apiService
    LaunchedEffect(Unit) {
        parqueaderosViewModel.cargarFavoritosDesdeBackend(userId, token)
    }

    val favoritosIds = parqueaderosViewModel.favoritos.collectAsState().value
    val parqueaderos = parqueaderosViewModel.parqueaderos.collectAsState().value
    val favoritos = parqueaderos.filter { favoritosIds.contains(it.id) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5E5E5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                "Parqueaderos favoritos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (favoritos.isEmpty()) {
                Text(
                    "No tienes parqueaderos favoritos.",
                    fontSize = 16.sp,
                    color = Color(0xFF181A2A),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(favoritos) { parqueadero ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    parqueadero.nombre_comercial,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    parqueadero.direccion,
                                    fontSize = 15.sp,
                                    color = Color(0xFF181A2A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Tarifa hora: $${parqueadero.tarifa_hora}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF5CA8FF)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
