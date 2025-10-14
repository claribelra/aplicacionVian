package com.example.aplicacionvianapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacionvianapp.LoginState
import com.example.aplicacionvianapp.LoginViewModel
import com.example.aplicacionvianapp.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit = {},
    viewModelStoreOwner: ViewModelStoreOwner
) {
    val viewModel: LoginViewModel = viewModel(viewModelStoreOwner)
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorCampos by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Fondo con esquinas redondeadas arriba
        Surface(
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Iniciar sesión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Inicia sesión para darte los mejores parqueaderos\ncerca de ti",
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                // TextField usuario
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("Nombre completo", color = Color(0xFF8A8A8A)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF5CA8FF),
                        focusedBorderColor = Color(0xFF5CA8FF),
                        cursorColor = Color(0xFF5CA8FF)
                    ),
                    textStyle = TextStyle(fontSize = 18.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // TextField contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Contraseña", color = Color(0xFF8A8A8A)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF5CA8FF),
                        focusedBorderColor = Color(0xFF5CA8FF),
                        cursorColor = Color(0xFF5CA8FF)
                    ),
                    textStyle = TextStyle(fontSize = 18.sp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Botón Iniciar
                Button(
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            errorCampos = "Completa los campos"
                        } else {
                            errorCampos = ""
                            viewModel.login(username, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Iniciar", fontSize = 24.sp, color = Color.White)
                }
                if (errorCampos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorCampos, color = MaterialTheme.colorScheme.error)
                }
                when (loginState) {
                    is LoginState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is LoginState.SuccessCliente -> {
                        LaunchedEffect(Unit) { onLoginSuccess() }
                    }
                    is LoginState.SuccessNoCliente -> {
                        errorCampos = "Solo los clientes pueden iniciar sesión"
                    }
                    is LoginState.Error -> {
                        errorCampos = (loginState as LoginState.Error).message
                    }
                    else -> {}
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Aún no estas registrado?",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CA8FF))
                ) {
                    Text("Crear cuenta", fontSize = 24.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                // Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.logo_vianapp),
                        contentDescription = "Logo Vianapp",
                        modifier = Modifier.size(48.dp)
                    )
                    // Redes sociales
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Redes sociales", color = Color(0xFF1976D2), fontSize = 14.sp)
                        Row {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_facebook),
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_instagram),
                                contentDescription = null,
                                tint = Color(0xFFEA4C89),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_x),
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    // Teléfono
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Teléfono", color = Color(0xFF1976D2), fontSize = 14.sp)
                        Text("0000000", color = Color.Black, fontSize = 14.sp)
                    }
                    // Correo
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Correo", color = Color(0xFF1976D2), fontSize = 14.sp)
                        Text("viANApp@gmail.com", color = Color.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
