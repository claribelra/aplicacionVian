package com.example.aplicacionvianapp.ui.screens

import androidx.preference.PreferenceManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aplicacionvianapp.Parqueadero
import com.example.aplicacionvianapp.ParqueaderosViewModel
import com.example.aplicacionvianapp.R
import com.example.aplicacionvianapp.Reserva
import com.example.aplicacionvianapp.ui.theme.Green
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun ParqueaderoOwnerMapScreen(
    navController: NavHostController,
    parqueaderoId: String,
    parqueaderosViewModel: ParqueaderosViewModel = viewModel()
) {
    val context = LocalContext.current

    // La pantalla solo le pide al ViewModel que cargue los datos.
    LaunchedEffect(Unit) {
        parqueaderosViewModel.cargarParqueaderos()
    }

    val parqueadero by parqueaderosViewModel.getParqueaderoById(parqueaderoId).collectAsState(initial = null)

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }
    }

    LaunchedEffect(parqueadero) {
        parqueadero?.let { p ->
            val geoPoint = GeoPoint(p.latitud ?: 0.0, p.longitud ?: 0.0)
            mapView.overlays.clear()
            val marker = Marker(mapView).apply {
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = p.nombre_comercial
                subDescription = p.direccion
                position = geoPoint
            }
            mapView.overlays.add(marker)
            marker.showInfoWindow()
            mapView.controller.setZoom(18.0)
            mapView.controller.animateTo(geoPoint)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = parqueadero?.nombre_comercial ?: "Vista Parqueadero",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2),
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("infoParqueadero/$parqueaderoId") },
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = parqueadero != null,
            colors = ButtonDefaults.buttonColors(containerColor = Green)
        ) {
            Text("Información Parqueadero", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("tarifas") },
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = parqueadero != null,
            colors = ButtonDefaults.buttonColors(containerColor = Green)
        ) {
            Text("Ajustar Tarifas", fontSize = 16.sp)
        }
        if (parqueadero == null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Cargando información del parqueadero...", color = Color.Gray, fontSize = 16.sp)
        } else {
            Spacer(modifier = Modifier.height(24.dp))
            ReservasRecientesTable()
        }
    }
}

@Composable
fun ReservasRecientesTable() {
    val reservas = remember {
        listOf(
            Reserva(nombre = "Cliente", cedula = "1013594121", telefono = "3125070934", placa = "agm333", tipo = "Carro", fechaHora = "Oct. 14, 2025, 12:46 a.m."),
            Reserva(nombre = "Test User", cedula = "123456789", telefono = "3001234567", placa = "ABC123", tipo = "Carro", fechaHora = "Oct. 8, 2025, 10 a.m."),
            Reserva(nombre = "Cliente", cedula = "1013594121", telefono = "3125070943", placa = "agm231", tipo = "Carro", fechaHora = "Oct. 7, 2025, 5:21 p.m.")
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Reservas recientes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Header
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE3F2FD))
            .padding(vertical = 8.dp, horizontal = 4.dp)) {
            Text("Nombre", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
            Text("Placa", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Fecha/Hora", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
            Text("Acciones", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold)
        }

        // Rows
        LazyColumn {
            items(reservas) { reserva ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(reserva.nombre ?: "", modifier = Modifier.weight(1.5f))
                    Text(reserva.placa ?: "", modifier = Modifier.weight(1f))
                    Text(reserva.fechaHora ?: "", modifier = Modifier.weight(2f), fontSize = 14.sp)
                    Button(onClick = { /* TODO */ }, modifier = Modifier.weight(1.2f)) {
                        Text("Cancelar", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
