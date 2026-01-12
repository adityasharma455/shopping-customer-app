package com.example.shoppingapp.Domain.UseCase.BuyProductUserUseCase

import com.example.shoppingapp.Common.ResultState
import com.example.shoppingapp.Domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClearCartUseCase (private val repo: Repo) {
    suspend fun clearCart(): Flow<ResultState<Boolean>> = repo.clearCart()
}