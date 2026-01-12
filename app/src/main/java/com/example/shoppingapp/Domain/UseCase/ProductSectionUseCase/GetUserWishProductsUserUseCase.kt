package com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase

import com.example.shoppingapp.Domain.repo.Repo
import javax.inject.Inject

class GetUserWishProductsUserUseCase (private val repo: Repo){
    suspend fun getUserWishProducts() = repo.getAllWishProductsOfUser()
}