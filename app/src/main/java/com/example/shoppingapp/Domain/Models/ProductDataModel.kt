package com.example.shoppingapp.Domain.Models

data class ProductDataModel(
    val name: String = "",
    val price: String = "",
    val finalprice: String = "",
    val description: String = "",
    val image: String? = "",
    val category: String = "",
    var productID: String ="",
    val availabelUnits: String = "",
    val BrandName: String = ""
)
