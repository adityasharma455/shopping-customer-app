package com.example.shoppingapp.Domain.UseCase.BuyProductUserUseCase

import com.example.shoppingapp.Domain.repo.Repo
import javax.inject.Inject

class CancelOrderUserUseCase (private val repo: Repo) {
    suspend fun cancelOrderUserUseCase(orderId: String)= repo.cancelOrder(orderId)
}