package com.example.shoppingapp.Presentation.DI


import com.example.shoppingapp.Presentation.ViewModel.MyViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel<MyViewModel> { MyViewModel(
         AuthUserUseCase= get(),
         getAllCategoriesUseCase= get(),
         getAllProductsUseCase= get(),
         toggleWishListUserUseCase= get(),
         isItemInWishList= get(),
         getUserUseCase = get(),
         cancelOrderUserUseCase= get(),
         updateUserDataUseCase= get(),
         createOrderUseCase= get(),
         getUserOrdersUseCase= get(),
         clearCartUseCase= get(),
         getAllBannerModelsUserUseCase= get(),
         getProductsByCategoryUseCase= get(),
         getUserWishProduct= get(),
         searchProductUserUseCase= get(),
         addProductToCartUserUseCase= get(),
         removeProductFromCartUserUseCase= get(),
         getAllCartProductsUserUseCase= get()
    ) }
}