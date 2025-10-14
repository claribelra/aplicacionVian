package com.example.aplicacionvianapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.aplicacionvianapp.ui.components.BottomBar
import com.example.aplicacionvianapp.ui.components.ButtonLogin

@Composable
fun RegistroScreen(onIniciarSesion: () -> Unit) {
    var paso = remember { mutableStateOf(1) }
    var nombres = remember { mutableStateOf("") }
    var apellidos = remember { mutableStateOf("") }
    var email = remember { mutableStateOf("") }
    var telefono = remember { mutableStateOf("") }
    var genero = remember { mutableStateOf("") }
    var cedula = remember { mutableStateOf("") }
    var departamento = remember { mutableStateOf("") }
    var municipio = remember { mutableStateOf("") }
    var tarjetaPropiedad = remember { mutableStateOf("") }
    var usuario = remember { mutableStateOf("") }
    var contrasena = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Registrarse",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF181A2A)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Regístrate para obtener descuentos, notificaciones\ny los parqueaderos más libres para ti",
            fontSize = 18.sp,
            color = Color(0xFF181A2A)
        )
        Spacer(modifier = Modifier.height(32.dp))
        if (paso.value == 1) {
            RegistroTextField("Nombres", nombres.value) { nombres.value = it }
            Spacer(modifier = Modifier.height(16.dp))
            RegistroTextField("Apellidos", apellidos.value) { apellidos.value = it }
            Spacer(modifier = Modifier.height(16.dp))
            RegistroTextField("Email", email.value) { email.value = it }
            Spacer(modifier = Modifier.height(16.dp))
            RegistroTextField("Teléfono", telefono.value) { telefono.value = it }
            Spacer(modifier = Modifier.height(16.dp))
            RegistroTextField("Género", genero.value) { genero.value = it }
            Spacer(modifier = Modifier.height(16.dp))
            RegistroTextField("Cédula", cedula.value) { cedula.value = it }
            Spacer(modifier = Modifier.height(32.dp))
            ButtonLogin(text = "Siguiente", color = Color(0xFF5CA8FF), modifier = Modifier) {
                paso.value = 2
            }
        } else {
            RegistroTextField("Departamento", departamento.value) { departamento.value = it }
            Spacer(modifier = Modifier.height(16.dp))
            RegistroTextField("Municipio", municipio.value) { municipio.value = it }
            Spacer(modifier = Modifier.height(16.dp))
            RegistroTextField("Tarjeta de propiedad", tarjetaPropiedad.value) { tarjetaPropiedad.value = it }
            Spacer(modifier = Modifier.height(16.dp))
            RegistroTextField("Usuario", usuario.value) { usuario.value = it }
            Spacer(modifier = Modifier.height(16.dp))
            RegistroTextField("Contraseña", contrasena.value) { contrasena.value = it }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonLogin(
                    text = "Anterior",
                    color = Color(0xFF5CA8FF),
                    modifier = Modifier.weight(0.45f),
                    onClick = { paso.value = 1 }
                )
                Spacer(modifier = Modifier.width(16.dp))
                ButtonLogin(
                    text = "Crear cuenta",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(0.55f),
                    onClick = { /* Aquí iría la lógica de registro */ }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Ya tienes una?",
                fontSize = 20.sp,
                color = Color(0xFF181A2A)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ButtonLogin(text = "Iniciar sesión", color = Color(0xFF5CA8FF), modifier = Modifier, onClick = onIniciarSesion)
        }
        Spacer(modifier = Modifier.height(48.dp))
        BottomBar()
    }
}

@Composable
fun RegistroTextField(hint: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = Color(0xFF757575), fontSize = 20.sp) },
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(60.dp),
        shape = RoundedCornerShape(30.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color(0xFF5CA8FF),
            unfocusedIndicatorColor = Color(0xFF5CA8FF),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}
