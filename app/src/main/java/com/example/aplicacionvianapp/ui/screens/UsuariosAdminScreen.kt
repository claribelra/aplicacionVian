package com.example.aplicacionvianapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aplicacionvianapp.AdminViewModel
import com.example.aplicacionvianapp.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosAdminScreen(navController: NavController, adminViewModel: AdminViewModel = viewModel(), token: String) {

    LaunchedEffect(Unit) {
        adminViewModel.loadProfiles(token)
    }

    val profiles by adminViewModel.profiles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios Registrados") },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cabecera de la tabla
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nombre", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                    Text("Email", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                    Text("Rol", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Estado", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Acción", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                }
                Divider(modifier = Modifier.padding(vertical = 4.dp))
            }

            // Filas de datos
            items(profiles) { profile ->
                UserRow(profile = profile)
                Divider()
            }
        }
    }
}

@Composable
private fun UserRow(profile: Profile) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(profile.nombres, modifier = Modifier.weight(1.5f))
        Text("usuario@email.com", modifier = Modifier.weight(2f)) // Placeholder for email
        Text(profile.rol, modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFC8E6C9))
                .padding(vertical = 4.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Activo", color = Color(0xFF388E3C), fontSize = 12.sp)
        }
        Button(
            onClick = { /* TODO: Lógica para desactivar */ },
            modifier = Modifier.weight(1.5f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A65)),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text("Desactivar", fontSize = 12.sp)
        }
    }
}

