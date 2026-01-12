package com.example.shoppingapp.Data.DI

import com.example.shoppingapp.Data.RepoImplementation.RepoImple
import com.example.shoppingapp.Domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.dsl.module

val dataModule = module {

    //Firebase
    single<FirebaseFirestore>{ FirebaseFirestore.getInstance() }
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    single<FirebaseMessaging> { FirebaseMessaging.getInstance() }

    //Repo
    single<Repo> { RepoImple(
        firestore = get(), firebaseAuth = get(),
        firebaseMessaging = get()
    ) }


}