package com.example.catalogo

// 1. Definimos la estructura de un Producto
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String
)