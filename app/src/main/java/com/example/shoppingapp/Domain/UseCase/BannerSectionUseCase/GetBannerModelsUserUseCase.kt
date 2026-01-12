package com.example.shoppingapp.Domain.UseCase.BannerSectionUseCase

import com.example.shoppingapp.Domain.repo.Repo
import javax.inject.Inject

class GetBannerModelsUserUseCase (private val repo: Repo) {
    suspend fun getBannerModelUserUseCase() = repo.getAllBannerModels()
}