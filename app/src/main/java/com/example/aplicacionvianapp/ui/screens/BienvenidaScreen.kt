package com.example.aplicacionvianapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import com.example.aplicacionvianapp.R
import com.example.aplicacionvianapp.ui.components.BottomBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.rotate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.aplicacionvianapp.ThemeViewModel

@Composable
fun BienvenidaScreen(themeViewModel: ThemeViewModel) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            painter = painterResource(id = R.drawable.logo_vianapp),
            contentDescription = "Logo Vianapp",
            modifier = Modifier.size(250.dp)
        )
        Text(
            text = "Aparca con\nSeguridad",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            Canvas(modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomCenter)) {
                drawCircle(
                    color = primaryColor,
                    radius = size.width / 2
                )
            }
            Image(
                painter = painterResource(id = R.drawable.celular1),
                contentDescription = "Celular 1",
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = (-40).dp, y = 20.dp)
                    .rotate(-15f)
            )
            Image(
                painter = painterResource(id = R.drawable.celular2),
                contentDescription = "Celular 2",
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = 40.dp, y = 0.dp)
                    .rotate(10f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            BeneficioItem("Reserva tu parqueadero en segundos")
            BeneficioItem("Seguridad validada para todos los usuarios")
            BeneficioItem("Herramienta exclusiva para propiedades horizontales")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Bienvenido!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))
        BottomBar()
    }
}

@Composable
fun BeneficioItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
    }
    Spacer(modifier = Modifier.height(8.dp))
}
