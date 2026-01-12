package com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase

import com.example.shoppingapp.Domain.repo.Repo
import javax.inject.Inject

class IsItemInWishList (private val repo: Repo){
    suspend fun isItemInWishList(productId: String) = repo.isItemInWishList(productId)
}