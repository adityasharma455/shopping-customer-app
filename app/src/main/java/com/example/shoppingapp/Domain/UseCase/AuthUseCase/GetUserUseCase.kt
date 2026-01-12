package com.example.shoppingapp.Domain.UseCase.AuthUseCase

import com.example.shoppingapp.Domain.repo.Repo

class GetUserUseCase(private val repo: Repo) {
    suspend fun getUserUseCase() = repo.getUser()
}