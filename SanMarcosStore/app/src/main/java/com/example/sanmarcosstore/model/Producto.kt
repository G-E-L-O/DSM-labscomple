package com.example.sanmarcosstore.model

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: String,
    val categoria: String,
    val favorito: Boolean = false
)

val productosDePrueba = listOf(
    Producto(1, "Cafe Premium 500g", "S/ 45.00", "Bebidas", true),
    Producto(2, "Chocolate artesanal 70%", "S/ 28.00", "Dulces"),
    Producto(3, "Pan de masa madre", "S/ 18.00", "Panaderia"),
    Producto(4, "Miel de abeja organica", "S/ 35.00", "Endulzantes", true),
    Producto(5, "Mermelada de fresa", "S/ 22.00", "Conservas"),
    Producto(6, "Te verde matcha", "S/ 55.00", "Bebidas"),
    Producto(7, "Galletas de avena", "S/ 15.00", "Dulces"),
    Producto(8, "Aceite de oliva extra virgen", "S/ 68.00", "Aceites"),

    Producto(9, "Mantequilla de almendras 250g", "S/ 38.00", "Snacks", true),
    Producto(10, "Granola artesanal con quinoa", "S/ 24.00", "Cereales"),
    Producto(11, "Kombucha de frutos rojos 350ml", "S/ 13.00", "Bebidas", true)
)
