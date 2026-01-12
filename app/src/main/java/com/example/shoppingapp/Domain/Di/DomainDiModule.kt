package com.example.shoppingapp.Domain.Di


import com.example.shoppingapp.Domain.UseCase.AuthUseCase.CreateUserUseCase
import com.example.shoppingapp.Domain.UseCase.AuthUseCase.GetUserUseCase
import com.example.shoppingapp.Domain.UseCase.AuthUseCase.UpdateUserDataUseCase
import com.example.shoppingapp.Domain.UseCase.BannerSectionUseCase.GetBannerModelsUserUseCase
import com.example.shoppingapp.Domain.UseCase.BuyProductUserUseCase.CancelOrderUserUseCase
import com.example.shoppingapp.Domain.UseCase.BuyProductUserUseCase.ClearCartUseCase
import com.example.shoppingapp.Domain.UseCase.BuyProductUserUseCase.CreateOrderUseCase
import com.example.shoppingapp.Domain.UseCase.BuyProductUserUseCase.GetUserOrdersUseCase
import com.example.shoppingapp.Domain.UseCase.CategorySectionUseCase.GetCategoriesUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.AddProductToCartUserUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.GetAllCartProductsUserUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.GetProductsByCategoryUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.GetProductsUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.GetUserWishProductsUserUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.IsItemInWishList
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.RemoveProductFromCartUserUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.ToggleWishListUserUseCase
import com.example.shoppingapp.Domain.UseCase.SearchSectionUseCase.SearchProductUserUseCase
import org.koin.dsl.module

val domainModule = module {

    factory { CreateUserUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory { UpdateUserDataUseCase(get()) }
    factory { GetBannerModelsUserUseCase(get()) }
    factory { CancelOrderUserUseCase(get()) }
    factory { ClearCartUseCase(get()) }
    factory { CreateOrderUseCase(get()) }
    factory { GetUserOrdersUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { AddProductToCartUserUseCase(get()) }
    factory { GetAllCartProductsUserUseCase(get()) }
    factory { GetProductsByCategoryUseCase(get()) }
    factory { GetProductsUseCase(get()) }
    factory { GetUserWishProductsUserUseCase(get()) }
    factory { IsItemInWishList(get()) }
    factory { RemoveProductFromCartUserUseCase(get()) }
    factory { ToggleWishListUserUseCase(get()) }
    factory { SearchProductUserUseCase(get()) }


}