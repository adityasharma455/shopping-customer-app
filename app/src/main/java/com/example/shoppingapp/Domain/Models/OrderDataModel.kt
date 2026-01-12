package com.example.shoppingapp.Domain.Models

data class OrderDataModel(
    val orderId: String = "",
    val userId: String = "",
    val products: List<ProductDataModel> = emptyList(),
    val shippingAddress: String = "",
    val customerName: String = "",
    val phoneNumber: String = "",
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val orderDate: String = "",
    val orderStatus: String = "Pending", // Pending, Confirmed, Shipped, Delivered
    val paymentMethod: String = "Cash on Delivery"
)