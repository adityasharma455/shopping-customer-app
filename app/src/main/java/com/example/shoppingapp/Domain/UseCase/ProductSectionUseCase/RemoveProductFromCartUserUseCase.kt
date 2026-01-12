package com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase

import com.example.shoppingapp.Domain.Models.ProductDataModel
import com.example.shoppingapp.Domain.repo.Repo
import javax.inject.Inject

class RemoveProductFromCartUserUseCase (private val repo: Repo){
    suspend fun removeProductFromCart(product: ProductDataModel) = repo.removeProductFromCart(product)
}