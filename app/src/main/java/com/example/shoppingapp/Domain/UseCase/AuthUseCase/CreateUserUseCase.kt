package com.example.shoppingapp.Domain.UseCase.AuthUseCase

import com.example.shoppingapp.Domain.Models.UserDataModel
import com.example.shoppingapp.Domain.repo.Repo
import javax.inject.Inject

class CreateUserUseCase(private val repo: Repo) {
    suspend fun createUserUseCase(
        UserData : UserDataModel
    ) = repo.registerUserWithEmailAndPassword(UserData)

    suspend fun loginUserUserCase(
        UserData: UserDataModel
    ) = repo.signInUserWithEmailAndPassword(UserData)

    suspend fun userCheckStatusUseCase()  = repo.checkUserStatus()

    suspend fun UserSignOut() = repo.UserSignOut()

}