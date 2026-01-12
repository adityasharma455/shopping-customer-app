package com.example.shoppingapp.Domain.Models

data class CategoryDataModel(
    val name: String = "",
    val date: Long = System.currentTimeMillis(),
    val categoryImage: String = ""
)
