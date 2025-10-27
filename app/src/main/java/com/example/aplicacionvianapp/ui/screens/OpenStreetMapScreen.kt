package com.example.aplicacionvianapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.aplicacionvianapp.Parqueadero
import com.example.aplicacionvianapp.ParqueaderosViewModel
import com.example.aplicacionvianapp.R
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import androidx.preference.PreferenceManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.infowindow.InfoWindow
import android.widget.ImageView
import android.widget.TextView

class ModernMarkerInfoWindow(mapView: MapView) : InfoWindow(R.layout.custom_marker_info_window, mapView) {
    override fun onOpen(item: Any?) {
        val marker = item as? Marker ?: return
        val view = mView
        val title = view.findViewById<TextView>(R.id.popup_title)
        val subtitle = view.findViewById<TextView>(R.id.popup_subtitle)
        title.text = marker.title ?: "¡Aquí estás!"
        subtitle.text = marker.subDescription ?: "Esta es tu ubicación detectada por GPS."
    }
    override fun onClose() {}
}

class ParqueaderoInfoWindow(
    val parqueadero: Parqueadero,
    mapView: MapView,
    val parqueaderosViewModel: ParqueaderosViewModel,
    val token: String,
    val userId: Int,
    val onClick: (() -> Unit)? = null
) : InfoWindow(R.layout.custom_marker_info_window_parqueadero, mapView) {
    override fun onOpen(item: Any?) {
        val marker = item as? Marker ?: return
        val view = mView
        val title = view.findViewById<TextView>(R.id.popup_title)
        val subtitle = view.findViewById<TextView>(R.id.popup_subtitle)
        val favoriteIcon = view.findViewById<ImageView>(R.id.popup_favorite)
        title.text = parqueadero.nombre_comercial
        subtitle.text = parqueadero.direccion

        val isFav = parqueaderosViewModel.esFavorito(parqueadero.id)
        favoriteIcon.setImageResource(R.drawable.ic_star)
        favoriteIcon.setColorFilter(if (isFav) 0xFFFFD700.toInt() else 0xFFB0B0B0.toInt())

        favoriteIcon.setOnClickListener {
            val isCurrentlyFavorite = parqueaderosViewModel.esFavorito(parqueadero.id)
            parqueaderosViewModel.toggleFavorito(parqueadero.id)
            if (isCurrentlyFavorite) {
                // TODO: Lógica para quitar favorito
                Toast.makeText(view.context, "Quitado de favoritos", Toast.LENGTH_SHORT).show()
            } else {
                parqueaderosViewModel.agregarFavoritoEnBackend(userId, parqueadero.id, token)
                Toast.makeText(view.context, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
            }
            marker.closeInfoWindow()
            marker.showInfoWindow()
        }

        view.setOnClickListener { onClick?.invoke() }
    }
    override fun onClose() {}
}

@Composable
fun ParqueaderoInfoCard(
    parqueadero: Parqueadero,
    onDismiss: () -> Unit,
    onRouteClick: () -> Unit,
    onValoracionesClick: () -> Unit // Callback añadido
) {
    Box(
        Modifier.fillMaxSize().background(Color(0x88000000)).clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier.width(320.dp).clickable(enabled = false) {}
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(parqueadero.nombre_comercial, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF4CAF50))
                Spacer(Modifier.height(8.dp))
                Text("Dirección: ${parqueadero.direccion}", fontSize = 16.sp)
                Text("Espacios: ${parqueadero.espacios}", fontSize = 16.sp)
                Text("Tarifa hora: $${parqueadero.tarifa_hora}", fontSize = 16.sp)
                Text("Tarifa día: $${parqueadero.tarifa_dia}", fontSize = 16.sp)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onRouteClick) {
                        Icon(Icons.Default.Directions, contentDescription = "Cómo llegar")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cómo llegar")
                    }
                    Button(onClick = onValoracionesClick) { // Botón añadido
                        Icon(Icons.Default.Comment, contentDescription = "Valoraciones")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Valoraciones")
                    }
                }
                 Spacer(Modifier.height(8.dp))
                Button(onClick = onDismiss) { Text("Cerrar") }
            }
        }
    }
}

@Composable
fun OpenStreetMapScreen(
    navController: NavHostController,
    parqueaderosViewModel: ParqueaderosViewModel,
    token: String,
    userId: Int
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val parqueaderos by parqueaderosViewModel.parqueaderos.collectAsState()
    val locationState = remember { mutableStateOf<Location?>(null) }
    val permissionState = remember { mutableStateOf(false) }
    val showPermissionDenied = remember { mutableStateOf(false) }
    var selectedParqueadero by remember { mutableStateOf<Parqueadero?>(null) }
    var isRouteLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionState.value = granted
        showPermissionDenied.value = !granted
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionState.value = true
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(permissionState.value) {
        if (permissionState.value) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                locationState.value = location
            }
            parqueaderosViewModel.cargarParqueaderos()
            parqueaderosViewModel.cargarFavoritosDesdeBackend(userId, token)
        }
    }

    val mapView = remember {
        MapView(context).apply {
            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
            Configuration.getInstance().userAgentValue = context.packageName
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(12.0)
            controller.setCenter(GeoPoint(4.60971, -74.08175))
        }
    }

    fun drawRoute(start: GeoPoint, end: GeoPoint) {
        coroutineScope.launch(Dispatchers.IO) {
            isRouteLoading = true
            val roadManager: RoadManager = OSRMRoadManager(context, context.packageName)
            try {
                val road = roadManager.getRoad(arrayListOf(start, end))
                if (road.mStatus == Road.STATUS_OK) {
                    launch(Dispatchers.Main) {
                        mapView.overlays.removeAll { it is Polyline }
                        val roadOverlay = RoadManager.buildRoadOverlay(road)
                        roadOverlay.color = 0x800000FF.toInt()
                        roadOverlay.width = 12f
                        mapView.overlays.add(roadOverlay)
                        mapView.invalidate()
                    }
                } else {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Error al calcular la ruta: ${road.mStatus}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Error de red al calcular la ruta", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isRouteLoading = false
            }
        }
    }

    LaunchedEffect(parqueaderos) {
        val currentOverlays = mapView.overlays.toList()
        mapView.overlays.removeAll(currentOverlays.filterNot { it is Marker && it.id == "user_marker" })

        parqueaderos.forEach { parqueadero ->
            val markerP = Marker(mapView)
            markerP.position = GeoPoint(parqueadero.latitud ?: 0.0, parqueadero.longitud ?: 0.0)
            markerP.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            markerP.infoWindow = ParqueaderoInfoWindow(parqueadero, mapView, parqueaderosViewModel, token, userId) {
                selectedParqueadero = parqueadero
            }
            markerP.setOnMarkerClickListener { marker, _ ->
                if (!marker.isInfoWindowShown) marker.showInfoWindow() else marker.closeInfoWindow()
                true
            }
            mapView.overlays.add(markerP)
        }
        mapView.invalidate()
    }

    LaunchedEffect(locationState.value) {
        val loc = locationState.value ?: return@LaunchedEffect

        mapView.overlays.removeAll { it is Marker && it.id == "user_marker" }

        val userMarker = Marker(mapView).apply {
            id = "user_marker"
            position = GeoPoint(loc.latitude, loc.longitude)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "¡Aquí estás!"
            infoWindow = ModernMarkerInfoWindow(mapView)
        }
        mapView.overlays.add(userMarker)

        mapView.controller.animateTo(userMarker.position, 18.0, 1000L)
        userMarker.showInfoWindow()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE5E5E5))
    ) {
        if (showPermissionDenied.value) {
            Surface(
                color = Color(0xFF1976D2),
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.align(Alignment.Center).padding(32.dp).zIndex(3f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(28.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Por favor activa los permisos de ubicación desde configuración", fontSize = 18.sp, color = Color.White, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.55f).align(Alignment.TopCenter).padding(top = 40.dp).zIndex(1f)
        ) {
            AndroidView(factory = { mapView })
        }

        Surface(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).zIndex(2f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF4CAF50), modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(48.dp).clickable { /* lógica de búsqueda */ }.padding(horizontal = 16.dp)) {
                            Image(painter = painterResource(id = R.drawable.ic_location), contentDescription = null, modifier = Modifier.size(24.dp), colorFilter = ColorFilter.tint(Color.White))
                            Spacer(Modifier.width(8.dp))
                            Text("Buscar parqueadero", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF5CA8FF), modifier = Modifier.size(48.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(painter = painterResource(id = R.drawable.ic_mic), contentDescription = "Voz", tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                            Icon(painter = painterResource(id = R.drawable.ic_recent), contentDescription = "Recientes", tint = Color(0xFF181A2A), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Recientes", fontSize = 14.sp)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp).clickable { navController.navigate("favoritos") }) {
                            Icon(painter = painterResource(id = R.drawable.ic_favorite), contentDescription = "Favoritos", tint = Color(0xFF181A2A), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Favoritos", fontSize = 14.sp)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                            Icon(painter = painterResource(id = R.drawable.ic_nearby), contentDescription = "Cercanos", tint = Color(0xFF181A2A), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cercanos", fontSize = 14.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF4CAF50), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.height(48.dp).clickable { /* lógica de parquear sin dirección */ }, contentAlignment = Alignment.Center) {
                        Text("Parquear sin dirección", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                 Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.logo_vianapp), contentDescription = "Logo Vianapp", modifier = Modifier.size(48.dp))
                    Row {
                        Icon(painter = painterResource(id = R.drawable.ic_facebook), contentDescription = "Facebook", tint = Color(0xFF181A2A), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(4.dp))
                        Icon(painter = painterResource(id = R.drawable.ic_instagram), contentDescription = "Instagram", tint = Color(0xFF181A2A), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(4.dp))
                        Icon(painter = painterResource(id = R.drawable.ic_x), contentDescription = "X", tint = Color(0xFF181A2A), modifier = Modifier.size(24.dp))
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

        if (selectedParqueadero != null) {
            Box(modifier = Modifier.zIndex(10f)) {
                ParqueaderoInfoCard(
                    parqueadero = selectedParqueadero!!, 
                    onDismiss = { 
                        selectedParqueadero = null
                        mapView.overlays.removeAll { it is Polyline }
                        mapView.invalidate()
                    },
                    onRouteClick = {
                        val startPoint = locationState.value?.let { GeoPoint(it.latitude, it.longitude) }
                        val endPoint = selectedParqueadero!!.latitud?.let { lat -> selectedParqueadero!!.longitud?.let { lon -> GeoPoint(lat, lon) } }
                        if (startPoint != null && endPoint != null) {
                            drawRoute(startPoint, endPoint)
                            selectedParqueadero = null
                        } else {
                            Toast.makeText(context, "Ubicación no disponible para calcular la ruta", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onValoracionesClick = { // Acción añadida
                        navController.navigate("cliente_valoraciones/${selectedParqueadero!!.id}")
                    }
                )
            }
        }

        if (isRouteLoading) {
            Box(modifier = Modifier.zIndex(11f)) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
