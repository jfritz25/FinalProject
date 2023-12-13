package com.example.finalproject

import com.google.firebase.firestore.GeoPoint

data class Restaurant(
    val name: String,
    val items: List<String>,
    val prices: List<String>,
    val location: GeoPoint,
    val usersFav: List<String>
)
