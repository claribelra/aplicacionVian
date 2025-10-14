package com.example.aplicacionvianapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.aplicacionvianapp.ApiService
import com.example.aplicacionvianapp.Parqueadero
import com.example.aplicacionvianapp.ParqueaderosViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicacionvianapp.R
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.ui.viewinterop.AndroidView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.zIndex
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.osmdroid.views.overlay.infowindow.InfoWindow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope

class ModernMarkerInfoWindow(mapView: MapView) : InfoWindow(R.layout.custom_marker_info_window, mapView) {
    override fun onOpen(item: Any?) {
        val marker = item as? Marker ?: return
        val view = mView
        val title = view.findViewById<TextView>(R.id.popup_title)
        val subtitle = view.findViewById<TextView>(R.id.popup_subtitle)
        val icon = view.findViewById<ImageView>(R.id.popup_icon)
        title.text = marker.title ?: "¡Aquí estás!"
        subtitle.text = marker.subDescription ?: "Esta es tu ubicación detectada por GPS."
        // Puedes personalizar el icono aquí si lo deseas
    }
    override fun onClose() {}
}

class ParqueaderoInfoWindow(
    val parqueadero: Parqueadero,
    mapView: MapView,
    val parqueaderosViewModel: com.example.aplicacionvianapp.ParqueaderosViewModel,
    val scope: CoroutineScope,
    val apiService: ApiService,
    val token: String,
    val onClick: (() -> Unit)? = null
) : InfoWindow(R.layout.custom_marker_info_window_parqueadero, mapView) {
    override fun onOpen(item: Any?) {
        val marker = item as? Marker ?: return
        val view = mView
        val title = view.findViewById<TextView>(R.id.popup_title)
        val subtitle = view.findViewById<TextView>(R.id.popup_subtitle)
        val icon = view.findViewById<ImageView>(R.id.popup_icon)
        val favoriteIcon = view.findViewById<ImageView>(R.id.popup_favorite)
        title.text = parqueadero.nombre_comercial
        subtitle.text = parqueadero.direccion

        // Observa el estado de favoritos y actualiza el ícono automáticamente
        scope.launch {
            parqueaderosViewModel.favoritos.collect { favoritosSet ->
                val isFav = favoritosSet.contains(parqueadero.id)
                favoriteIcon.setImageResource(R.drawable.ic_star)
                favoriteIcon.setColorFilter(if (isFav) 0xFFFFD700.toInt() else 0xFFB0B0B0.toInt())
            }
        }

        // Acción al tocar el icono de favorito
        favoriteIcon.setOnClickListener {
            scope.launch {
                if (parqueaderosViewModel.esFavorito(parqueadero.id)) {
                    // Aquí podrías implementar quitar favorito si tienes la función
                    // parqueaderosViewModel.quitarFavoritoEnBackend(parqueadero.id, token, apiService)
                } else {
                    parqueaderosViewModel.agregarFavoritoEnBackend(parqueadero.id, token, apiService)
                }
                val nuevoFav = parqueaderosViewModel.esFavorito(parqueadero.id)
                Toast.makeText(view.context, if (nuevoFav) "Agregado a favoritos" else "Quitado de favoritos", Toast.LENGTH_SHORT).show()
                favoriteIcon.setColorFilter(if (nuevoFav) 0xFFFFD700.toInt() else 0xFFB0B0B0.toInt())
                marker.closeInfoWindow()
                marker.showInfoWindow()
            }
        }
        // Callback de selección
        view.setOnClickListener { onClick?.invoke() }
    }
    override fun onClose() { /* No se requiere acción especial */ }
}

@Composable
fun ParqueaderoInfoCard(parqueadero: Parqueadero, onDismiss: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(Color(0x88000000)).clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier.width(320.dp)
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(parqueadero.nombre_comercial, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF4CAF50))
                Spacer(Modifier.height(8.dp))
                Text("Dirección: ${parqueadero.direccion}", fontSize = 16.sp)
                Text("Espacios: ${parqueadero.espacios}", fontSize = 16.sp)
                Text("Tarifa hora: $${parqueadero.tarifa_hora}", fontSize = 16.sp)
                Text("Tarifa día: $${parqueadero.tarifa_dia}", fontSize = 16.sp)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onDismiss) { Text("Cerrar") }
            }
        }
    }
}

@Composable
fun OpenStreetMapScreen(
    navController: NavHostController,
    parqueaderosViewModel: ParqueaderosViewModel,
    token: String
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationState = remember { mutableStateOf<Location?>(null) }
    val permissionState = remember { mutableStateOf(false) }
    val showPermissionDenied = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val parqueaderos = remember { mutableStateListOf<Parqueadero>() }
    var selectedParqueadero by remember { mutableStateOf<Parqueadero?>(null) }

    // Launcher para pedir permisos
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionState.value = granted
        showPermissionDenied.value = !granted
    }

    // Solicitar permiso al iniciar
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            permissionState.value = true
        }
    }

    // Obtener ubicación si el permiso está concedido
    LaunchedEffect(permissionState.value) {
        if (permissionState.value) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                locationState.value = location
            }
        }
    }

    // Recordar el MapView para evitar recrearlo en cada recomposición
    val mapView = remember {
        MapView(context).apply {
            Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
            setMultiTouchControls(true)
        }
    }
    // Estado para el marker
    val marker = remember {
        Marker(mapView).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "¡Aquí estás!"
            subDescription = "Esta es tu ubicación detectada por GPS."
        }
    }

    // Retrofit setup
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/") // Cambiado para emulador Android
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val apiService = remember { retrofit.create(ApiService::class.java) }

    // Obtener parqueaderos al iniciar
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val result = apiService.getParqueaderos()
                parqueaderos.clear()
                parqueaderos.addAll(result)
            } catch (e: Exception) {
                // Manejo de error opcional
            }
        }
    }

    // Efecto para actualizar la ubicación y el marker
    LaunchedEffect(locationState.value) {
        val loc = locationState.value
        if (loc != null) {
            val geoPoint = GeoPoint(loc.latitude, loc.longitude)
            mapView.overlays.clear() // Limpiar overlays previos
            marker.position = geoPoint
            mapView.overlays.add(marker)
            // Centrar y animar el mapa directamente a la ubicación del usuario
            mapView.controller.animateTo(geoPoint)
            mapView.controller.setZoom(18.0)
            marker.showInfoWindow() // Mostrar el popup
        }
    }

    // Efecto para mostrar markers de parqueaderos
    LaunchedEffect(parqueaderos, locationState.value) {
        // Limpiar overlays previos
        mapView.overlays.clear()
        // Marker usuario
        locationState.value?.let { loc ->
            val geoPoint = GeoPoint(loc.latitude, loc.longitude)
            marker.position = geoPoint
            mapView.overlays.add(marker)
            mapView.controller.animateTo(geoPoint)
            mapView.controller.setZoom(18.0)
            marker.showInfoWindow()
        }
        // Markers parqueaderos
        parqueaderos.forEach { parqueadero ->
            val markerP = Marker(mapView)
            // Usar latitud y longitud reales si existen en el modelo
            val lat = parqueadero.latitud ?: 4.60971
            val lon = parqueadero.longitud ?: -74.08175
            markerP.position = GeoPoint(lat, lon)
            markerP.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            markerP.infoWindow = ParqueaderoInfoWindow(parqueadero, mapView, parqueaderosViewModel, coroutineScope, apiService, "Bearer TU_TOKEN") {
                selectedParqueadero = parqueadero
            }
            mapView.overlays.add(markerP)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5E5E5))
    ) {
        // Mostrar mensaje si el permiso fue rechazado
        if (showPermissionDenied.value) {
            Surface(
                color = Color(0xFF1976D2), // Color azul llamativo
                shadowElevation = 16.dp, // Más sombra
                shape = RoundedCornerShape(24.dp), // Bordes más redondeados
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp)
                    .zIndex(2f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Por favor activa los permisos de ubicación desde configuración",
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Mapa con marcador y popup
        Surface(
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.55f)
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
                .zIndex(1f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(factory = { mapView }, update = {
                    // El update se encarga de refrescar overlays si es necesario
                }, modifier = Modifier.fillMaxSize())

                // Mostrar advertencia sobre el mapa si no hay parqueaderos
                Text(
                    text = "No se encontraron parqueaderos disponibles en tu ubicación",
                    fontSize = 14.sp,
                    color = Color(0xFF181A2A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .background(
                            color = Color(0xFF1976D2).copy(alpha = 0.90f), // Fondo azul llamativo
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Barra inferior con opciones
        Surface(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .height(48.dp)
                                .clickable { /* lógica de búsqueda */ }
                                .padding(horizontal = 16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Buscar parqueadero",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF5CA8FF),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_microphone),
                                contentDescription = "Voz",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_recent),
                                contentDescription = "Recientes",
                                tint = Color(0xFF181A2A),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Recientes", fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { navController.navigate("favoritos") }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_favorite),
                                contentDescription = "Favoritos",
                                tint = Color(0xFF181A2A),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Favoritos", fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_nearby),
                                contentDescription = "Cercanos",
                                tint = Color(0xFF181A2A),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cercanos", fontSize = 14.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .clickable { /* lógica de parquear sin dirección */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Parquear sin dirección",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_vianapp),
                        contentDescription = "Logo Vianapp",
                        modifier = Modifier.size(48.dp)
                    )
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_facebook),
                            contentDescription = "Facebook",
                            tint = Color(0xFF181A2A),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_instagram),
                            contentDescription = "Instagram",
                            tint = Color(0xFF181A2A),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_x),
                            contentDescription = "X",
                            tint = Color(0xFF181A2A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Teléfono", fontSize = 12.sp, color = Color(0xFF181A2A))
                        Text("0000000", fontSize = 12.sp, color = Color(0xFF181A2A))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Correo", fontSize = 12.sp, color = Color(0xFF181A2A))
                        Text("viANApp@gmail.com", fontSize = 12.sp, color = Color(0xFF181A2A))
                    }
                }
            }
        }

        // Mostrar información del parqueadero seleccionado
        selectedParqueadero?.let { parqueadero ->
            ParqueaderoInfoCard(parqueadero = parqueadero, onDismiss = { selectedParqueadero = null })
        }
    }
}
