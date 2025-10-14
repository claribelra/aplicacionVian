package com.example.aplicacionvianapp

data class Opinion(
    val id: Int,
    val nombreParqueadero: String,
    val estrellas: Int,
    val comentario: String,
    val imagenResId: Int?,
    val usuario: Int
)

