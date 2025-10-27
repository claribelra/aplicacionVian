package com.example.aplicacionvianapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aplicacionvianapp.Parqueadero
import com.example.aplicacionvianapp.ParqueaderosViewModel
import com.example.aplicacionvianapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParqueaderosAdminScreen(
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
                title = { Text("Parqueaderos Registrados") },
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
                .padding(horizontal = 8.dp)
        ) {
            item {
                HeaderRow()
                Divider()
            }
            items(parqueaderos) { parqueadero ->
                ParqueaderoRow(parqueadero = parqueadero)
                Divider()
            }
        }
    }
}

@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Foto", modifier = Modifier.width(100.dp), fontWeight = FontWeight.Bold)
        Text("Propietario", modifier = Modifier.width(100.dp), fontWeight = FontWeight.Bold)
        Text("Nombre", modifier = Modifier.width(150.dp), fontWeight = FontWeight.Bold)
        Text("Dirección", modifier = Modifier.width(150.dp), fontWeight = FontWeight.Bold)
        Text("Ciudad", modifier = Modifier.width(100.dp), fontWeight = FontWeight.Bold)
        Text("Teléfono", modifier = Modifier.width(120.dp), fontWeight = FontWeight.Bold)
        Text("Email", modifier = Modifier.width(200.dp), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ParqueaderoRow(parqueadero: Parqueadero) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholders for images
        Image(painter = painterResource(id = R.drawable.ic_placeholder), contentDescription = "Foto Parqueadero", modifier = Modifier.width(100.dp).size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        Image(painter = painterResource(id = R.drawable.ic_user_placeholder), contentDescription = "Foto Propietario", modifier = Modifier.width(100.dp).size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop)

        Text(parqueadero.nombre_comercial, modifier = Modifier.width(150.dp))
        Text(parqueadero.direccion, modifier = Modifier.width(150.dp))
        Text("-", modifier = Modifier.width(100.dp)) // Placeholder for Ciudad
        Text("3101234567", modifier = Modifier.width(120.dp)) // Placeholder for Teléfono
        Text("parqueadero@email.com", modifier = Modifier.width(200.dp)) // Placeholder for Email
    }
}
