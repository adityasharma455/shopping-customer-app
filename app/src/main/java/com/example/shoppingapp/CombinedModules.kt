package com.example.shoppingapp

import com.example.shoppingapp.Data.DI.dataModule
import com.example.shoppingapp.Domain.Di.domainModule
import com.example.shoppingapp.Presentation.DI.presentationModule

val CombinedModules = listOf(
        dataModule,
        domainModule,
        presentationModule
)