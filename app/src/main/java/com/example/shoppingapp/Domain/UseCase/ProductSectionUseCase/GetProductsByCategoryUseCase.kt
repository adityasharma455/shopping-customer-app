package com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase

import com.example.shoppingapp.Domain.repo.Repo
import javax.inject.Inject

class GetProductsByCategoryUseCase (private val repo: Repo) {

    suspend fun getProductsByCategory(category: String) = repo.getProductByCategory(category)
}