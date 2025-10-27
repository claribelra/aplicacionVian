package com.example.aplicacionvianapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aplicacionvianapp.ComentariosViewModel
import com.example.aplicacionvianapp.Valoracion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValoracionesScreen(
    navController: NavController,
    parqueaderoId: String,
    token: String,
    comentariosViewModel: ComentariosViewModel = viewModel()
) {
    LaunchedEffect(parqueaderoId) {
        comentariosViewModel.loadValoraciones(parqueaderoId, token)
    }

    val valoraciones by comentariosViewModel.valoraciones.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Valoraciones del Parqueadero") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(valoraciones) { valoracion ->
                    ValoracionRow(valoracion = valoracion)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { navController.popBackStack() }) {
                Text("Volver a lista de parqueaderos")
            }
        }
    }
}

@Composable
private fun ValoracionRow(valoracion: Valoracion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Estrellas
                Row {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < valoracion.rating) Color.Yellow else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(valoracion.comment, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("- ${valoracion.author}", fontSize = 12.sp, color = Color.Gray)
            }
            TextButton(onClick = { /* TODO: Lógica para eliminar */ }) {
                Text("Eliminar", color = Color.Red)
            }
        }
    }
}

