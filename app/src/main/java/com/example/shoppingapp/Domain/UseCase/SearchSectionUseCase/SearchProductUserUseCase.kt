package com.example.shoppingapp.Domain.UseCase.SearchSectionUseCase

import com.example.shoppingapp.Domain.repo.Repo
import javax.inject.Inject

class SearchProductUserUseCase (private val repo: Repo) {
    suspend fun searchProductUserUseCase(SearchQuery: String) = repo.searchProduct(SearchQuery)
}