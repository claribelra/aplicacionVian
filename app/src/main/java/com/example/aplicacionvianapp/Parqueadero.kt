package com.example.aplicacionvianapp

data class Parqueadero(
    val id: Int,
    val nombre_comercial: String,
    val direccion: String,
    val espacios: Int,
    val tarifa_hora: Int,
    val tarifa_dia: Int,
    val latitud: Double?,
    val longitud: Double?
    // ...otros campos si los tienes
)
