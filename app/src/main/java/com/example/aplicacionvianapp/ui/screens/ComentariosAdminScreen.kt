package com.example.aplicacionvianapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aplicacionvianapp.Parqueadero
import com.example.aplicacionvianapp.ParqueaderosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentariosAdminScreen(
    navController: NavController,
    parqueaderosViewModel: ParqueaderosViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        parqueaderosViewModel.cargarParqueaderos()
    }

    val parqueaderos by parqueaderosViewModel.parqueaderos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comentarios de Parqueaderos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(parqueaderos) { parqueadero ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(parqueadero.nombre_comercial, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { navController.navigate("admin_valoraciones/${parqueadero.id}") }) {
                            Text("Ver")
                        }
                    }
                }
            }
        }
    }
}
