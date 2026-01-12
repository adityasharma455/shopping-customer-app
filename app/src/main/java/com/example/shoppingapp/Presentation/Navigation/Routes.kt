package com.example.shoppingapp.Presentation.Navigation

import kotlinx.serialization.Serializable


sealed class SubNavigation{
    @Serializable
    object LogInSignUpScreenRoutes: SubNavigation()

    @Serializable
    object HomeProductScreenRoutes : SubNavigation()

    @Serializable
    object ProfileScreenRoutes : SubNavigation()


}
sealed class Routes {

    @Serializable
    object LoginScreenRoutes

    @Serializable
    object SignUpScreenRoutes

    @Serializable
    object HomeScreenRoutes

    @Serializable
    object AllProductsScreenRoutes

    @Serializable
    object AllCategoriesScreenRoutes

    @Serializable
    object CartScreenRoutes

    @Serializable
    object WishListScreenRoutes

    @Serializable
    object ProfileScreenRoutes

    @Serializable
    data class AllProductByCategoryScreenRoutes(
        val CategoryName: String
    )
    @Serializable
    data class BuyNowScreenRoutes(
        val ProductId : String

    )

    @Serializable
    data class AllSearchProductsScreenRoutes(
        val SearchQuery: String
    )

    @Serializable
    data class EachItemScreenRoutes(
        val ProductId: String
    )
    @Serializable
    object EditAdreessScreenRoutes

    @Serializable
    object BuyCartProductScreenRoutes

    @Serializable
    object OrdersScreenRoutes

}