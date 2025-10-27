package com.example.aplicacionvianapp

data class Profile(
    val id: Int,
    val username: String, // Campo añadido
    val rol: String,
    val nombres: String,
    val apellidos: String,
    val telefono: String,
    val genero: String,
    val cedula: String,
    val departamento: String,
    val municipio: String,
    val placa: String,
    val tarjeta: Int?,
    val user: Int,
    val parqueaderoprivado: Int?
)
