package com.example.shoppingapp.Domain.UseCase.BuyProductUserUseCase

import com.example.shoppingapp.Common.ResultState
import com.example.shoppingapp.Domain.Models.OrderDataModel
import com.example.shoppingapp.Domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserOrdersUseCase (private val repo: Repo) {
    suspend fun getUserOrders(): Flow<ResultState<List<OrderDataModel>>> = repo.getUserOrders()
}