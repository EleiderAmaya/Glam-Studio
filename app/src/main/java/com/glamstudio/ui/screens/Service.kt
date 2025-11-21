package com.glamstudio.ui.screens

data class Service(
    val id: String, // Para identificarlo de forma única
    val name: String,
    val description: String,
    val durationInMinutes: Int,
    val price: Double
)