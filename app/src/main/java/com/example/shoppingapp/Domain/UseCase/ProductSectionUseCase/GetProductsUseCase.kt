package com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase

import com.example.shoppingapp.Domain.repo.Repo
import javax.inject.Inject

class GetProductsUseCase (private val repo: Repo) {
    suspend fun getAllProductsUseCase() = repo.getAllProducts()

    suspend fun getSpecificProductUseCase(ProductId: String) = repo.getSpecificProduct(ProductId)
}