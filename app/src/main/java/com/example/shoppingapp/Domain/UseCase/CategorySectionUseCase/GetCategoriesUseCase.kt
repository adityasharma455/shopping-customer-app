package com.example.shoppingapp.Domain.UseCase.CategorySectionUseCase

import com.example.shoppingapp.Domain.repo.Repo
import javax.inject.Inject

class GetCategoriesUseCase (private val repo: Repo) {
    suspend fun getAllCategoriesUseCase() = repo.getAllCategory()
}