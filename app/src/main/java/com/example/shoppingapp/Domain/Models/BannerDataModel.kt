package com.example.shoppingapp.Domain.Models

data class BannerDataModel (
    val name: String? = "",
    val imageUrl: String? = "",
    val date: Long = System.currentTimeMillis()
)