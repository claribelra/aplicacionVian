package com.example.aplicacionvianapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarifasScreen() {
    var tarifaHora by remember { mutableStateOf("5000") }
    var tarifaDia by remember { mutableStateOf("30000") }
    var cuposDisponibles by remember { mutableStateOf("10") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tarifa / hora", modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = tarifaHora,
                onValueChange = { tarifaHora = it },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tarifa / día", modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = tarifaDia,
                onValueChange = { tarifaDia = it },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Cupos disponibles", modifier = Modifier.weight(1f))
            OutlinedTextField(
                value = cuposDisponibles,
                onValueChange = { cuposDisponibles = it },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { /* TODO: Guardar cambios */ }) {
            Text("Guardar cambios")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarifasScreenPreview() {
    TarifasScreen()
}
