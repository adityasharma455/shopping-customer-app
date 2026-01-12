package com.example.shoppingapp.Domain.repo

import com.example.shoppingapp.Common.ResultState
import com.example.shoppingapp.Domain.Models.BannerDataModel
import com.example.shoppingapp.Domain.Models.CategoryDataModel
import com.example.shoppingapp.Domain.Models.OrderDataModel
import com.example.shoppingapp.Domain.Models.ProductDataModel
import com.example.shoppingapp.Domain.Models.UserDataModel
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun registerUserWithEmailAndPassword(UserData: UserDataModel) : Flow<ResultState<String>>

    fun signInUserWithEmailAndPassword(UserData: UserDataModel): Flow<ResultState<String>>

    fun checkUserStatus() : Flow<ResultState<String>>

    fun UserSignOut(): Flow<ResultState<String>>

    fun getUser(): Flow<ResultState<UserDataModel>>

    fun UpdateUserData(UserData: UserDataModel): Flow<ResultState<String>>

    fun getAllCategory() : Flow<ResultState<List<CategoryDataModel>>>

    fun getAllProducts(): Flow<ResultState<List<ProductDataModel>>>

     fun getSpecificProduct(ProductId: String): Flow<ResultState<ProductDataModel>>

    fun getProductByCategory(categoryName : String): Flow<ResultState<List<ProductDataModel>>>

     fun toggleWishList(product: ProductDataModel) : Flow<ResultState<Boolean>>

     fun isItemInWishList(productId: String) : Flow<ResultState<Boolean>>

     fun getAllWishProductsOfUser(): Flow<ResultState<List<ProductDataModel>>>

     fun getAllBannerModels(): Flow<ResultState<List<BannerDataModel>>>

     fun searchProduct(SearchQuery: String) : Flow<ResultState<List<ProductDataModel>>>

    fun addProductToCart(product: ProductDataModel) : Flow<ResultState<Boolean>>

    fun removeProductFromCart(product: ProductDataModel) : Flow<ResultState<Boolean>>

    fun getProductsFromCart(): Flow<ResultState<List<ProductDataModel>>>

    fun createOrder(order: OrderDataModel): Flow<ResultState<String>>

    fun getUserOrders(): Flow<ResultState<List<OrderDataModel>>>
    fun clearCart(): Flow<ResultState<Boolean>>

    fun cancelOrder(orderId: String): Flow<ResultState<Boolean>>




}