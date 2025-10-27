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
import androidx.navigation.NavHostController
import com.example.aplicacionvianapp.ThemeViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit = {},
    viewModelStoreOwner: ViewModelStoreOwner,
    themeViewModel: ThemeViewModel
) {
    val viewModel: LoginViewModel = viewModel(viewModelStoreOwner)
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorCampos by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Contenedor principal con fondo de superficie
        Surface(
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = MaterialTheme.colorScheme.surface,
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
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Inicia sesión para darte los mejores parqueaderos\ncerca de ti",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                // TextField usuario
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("Nombre completo", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // TextField contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Contraseña", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Este verde puede ser un color de marca
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
                    is LoginState.SuccessParqueadero -> {
                        val parqueadero = (loginState as LoginState.SuccessParqueadero).parqueadero
                        LaunchedEffect(parqueadero) {
                            navController.navigate("parqueaderoOwnerMap/${parqueadero.id}")
                        }
                    }
                    is LoginState.SuccessAdmin -> {
                        LaunchedEffect(Unit) {
                            navController.navigate("admin_panel") {
                                popUpTo(0) // Clear back stack
                            }
                        }
                    }
                    is LoginState.SuccessNoCliente -> {
                        errorCampos = "Solo los clientes o parqueaderos pueden iniciar sesión"
                    }
                    is LoginState.Error -> {
                        errorCampos = (loginState as LoginState.Error).message
                    }
                    else -> {}
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Aún no estas registrado?",
                    color = MaterialTheme.colorScheme.onSurface,
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Crear cuenta", fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary)
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
                        Text("Redes sociales", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                        Row {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_facebook),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_instagram),
                                contentDescription = null,
                                tint = Color(0xFFEA4C89), // Color de marca
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_x),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    // Teléfono
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Teléfono", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                        Text("0000000", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                    }
                    // Correo
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Correo", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                        Text("viANApp@gmail.com", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
