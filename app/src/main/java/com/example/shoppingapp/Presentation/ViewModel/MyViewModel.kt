package com.example.shoppingapp.Presentation.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.Common.ResultState
import com.example.shoppingapp.Domain.Models.BannerDataModel
import com.example.shoppingapp.Domain.Models.CategoryDataModel
import com.example.shoppingapp.Domain.Models.OrderDataModel
import com.example.shoppingapp.Domain.Models.ProductDataModel
import com.example.shoppingapp.Domain.Models.UserDataModel
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.AddProductToCartUserUseCase
import com.example.shoppingapp.Domain.UseCase.AuthUseCase.CreateUserUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.GetAllCartProductsUserUseCase
import com.example.shoppingapp.Domain.UseCase.BannerSectionUseCase.GetBannerModelsUserUseCase
import com.example.shoppingapp.Domain.UseCase.CategorySectionUseCase.GetCategoriesUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.GetProductsByCategoryUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.GetProductsUseCase
import com.example.shoppingapp.Domain.UseCase.AuthUseCase.GetUserUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.GetUserWishProductsUserUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.IsItemInWishList
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.RemoveProductFromCartUserUseCase
import com.example.shoppingapp.Domain.UseCase.SearchSectionUseCase.SearchProductUserUseCase
import com.example.shoppingapp.Domain.UseCase.ProductSectionUseCase.ToggleWishListUserUseCase
import com.example.shoppingapp.Domain.UseCase.AuthUseCase.UpdateUserDataUseCase
import com.example.shoppingapp.Domain.UseCase.BuyProductUserUseCase.CancelOrderUserUseCase
import com.example.shoppingapp.Domain.UseCase.BuyProductUserUseCase.ClearCartUseCase
import com.example.shoppingapp.Domain.UseCase.BuyProductUserUseCase.CreateOrderUseCase
import com.example.shoppingapp.Domain.UseCase.BuyProductUserUseCase.GetUserOrdersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MyViewModel(
    private val AuthUserUseCase: CreateUserUseCase,
    private val getAllCategoriesUseCase: GetCategoriesUseCase,
    private val getAllProductsUseCase: GetProductsUseCase,
    private val toggleWishListUserUseCase: ToggleWishListUserUseCase,
    private val isItemInWishList: IsItemInWishList,
    private val getUserUseCase : GetUserUseCase,
    private val cancelOrderUserUseCase: CancelOrderUserUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val getUserOrdersUseCase: GetUserOrdersUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val getAllBannerModelsUserUseCase: GetBannerModelsUserUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val getUserWishProduct: GetUserWishProductsUserUseCase,
    private val searchProductUserUseCase: SearchProductUserUseCase,
    private val addProductToCartUserUseCase: AddProductToCartUserUseCase,
    private val removeProductFromCartUserUseCase: RemoveProductFromCartUserUseCase,
    private val getAllCartProductsUserUseCase: GetAllCartProductsUserUseCase
): ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Uninitialized)
    val authState = _authState.asStateFlow()

    // Add these with your other StateFlow declarations
    private val _createOrderState = MutableStateFlow<CreateOrderScreen>(CreateOrderScreen.Idle)
    val createOrderState: StateFlow<CreateOrderScreen> = _createOrderState

    private val _getUserOrdersState = MutableStateFlow(GetUserOrdersScreen())
    val getUserOrdersState = _getUserOrdersState.asStateFlow()

    private val _clearCartState = MutableStateFlow(ClearCartScreen())
    val clearCartState = _clearCartState.asStateFlow()

    private val _getUserState = MutableStateFlow(GetUserScreen())
    val getUserState = _getUserState.asStateFlow()

    private val _updateUserDataState = MutableStateFlow(UpdateUserDataScreen())
    val updateUserDataState = _updateUserDataState.asStateFlow()

    private val _getAllCategoriesState = MutableStateFlow(GetAllCategories())
    val getAllCategoriesState = _getAllCategoriesState.asStateFlow()

    // Safe pattern - UI can't modify state
    private val _getAllProductsState = MutableStateFlow(GetAllProducts())
    val getAllProductsState = _getAllProductsState.asStateFlow()

    private val _getSpecificProductState = MutableStateFlow(GetSpecificProduct())
    val getSpecificProductState = _getSpecificProductState.asStateFlow()

    private val _getHomedataState = MutableStateFlow(LoadHomeScreen())
    val getHomedataState = _getHomedataState.asStateFlow()

    // In your ViewModel
    private val _wishListState = mutableStateMapOf<String, WishListState>()

    // Create a StateFlow from the mutableStateMap
    private val _wishListStateFlow = MutableStateFlow(_wishListState.toMap())
    val wishListState: StateFlow<Map<String, WishListState>> = _wishListStateFlow.asStateFlow()

    private val _getProductsByCategoryState = MutableStateFlow(GetProductsByCategoryScreen())
    val getProductsByCategoryState = _getProductsByCategoryState.asStateFlow()

    private val _getUserWishProductsState = MutableStateFlow(GetUserWishProductsScreen())
    val getUserWishProductsState = _getUserWishProductsState.asStateFlow()


    private val _getBannerModelsState = MutableStateFlow(GetBannerModelsScreenState())
    val getBannerModelsState = _getBannerModelsState.asStateFlow()

    private val _searchProductState = MutableStateFlow(SearchProductScreen())
    val searchProductState = _searchProductState.asStateFlow()

    private val _addProductToCartState = MutableStateFlow(AddProductToCartScreen())
    val addProductToCartState = _addProductToCartState.asStateFlow()

    private val _removeProductFromCartState = MutableStateFlow(RemoveProductFromCartScreen())
    val removeProductFromCartState = _removeProductFromCartState.asStateFlow()

    private val _getAllCartProductsState = MutableStateFlow(GetAllCartProductsScreenState())
    val getAllCartProductsState = _getAllCartProductsState.asStateFlow()

    // In your ViewModel class - add these with your other StateFlow declarations
    private val _cancelOrderState = MutableStateFlow(CancelOrderScreen())
    val cancelOrderState = _cancelOrderState.asStateFlow()

    val _SearchQuery = MutableStateFlow("")


    init {
        loadHomeData()
        searchQuery()
        getUser()
    }
    private val TAG = "OrdersViewModel"


    fun loadHomeData() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                getAllCategoriesUseCase.getAllCategoriesUseCase(),
                getAllProductsUseCase.getAllProductsUseCase(),
                getAllBannerModelsUserUseCase.getBannerModelUserUseCase()
            ) { categoryState, productState, BannerModelState ->
                when {
                    categoryState is ResultState.Success<List<CategoryDataModel>> &&
                            productState is ResultState.Success<List<ProductDataModel>> &&
                            BannerModelState is ResultState.Success<List<BannerDataModel>> -> {
                        LoadHomeScreen(
                            isLoading = false,
                            isSuccessProduct = productState.data,
                            isSuccessCategory = categoryState.data,
                            isSuccessBannerModel = BannerModelState.data

                        )
                    }

                    categoryState is ResultState.Error -> {
                        LoadHomeScreen(
                            isLoading = false,
                            Error = categoryState.message.toString()
                        )

                    }

                    productState is ResultState.Error -> {
                        LoadHomeScreen(
                            isLoading = false,
                            Error = productState.message.toString()
                        )
                    }

                    BannerModelState is ResultState.Error -> {
                        LoadHomeScreen(
                            isLoading = false,
                            Error = BannerModelState.message.toString()
                        )
                    }

                    else -> {
                        LoadHomeScreen(
                            isLoading = true
                        )
                    }
                }


            }.collect { state ->
                _getHomedataState.value = state
            }
        }
    }

    fun UserSignOut() {
        viewModelScope.launch(Dispatchers.IO) {
            _authState.value = AuthState.Loading
            AuthUserUseCase.UserSignOut().collectLatest {
                _authState.value = when (it) {
                    is ResultState.Success -> AuthState.SignedOut
                    is ResultState.Error -> AuthState.Error(it.message.toString())
                    else -> AuthState.Uninitialized
                }
            }
        }
    }

    fun createUser(UserData: UserDataModel) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            AuthUserUseCase.createUserUseCase(UserData).collectLatest {
                _authState.value = when (it) {
                    is ResultState.Success -> AuthState.SignUpSuccess(it.data)
                    is ResultState.Error -> AuthState.Error(it.message.toString())
                    else -> AuthState.Uninitialized
                }
            }


        }
    }

    fun logInUser(UserData: UserDataModel) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            AuthUserUseCase.loginUserUserCase(UserData).collectLatest {
                _authState.value = when (it) {
                    is ResultState.Success -> AuthState.LoginSuccess(it.data)
                    is ResultState.Error -> AuthState.Error(it.message.toString())
                    else -> AuthState.Uninitialized
                }
            }
        }
    }

    fun getUser() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserUseCase.getUserUseCase().collectLatest {
                when (it) {
                    is ResultState.Loading -> {
                        _getUserState.value = GetUserScreen(isLoading = true)
                    }

                    is ResultState.Success<UserDataModel> -> {
                        _getUserState.value = GetUserScreen(
                            isSuccess = it.data,
                            isLoading = false
                        )
                    }

                    is ResultState.Error -> {
                        _getUserState.value =
                            GetUserScreen(Error = it.message.toString(), isLoading = false)
                    }
                }
            }
        }
    }

    fun updateUserData(UserData: UserDataModel) {
        viewModelScope.launch {
            updateUserDataUseCase.updateUserDataUseCase(UserData).collectLatest {
                when (it) {
                    is ResultState.Loading -> {
                        _updateUserDataState.value = UpdateUserDataScreen(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _updateUserDataState.value = UpdateUserDataScreen(
                            isSuccess = it.data as String?, isLoading = false
                        )
                    }

                    is ResultState.Error -> {
                        _updateUserDataState.value = UpdateUserDataScreen(
                            Error = it.message.toString(), isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun getAllCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllCategoriesUseCase.getAllCategoriesUseCase().collectLatest {
                Log.d("Categories", "all categories ${it}")
                when (it) {
                    is ResultState.Loading -> {
                        _getAllCategoriesState.value = GetAllCategories(isLoading = true)
                    }

                    is ResultState.Success<List<CategoryDataModel>> -> {
                        Log.d("AtSuccessCategories", "all categories ${it}")

                        _getAllCategoriesState.value =
                            GetAllCategories(isSuccess = it.data, isLoading = false)
                    }

                    is ResultState.Error -> {
                        _getAllCategoriesState.value =
                            GetAllCategories(Error = it.message.toString(), isLoading = false)
                    }
                }
            }
        }
    }

    fun getAllProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllProductsUseCase.getAllProductsUseCase().collectLatest {
                when (it) {
                    is ResultState.Loading -> {
                        _getAllProductsState.value = GetAllProducts(isLoading = true)
                    }

                    is ResultState.Success<List<ProductDataModel>> -> {
                        _getAllProductsState.value =
                            GetAllProducts(isSuccess = it.data, isLoading = false)
                    }

                    is ResultState.Error -> {
                        _getAllProductsState.value =
                            GetAllProducts(Error = it.message.toString(), isLoading = false)
                    }
                }
            }
        }
    }

    fun getSpecificProduct(ProductId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getAllProductsUseCase.getSpecificProductUseCase(ProductId)
                .collectLatest {
                    when (it) {
                        is ResultState.Loading -> {

                            _getSpecificProductState.value = GetSpecificProduct(isLoading = true)
                        }

                        is ResultState.Success<ProductDataModel> -> {

                            _getSpecificProductState.value =
                                GetSpecificProduct(isSuccess = it.data, isLoading = false)
                        }

                        is ResultState.Error -> {

                            _getSpecificProductState.value =
                                GetSpecificProduct(Error = it.message.toString(), isLoading = false)
                        }
                    }
                }
        }
    }

    // Simple toggle function
    fun toggleWishList(product: ProductDataModel) {
        val productId = product.productID

        viewModelScope.launch(Dispatchers.IO) {
            updateWishListState(productId, WishListState(isLoading = true))

            Log.d("WishlistVM", "Toggle product: $productId")


            toggleWishListUserUseCase.toggleWishList(product).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        updateWishListState(productId, WishListState(isLoading = true))
                    }

                    is ResultState.Success<Boolean> -> {
                        updateWishListState(
                            productId, WishListState(
                                isInWishList = result.data,
                                lastAction = if (result.data) "added" else "removed",
                                isLoading = false
                            )
                        )
                        Log.d("WishlistDebug", "Toggle success: isInWishList=${result.data}")

                    }

                    is ResultState.Error -> {
                        updateWishListState(
                            productId, WishListState(
                                Error = result.message.toString(),
                                isLoading = false
                            )
                        )
                    }
                }
            }
        }
    }

    // Helper function to update states and notify Flow
    private fun updateWishListState(productId: String, newState: WishListState) {
        _wishListState[productId] = newState
        _wishListStateFlow.value = _wishListState.toMap() // Update the flow
    }

    // Add these PUBLIC functions to your ViewModel
    fun clearWishListActionState(productId: String) {
        val currentState = _wishListState[productId] ?: WishListState()
        _wishListState[productId] = currentState.copy(lastAction = null, Error = null)
        _wishListStateFlow.value = _wishListState.toMap()
    }

    fun clearWishListErrorState(productId: String) {
        val currentState = _wishListState[productId] ?: WishListState()
        _wishListState[productId] = currentState.copy(Error = null)
        _wishListStateFlow.value = _wishListState.toMap()
    }

    fun checkIfItemInWishList(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateWishListState(productId, WishListState(isLoading = true))
            isItemInWishList.isItemInWishList(productId).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        updateWishListState(productId, WishListState(isLoading = true))
                    }

                    is ResultState.Success<Boolean> -> {
                        updateWishListState(
                            productId, WishListState(
                                isInWishList = result.data,
                                isLoading = false
                            )
                        )
                        Log.d("WishlistDebug", "Toggle success: isInWishList=${result.data}")
                    }

                    is ResultState.Error -> {
                        updateWishListState(
                            productId, WishListState(
                                Error = result.message.toString(),
                                isLoading = false
                            )
                        )
                    }
                }

            }
        }
    }

    fun getAllProductsByCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getProductsByCategoryUseCase.getProductsByCategory(category).collectLatest {
                when (it) {
                    is ResultState.Loading -> {
                        _getProductsByCategoryState.value =
                            GetProductsByCategoryScreen(isLoading = true)
                    }

                    is ResultState.Success<List<ProductDataModel>> -> {
                        _getProductsByCategoryState.value = GetProductsByCategoryScreen(
                            isSuccess = it.data,
                            isLoading = false
                        )
                    }

                    is ResultState.Error -> {
                        _getProductsByCategoryState.value = GetProductsByCategoryScreen(
                            Error = it.message.toString(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun getAllUserWishProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserWishProduct.getUserWishProducts().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getUserWishProductsState.value =
                            GetUserWishProductsScreen(isLoading = true)

                    }

                    is ResultState.Success<List<ProductDataModel>> -> {
                        Log.d("ViewModelWishList", "Toggle result: ${result.data}")
                        _getUserWishProductsState.value = GetUserWishProductsScreen(
                            isSuccess = result.data,
                            isLoading = false
                        )
                    }

                    is ResultState.Error -> {
                        Log.d("ViewModelWishList", "Toggle Result: ${result.message}")
                        _getUserWishProductsState.value = GetUserWishProductsScreen(
                            Error = result.message.toString(),
                            isLoading = false
                        )
                    }
                }
            }

        }
    }

    fun onSearchQueryChanged(query: String) {
        _SearchQuery.value = query
    }

    fun searchQuery(){
        viewModelScope.launch(Dispatchers.IO) {
            _SearchQuery.debounce(500L).distinctUntilChanged().collect {
                SearchProduct(it)
            }
        }
    }

    fun SearchProduct(SearchQuery: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchProductUserUseCase.searchProductUserUseCase(SearchQuery).collectLatest {
                when (it) {
                    is ResultState.Loading -> {
                        _searchProductState.value = SearchProductScreen(isLoading = true)
                    }

                    is ResultState.Success<List<ProductDataModel>> -> {
                        _searchProductState.value = SearchProductScreen(
                            isSuccess = it.data,
                            isLoading = false
                        )

                    }

                    is ResultState.Error -> {
                        _searchProductState.value = SearchProductScreen(
                            Error = it.message.toString(),
                            isLoading = false
                        )
                    }

                }
            }
        }
    }
    // Add this function to your MyViewModel
    fun clearSearchResults() {
        _searchProductState.value = SearchProductScreen()
        _SearchQuery.value = ""
    }

    fun addProductToCart(product: ProductDataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            addProductToCartUserUseCase.addProductToCart(product).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _addProductToCartState.value = AddProductToCartScreen(isLoading = true)
                    }

                    is ResultState.Success<Boolean> -> {
                        _addProductToCartState.value = AddProductToCartScreen(
                            isSuccess = result.data,
                            isLoading = false
                        )
                    }
                    is ResultState.Error -> {
                        _addProductToCartState.value = AddProductToCartScreen(
                            Error = result.message.toString(),
                            isLoading = false
                        )
                    }

                }
            }
        }
    }

    fun resetAddToCartState() {
        _addProductToCartState.value = AddProductToCartScreen()
    }

    fun removeProductFromCart(product: ProductDataModel){
        viewModelScope.launch(Dispatchers.IO) {
            removeProductFromCartUserUseCase.removeProductFromCart(product).collectLatest {results ->
                when(results){
                    is ResultState.Loading -> {
                        _removeProductFromCartState.value = RemoveProductFromCartScreen(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _removeProductFromCartState.value = RemoveProductFromCartScreen(
                            isSuccess = results.data,
                            isLoading = false
                        )
                        // ✅ Update the cart UI state immediately
                        val currentList = _getAllCartProductsState.value.isSuccess?.toMutableList()
                        currentList?.remove(product)
                        _getAllCartProductsState.value = _getAllCartProductsState.value.copy(isSuccess = currentList)
                    }
                    is ResultState.Error -> {
                        _removeProductFromCartState.value = RemoveProductFromCartScreen(
                            Error = results.message.toString(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun getAllCartProducts(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllCartProductsUserUseCase.getAllCartProducts().collectLatest {
                when(it){
                    is ResultState.Loading ->{
                        _getAllCartProductsState.value = GetAllCartProductsScreenState(
                            isLoading = true
                        )
                    }
                    is ResultState.Success<List<ProductDataModel>> ->{
                        _getAllCartProductsState.value = GetAllCartProductsScreenState(
                            isSuccess = it.data,
                            isLoading = false
                        )
                    }
                    is ResultState.Error ->{
                        _getAllCartProductsState.value = GetAllCartProductsScreenState(
                            Error = it.message.toString(),
                            isLoading = false
                        )
                    }

                }
                }
            }

        }

    // Order Functions
    fun createOrder(order: OrderDataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            createOrderUseCase.createOrder(order).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _createOrderState.value = CreateOrderScreen.Loading
                    }

                    is ResultState.Success<String> -> {
                        _createOrderState.value = CreateOrderScreen.Success(result.data)
                        // Automatically clear cart after successful order
                        clearCart()
                    }

                    is ResultState.Error -> {
                        _createOrderState.value = CreateOrderScreen.Error(result.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun getUserOrders() {
        Log.d(TAG, "🔄 getUserOrders() called - Triggered from UI")

        viewModelScope.launch(Dispatchers.IO) {
            getUserOrdersUseCase.getUserOrders().collectLatest { result ->
                Log.d(TAG, "🎯 ViewModel: Raw result received: $result")
                Log.d(TAG, "   Result class: ${result::class.simpleName}")

                when (result) {
                    is ResultState.Loading -> {
                        Log.d(TAG, "📦 ViewModel: Loading orders...")
                        _getUserOrdersState.value = GetUserOrdersScreen(isLoading = true)
                    }

                    is ResultState.Success<List<OrderDataModel>> -> {
                        Log.d(TAG, "✅ ViewModel: Received ${result.data.size} orders")
                        // Filter out cancelled orders
                        val activeOrders = result.data.filter { it.orderStatus != "Cancelled" }
                        _getUserOrdersState.value = GetUserOrdersScreen(
                            isSuccess = activeOrders,
                            isLoading = false
                        )
                    }

                    is ResultState.Error -> {
                        Log.d(TAG, "❌ ViewModel: Error: ${result.message.toString()}")
                        _getUserOrdersState.value = GetUserOrdersScreen(
                            Error = result.message.toString(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch(Dispatchers.IO) {
            clearCartUseCase.clearCart().collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _clearCartState.value = ClearCartScreen(isLoading = true)
                    }

                    is ResultState.Success<Boolean> -> {
                        _clearCartState.value = ClearCartScreen(
                            isSuccess = result.data,
                            isLoading = false
                        )

                        // Refresh cart products after clearing
                        getAllCartProducts()
                    }

                    is ResultState.Error -> {
                        _clearCartState.value = ClearCartScreen(
                            Error = result.message.toString(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    // Cancel order function - can cancel any order regardless of status
    fun cancelOrder(orderId: String) {
        Log.d(TAG, "🔄 cancelOrder() called for order: $orderId")
        viewModelScope.launch(Dispatchers.IO) {
            cancelOrderUserUseCase.cancelOrderUserUseCase(orderId).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> {
                        Log.d(TAG, "⏳ ViewModel: Cancelling order...")
                        _cancelOrderState.value = CancelOrderScreen(isLoading = true)
                    }
                    is ResultState.Success<Boolean> -> {
                        Log.d(TAG, "✅ ViewModel: Order cancelled successfully")
                        _cancelOrderState.value = CancelOrderScreen(
                            isSuccess = result.data,
                            isLoading = false
                        )
                        // Refresh orders list after cancellation
                        getUserOrders()
                    }
                    is ResultState.Error -> {
                        Log.d(TAG, "❌ ViewModel: Cancel failed: ${result.message.toString()}")
                        _cancelOrderState.value = CancelOrderScreen(
                            Error = result.message.toString(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }



    // Reset functions for state management
    fun resetCreateOrderState() {
        _createOrderState.value = CreateOrderScreen.Idle
    }
    // Reset cancel order state
    fun resetCancelOrderState() {
        _cancelOrderState.value = CancelOrderScreen()
    }
    fun resetGetUserOrdersState() {
        _getUserOrdersState.value = GetUserOrdersScreen()
    }

    fun resetClearCartState() {
        _clearCartState.value = ClearCartScreen()
    }

     fun getBannerModels(){
        viewModelScope.launch {
            getAllBannerModelsUserUseCase.getBannerModelUserUseCase().collectLatest {
                when (it) {
                    is ResultState.Loading -> {
                        _getBannerModelsState.value = GetBannerModelsScreenState(isLoading = true)
                    }

                    is ResultState.Success<List<BannerDataModel>> -> {
                        _getBannerModelsState.value = GetBannerModelsScreenState(
                            isSuccess = it.data,
                            isLoading = false
                        )
                    }

                    is ResultState.Error -> {
                        _getBannerModelsState.value = GetBannerModelsScreenState(
                            Error = it.message.toString(),
                            isLoading = false
                        )
                    }
                }

            }
        }
    }

}

sealed class AuthState {
    // Initial state (no operation in progress)
    object Uninitialized : AuthState()

    // Loading state (common for all operations)
    object Loading : AuthState()

    // Success states (with success data)
    data class SignUpSuccess(val message: String) : AuthState()
    data class LoginSuccess(val message: String) : AuthState()
    data class UserAuthenticated(val message: String) : AuthState()

    // Error state (shared across all operations)
    data class Error(val message: String) : AuthState()

    // Logged out state
    object SignedOut : AuthState()
}

data class GetAllCategories(
    val isLoading: Boolean = false,
    val isSuccess: List<CategoryDataModel>?= emptyList(),
    val Error: String? = null
)

data class GetAllProducts(
    val isLoading: Boolean = false,
    val isSuccess: List<ProductDataModel>?= emptyList(),
    val Error: String? = null
)

data class GetSpecificProduct(
    val isLoading: Boolean = false,
    val isSuccess: ProductDataModel?= null,
    val Error: String? = null
)

data class LoadHomeScreen(
    val isLoading: Boolean = false,
    val isSuccessProduct : List<ProductDataModel>? = emptyList(),
    val isSuccessCategory : List<CategoryDataModel>? = emptyList(),
    val isSuccessBannerModel : List<BannerDataModel>? = emptyList<BannerDataModel>(),
    val Error: String? =  null
)

data class GetUserScreen(
    val isLoading: Boolean = false,
    val isSuccess: UserDataModel? = null,
    val Error: String? = null

    )

data class UpdateUserDataScreen(
    val isLoading: Boolean = false,
    val isSuccess: String? = null,
    val Error: String? = null
)

data class GetBannerModelsScreenState(
    val isLoading: Boolean = false,
    val isSuccess: List<BannerDataModel>? = emptyList<BannerDataModel>(),
    val Error: String? = null
)

data class IsItemInWishListScreen(
    val isLoading: Boolean = false,
    val isInWishList: String? = null,
    val Error: String? = null
)

data class WishListState(
    val isLoading: Boolean = false,
    val isInWishList: Boolean? = null, // Current wishlist status
    val lastAction: String? = null, // "added" or "removed"
    val Error: String? = null
)

data class GetProductsByCategoryScreen(
    val isLoading: Boolean = false,
    val isSuccess: List<ProductDataModel>? = emptyList<ProductDataModel>(),
    val Error: String? = null
)

data class GetUserWishProductsScreen(
    val isLoading: Boolean = false,
    val isSuccess: List<ProductDataModel>? = emptyList<ProductDataModel>(),
    val Error: String? = null
)

data class SearchProductScreen(
    val isLoading: Boolean = false,
    val isSuccess: List<ProductDataModel>? = emptyList<ProductDataModel>(),
    val Error: String? = null
)

data class AddProductToCartScreen(
    val isLoading: Boolean = false,
    val isSuccess: Boolean? = null,
    val Error: String? = null
)

data class RemoveProductFromCartScreen(
    val isLoading: Boolean = false,
    val isSuccess: Boolean? = null,
    val Error: String? = null
)

data class GetAllCartProductsScreenState(
    val isLoading: Boolean? =false,
    val isSuccess: List<ProductDataModel>? = emptyList<ProductDataModel>(),
    val Error: String? = null
)

sealed class CreateOrderScreen {
    object Idle : CreateOrderScreen()
    object Loading : CreateOrderScreen()
    data class Success(val message: String) : CreateOrderScreen()
    data class Error(val error: String) : CreateOrderScreen()
}


data class GetUserOrdersScreen(
    val isLoading: Boolean = false,
    val isSuccess: List<OrderDataModel>? = emptyList(),
    val Error: String? = null
)

data class ClearCartScreen(
    val isLoading: Boolean = false,
    val isSuccess: Boolean? = null,
    val Error: String? = null
)

// Add this to your state classes
data class CancelOrderScreen(
    val isLoading: Boolean = false,
    val isSuccess: Boolean? = null,
    val Error: String? = null
)


