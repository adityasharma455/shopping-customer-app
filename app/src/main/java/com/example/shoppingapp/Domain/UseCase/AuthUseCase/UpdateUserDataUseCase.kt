package com.example.shoppingapp.Domain.UseCase.AuthUseCase

import com.example.shoppingapp.Domain.Models.UserDataModel
import com.example.shoppingapp.Domain.repo.Repo
import javax.inject.Inject

class UpdateUserDataUseCase (private val repo: Repo) {
    suspend fun updateUserDataUseCase(UserData: UserDataModel) = repo.UpdateUserData(UserData)
}