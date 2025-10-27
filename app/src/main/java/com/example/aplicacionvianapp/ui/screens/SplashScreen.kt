package com.example.aplicacionvianapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aplicacionvianapp.R
import com.example.aplicacionvianapp.ui.theme.Green
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, startDestination: String) {
    // Navigate after a delay
    LaunchedEffect(key1 = true) {
        delay(4000L) // 4 second delay
        // Navigate to the appropriate screen and remove the splash screen from the back stack
        navController.navigate(startDestination) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_vianapp),
                contentDescription = "Vianapp Logo",
                modifier = Modifier.height(60.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Aparca con Seguridad",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Placeholder for the main image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Imagen de teléfonos")
            }

            Spacer(modifier = Modifier.height(24.dp))

            FeatureRow("Reserva tu parqueadero en segundos")
            FeatureRow("Seguridad validada para todos los usuarios")
            FeatureRow("Herramienta exclusiva para propiedades horizontales")

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "¡Bienvenido!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FeatureRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Green
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 14.sp)
    }
}
