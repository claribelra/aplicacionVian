package com.example.aplicacionvianapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aplicacionvianapp.ParqueaderosViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacionAdicionalScreen(
    navController: NavHostController,
    parqueaderoId: String,
    parqueaderosViewModel: ParqueaderosViewModel = viewModel(),
    token: String
) {
    val scope = rememberCoroutineScope()
    // Obtenemos el parqueadero específico desde el ViewModel
    val parqueadero by parqueaderosViewModel.getParqueaderoById(parqueaderoId).collectAsState(initial = null)

    // Estados para los campos de texto
    var correo by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var nombreComercial by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    // Cuando el objeto 'parqueadero' se cargue, actualizamos los estados locales
    LaunchedEffect(parqueadero) {
        parqueadero?.let {
            nombreComercial = it.nombre_comercial
            direccion = it.direccion
            correo = "parqueadero@vianapp.com" // Placeholder
            telefono = "3101234567" // Placeholder
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Información Adicional", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5))
            )
        }
    ) { paddingValues ->
        if (parqueadero == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                EditableInfoField(label = "Correo electrónico", value = correo, onValueChange = { correo = it })
                Spacer(modifier = Modifier.height(16.dp))
                EditableInfoField(label = "Dirección", value = direccion, onValueChange = { direccion = it })
                Spacer(modifier = Modifier.height(16.dp))
                EditableInfoField(label = "Nombre comercial", value = nombreComercial, onValueChange = { nombreComercial = it })
                Spacer(modifier = Modifier.height(16.dp))
                EditableInfoField(label = "Teléfono", value = telefono, onValueChange = { telefono = it })

                Spacer(modifier = Modifier.height(24.dp))
                Text("Cambiar foto del parqueadero", modifier = Modifier.fillMaxWidth())
                Button(onClick = { /* TODO */ }) { Text("Seleccionar archivo") }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cambiar foto del dueño", modifier = Modifier.fillMaxWidth())
                Button(onClick = { /* TODO */ }) { Text("Seleccionar archivo") }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        scope.launch {
                            val datosActualizados = mutableMapOf<String, String>()
                            if (nombreComercial != parqueadero?.nombre_comercial) {
                                datosActualizados["nombre_comercial"] = nombreComercial
                            }
                            if (direccion != parqueadero?.direccion) {
                                datosActualizados["direccion"] = direccion
                            }

                            if (datosActualizados.isNotEmpty()) {
                                // LLAMADA CORREGIDA: Ya no se pasa apiService
                                parqueaderosViewModel.actualizarParqueadero(
                                    token = token,
                                    parqueaderoId = parqueaderoId,
                                    datosActualizados = datosActualizados
                                )
                            }
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Guardar cambios", fontSize = 18.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditableInfoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}
