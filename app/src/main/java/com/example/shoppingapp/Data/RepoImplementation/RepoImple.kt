package com.example.shoppingapp.Data.RepoImplementation

import ADD_TO_WISH_LIST
import Add_TO_CART
import BANNER_MODEL
import CATEGORY_PATH
import ORDERS_PATH
import PRODUCT_PATH
import USER_FCM_TOKEN
import USER_ORDERS_SUBCOLLECTION
import USER_PATH
import android.util.Log
import com.example.shoppingapp.Common.ResultState
import com.example.shoppingapp.Domain.Models.BannerDataModel
import com.example.shoppingapp.Domain.Models.CategoryDataModel
import com.example.shoppingapp.Domain.Models.OrderDataModel
import com.example.shoppingapp.Domain.Models.ProductDataModel
import com.example.shoppingapp.Domain.Models.UserDataModel
import com.example.shoppingapp.Domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow



class RepoImple(
    private val firestore : FirebaseFirestore,
    private val firebaseMessaging: FirebaseMessaging,
    private val firebaseAuth: FirebaseAuth
): Repo{
    private val TAG = "OrdersRepository"

     override  fun registerUserWithEmailAndPassword(UserData: UserDataModel)
    : Flow<ResultState<String>> = callbackFlow{
        trySend(ResultState.Loading)
        try {
            firebaseAuth.createUserWithEmailAndPassword(UserData.email , UserData.password).addOnSuccessListener {
                updateFcmToken(it.user?.uid ?: "")
                firestore.collection(USER_PATH).document(
                    it.user?.uid.toString()
                ).set(UserData).addOnSuccessListener {
                    trySend(ResultState.Success("User Register Successfully"))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.message.toString()))
                }
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }
        }
        catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }
    }

     override  fun signInUserWithEmailAndPassword(UserData: UserDataModel):
            Flow<ResultState<String>> = callbackFlow{
                trySend(ResultState.Loading)
        try {
            firebaseAuth.signInWithEmailAndPassword(UserData.email , UserData.password).addOnSuccessListener {
                val uid = it.user?.uid ?:" "
                updateFcmToken(uid)
                trySend(ResultState.Success("User Logged In Successfully"))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }
        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }
    }

     override  fun checkUserStatus(): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            if (firebaseAuth.currentUser != null){
                trySend(ResultState.Success("authenticated"))
            }else{
                trySend(
                    ResultState.Success ("unauthenticated")
                )
            }
        }
        catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }

    }

     override fun UserSignOut(): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firebaseAuth.signOut()
            trySend(ResultState.Success("User signed Out successfully"))
        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }

    }

     override fun getUser() : Flow<ResultState<UserDataModel>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firestore.collection(USER_PATH).document(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
                val User = it.toObject(UserDataModel::class.java)
             User?.let {
                    trySend(ResultState.Success(User))
           } ?: trySend(ResultState.Error("User not found"))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }
        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }
    }

     override fun UpdateUserData(UserData: UserDataModel) : Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firestore.collection(USER_PATH).document(firebaseAuth.currentUser!!.uid)
                .set(UserData).addOnSuccessListener {
                    trySend(ResultState.Success("User Data Updated Successfully"))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.message.toString()))
                }
        }catch(e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }
    }

     override fun getAllCategory():
            Flow<ResultState<List<CategoryDataModel>>> =callbackFlow{
                trySend(ResultState.Loading)
        try {
            firestore.collection(CATEGORY_PATH).get().addOnSuccessListener{ categorySnapshot ->
                val categoryList = categorySnapshot.documents.mapNotNull {
                    it.toObject(CategoryDataModel::class.java)
                }
                Log.d("Repo_CategoryList", "Category list: $categoryList")
                trySend(ResultState.Success(categoryList))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }

        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
            awaitClose{
                close()
            }
    }

     override fun getAllProducts( ):
            Flow<ResultState<List<ProductDataModel>>> = callbackFlow {
                trySend(ResultState.Loading) // 1. Show loading state
        try {
            // 2. Register Firestore callbacks
            firestore.collection(PRODUCT_PATH).get().addOnSuccessListener { collectionSnapshot ->  // Success callback
                // Converts Firestore data of Products into a list of ProductModel
                val productsList = collectionSnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(ProductDataModel::class.java)?.apply {
                        productID = documentSnapshot.id
                    }
                }
                trySend(ResultState.Success(productsList)) // Success callback

            }.addOnFailureListener {Error ->   // Failure callback
                trySend(ResultState.Error(Error.message.toString()))
            }

        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        // 5. Keep Flow alive until cancelled
        awaitClose{
            close()
        }

    }

     override fun getSpecificProduct(ProductId: String):
            Flow<ResultState<ProductDataModel>> =callbackFlow{
                trySend(ResultState.Loading)


        try {
            firestore.collection(PRODUCT_PATH).document(ProductId).get().addOnSuccessListener{ documentSnapshot ->

                val product = documentSnapshot.toObject(ProductDataModel::class.java)

                product?.let {
                    it.productID = documentSnapshot.id // ← CRITICAL: Set the ID
                    trySend(ResultState.Success(product))
                } ?: trySend(ResultState.Error("Product not found"))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }
        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }
    }

     override fun getAllWishProductsOfUser(): Flow<ResultState<List<ProductDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firestore.collection(ADD_TO_WISH_LIST).document(firebaseAuth.currentUser!!.uid)
                .collection("User_Fav").get().addOnSuccessListener {querySnapshot ->
                    // Debug: Print all document data
                    querySnapshot.documents.forEach { document ->
                        Log.d("RepoWishlist", "Document data: ${document.data}")
                        Log.d("RepoWishlist", "Document ID: ${document.id}")
                        Log.d("WishlistDebug", "Has productID field: ${document.data?.containsKey("productID")}")

                    }

                    val productList = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(ProductDataModel::class.java)
                }
                trySend(ResultState.Success(productList))
                Log.d("RepoWishlist", "Toggle result: $productList")

            }.addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
                Log.d("RepoWishlist", "Toggle result: ${it.message.toString()}")
            }

        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
            Log.d("RepoWishlist", "Toggle result: ${e.message.toString()}")
        }
        awaitClose{
            close()
        }
    }

     override fun getProductByCategory(categoryName: String): Flow<ResultState
    <List<ProductDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firestore.collection(PRODUCT_PATH).whereEqualTo("category", categoryName).get()
                .addOnSuccessListener {
                    val products = it.documents.mapNotNull {
                        it.toObject(ProductDataModel::class.java)?.apply {
                            productID = it.id
                        }
                    }
                   trySend(ResultState.Success(products))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.message.toString()))
                }

        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }

    }

    // In your Repo interface - ONLY keep toggle override function
     override fun toggleWishList(product: ProductDataModel): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User Not Authenticated")
            val productId = product.productID


            // Check if item exists
            firestore.collection(ADD_TO_WISH_LIST)
                .document(userId)
                .collection("User_Fav")
                .whereEqualTo("productID", productId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {

                        // REMOVE - Item exists, so delete it
                        val batch = firestore.batch()
                        for (document in querySnapshot.documents) {
                            batch.delete(document.reference)
                        }
                        batch.commit().addOnSuccessListener {
                            trySend(ResultState.Success(false)) // false = removed
                        }.addOnFailureListener { e ->
                            trySend(ResultState.Error(e.message.toString()))
                        }
                    } else {
                        // ADD - Item doesn't exist, so add it
                        val docRef = firestore.collection(ADD_TO_WISH_LIST)
                            .document(userId)
                            .collection("User_Fav")
                            .document(productId)


                        docRef.set(product).addOnSuccessListener {
                            trySend(ResultState.Success(true)) // true = added
                        }.addOnFailureListener {
                            trySend(ResultState.Error(it.message.toString()))
                        }
                    }
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.message.toString()))
                }
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose {
            close()
        }
    }


     override fun addProductToCart(product: ProductDataModel): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        val userId = firebaseAuth.currentUser?.uid
        if(userId == null){
            trySend(ResultState.Error("User Not Authenticated"))
            close()
            return@callbackFlow
        }
        try {
            val productId = product.productID
            val docRef = firestore.collection(Add_TO_CART)
                .document(userId)
                .collection("User_Cart")
                .document(productId)
            val listener = docRef.set(product).addOnSuccessListener {
                trySend(ResultState.Success(true))
                close()
            }.addOnFailureListener {exception ->
                trySend(ResultState.Error(exception.message.toString() ?: "Unknown Error"))
                close()
            }

        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }
    }

     override fun removeProductFromCart(product: ProductDataModel): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null){
            trySend(ResultState.Error("User not Authenticated"))
            return@callbackFlow
            close()
        }

        try {
            val productId = product.productID

           val listener =  firestore.collection(Add_TO_CART).document(userId)
                .collection("User_Cart")
                .document(productId)
                .delete().addOnSuccessListener {
                    trySend(ResultState.Success(true))
                   close()
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.message.toString()))
                   close()
                }

        } catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }
    }

     override fun getProductsFromCart(): Flow<ResultState<List<ProductDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val userId = firebaseAuth.currentUser?.uid
            if (userId == null){
                trySend(ResultState.Error("User not Authenticated"))
                return@callbackFlow
                close()
            }
            val listener = firestore.collection(Add_TO_CART)
                .document(userId)
                .collection("User_Cart")
                .get().addOnSuccessListener {collectionSnapshot ->
                    val productList = collectionSnapshot.documents.mapNotNull {documentSnapshot ->
                        documentSnapshot.toObject(ProductDataModel::class.java)
                    }
                    Log.d("CartDebug", "Cart list: $productList")
                    trySend(ResultState.Success(productList))

                    close()
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.message.toString()))
                    close()

                }

        } catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }

    }

    override fun createOrder(order: OrderDataModel): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val userId = firebaseAuth.currentUser?.uid ?: run {
            trySend(ResultState.Error("User not authenticated"))
            close()
            return@callbackFlow
        }

        try {
            val orderId = firestore.collection(ORDERS_PATH).document().id
            // ✅ FIXED: Include both orderId AND userId
            val orderWithId = order.copy(
                orderId = orderId,
                userId = userId
            )

            // Use batch write for atomic operation
            val batch = firestore.batch()

            // Add to main orders collection
            val mainOrderRef = firestore.collection(ORDERS_PATH).document(orderId)
            batch.set(mainOrderRef, orderWithId)

            // Add to user's subcollection
            val userOrderRef = firestore.collection(USER_PATH)
                .document(userId)
                .collection(USER_ORDERS_SUBCOLLECTION)
                .document(orderId)
            batch.set(userOrderRef, orderWithId)

            batch.commit()
                .addOnSuccessListener {
                    trySend(ResultState.Success("Order created successfully"))
                    close()
                }
                .addOnFailureListener { e ->
                    trySend(ResultState.Error("Failed to create order: ${e.message}"))
                    close()
                }
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message.toString()))
            close()
        }

        awaitClose { close() }
    }

    override fun getUserOrders(): Flow<ResultState<List<OrderDataModel>>> = callbackFlow {
        Log.d(TAG, "🔥 Repository: getUserOrders() started")

        trySend(ResultState.Loading)

        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Log.d(TAG, "❌ Repository: User not authenticated")
            trySend(ResultState.Error("User not Authenticated"))
            close()
            return@callbackFlow
        }

        Log.d(TAG, "👤 Repository: User ID: $userId")
        Log.d(TAG, "📁 Repository: Listening to user orders subcollection")

        val listenerRegistration = firestore.collection(USER_PATH)
            .document(userId)
            .collection(USER_ORDERS_SUBCOLLECTION)
            .orderBy("orderDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                Log.d(TAG, "🎯 Repository: SnapshotListener triggered")

                if (error != null) {
                    Log.d(TAG, "❌ Repository: Snapshot error: ${error.message}")
                    trySend(ResultState.Error("Failed to listen for orders: ${error.message}"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d(TAG, "📄 Repository: Snapshot received - ${snapshot.documents.size} documents")
                    Log.d(TAG, "   From cache: ${snapshot.metadata.isFromCache}")

                    val ordersList = snapshot.documents.mapNotNull { doc ->
                        val order = doc.toObject(OrderDataModel::class.java)
                        Log.d(TAG, "   📦 Order: ${order?.orderId} - Status: ${order?.orderStatus}")
                        order
                    }.filter { it.orderStatus != "Cancelled" }

                    Log.d(TAG, "✅ Repository: Filtered to ${ordersList.size} active orders")
                    trySend(ResultState.Success(ordersList))
                } else {
                    Log.d(TAG, "⚠️ Repository: Snapshot is null")
                }
            }

        Log.d(TAG, "👂 Repository: Listener registered successfully")

        awaitClose {
            Log.d(TAG, "🔚 Repository: Listener removed")
            listenerRegistration.remove()
        }
    }
    // Add this override function to check if item is in wishlist
     override fun isItemInWishList(productId: String): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            val userId = firebaseAuth.currentUser?.uid ?:
            throw Exception("User Not Authenticated")

            firestore.collection(ADD_TO_WISH_LIST)
                .document(userId)
                .collection("User_Fav")
                .document(productId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                   val isInWishList = documentSnapshot.exists()
                    trySend(ResultState.Success(isInWishList))

                }.addOnFailureListener {
                    trySend(ResultState.Error(it.message.toString()))
                }
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose {
            close()
        }
    }
    // Also add the clearCart method to complete the order flow
    override fun clearCart(): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)

        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            trySend(ResultState.Error("User not authenticated"))
            close()
            return@callbackFlow
        }

        try {
            // Get all cart items and delete them in batch
            firestore.collection(Add_TO_CART)
                .document(userId)
                .collection("User_Cart")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val batch = firestore.batch()

                    querySnapshot.documents.forEach { document ->
                        batch.delete(document.reference)
                    }

                    batch.commit()
                        .addOnSuccessListener {
                            trySend(ResultState.Success(true))
                            close()
                        }
                        .addOnFailureListener { e ->
                            trySend(ResultState.Error("Failed to clear cart: ${e.message}"))
                            close()
                        }
                }
                .addOnFailureListener { e ->
                    trySend(ResultState.Error("Failed to get cart items: ${e.message}"))
                    close()
                }

        } catch (e: Exception) {
            trySend(ResultState.Error(e.message.toString()))
            close()
        }

        awaitClose {
            close()
        }
    }

    override fun cancelOrder(orderId: String): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.Loading)
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            trySend(ResultState.Error("User not authenticated"))
            close()
            return@callbackFlow
        }
        try {
            // Update order status to cancelled in main orders collection
            firestore.collection(ORDERS_PATH)
                .document(orderId)
                .update("orderStatus", "Cancelled")
                .addOnSuccessListener {
                    // Also update in user's personal orders subcollection
                    firestore.collection(USER_PATH)
                        .document(userId)
                        .collection(USER_ORDERS_SUBCOLLECTION)
                        .document(orderId)
                        .update("orderStatus", "Cancelled")
                        .addOnSuccessListener {
                            trySend(ResultState.Success(true))
                            close()
                        }
                        .addOnFailureListener { e ->
                            trySend(ResultState.Error("Order cancelled but user reference update failed: ${e.message}"))
                            close()
                        }
                }
                .addOnFailureListener { e ->
                    trySend(ResultState.Error("Failed to cancel order: ${e.message}"))
                    close()
                }

        } catch (e: Exception) {
            trySend(ResultState.Error(e.message.toString()))
            close()
        }
        awaitClose {
            close()
        }

    }



    override fun getAllBannerModels(): Flow<ResultState<List<BannerDataModel>>> = callbackFlow {
       trySend(ResultState.Loading)
        try {
            firestore.collection(BANNER_MODEL).get().addOnSuccessListener { collectionSnapshot ->
                val BannerModelList = collectionSnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(BannerDataModel::class.java)
                }
                trySend(ResultState.Success(BannerModelList))

            }.addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }

        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }
    }

     override fun searchProduct(SearchQuery: String): Flow<ResultState<List<ProductDataModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        try {
            firestore.collection(PRODUCT_PATH).orderBy("name").startAt(SearchQuery)
                .endAt(SearchQuery + "\uf8ff")
                .get().addOnSuccessListener {

                val productList = it.documents.mapNotNull {document->
                    val product = document.toObject(ProductDataModel::class.java)
                   product?.copy(productID = document.id)
                }
                trySend(ResultState.Success(productList))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }

        }catch (e: Exception){
            trySend(ResultState.Error(e.message.toString()))
        }
        awaitClose{
            close()
        }
    }

  private fun updateFcmToken(userId: String){
        firebaseMessaging.token.addOnSuccessListener { task ->
            val token = task

            firestore.collection(USER_FCM_TOKEN).document(userId).set(
                mapOf("token" to token)
            ).addOnSuccessListener {
                Log.d("FCM_TOKEN", "Token updated successfully")
            }.addOnFailureListener { e ->
                Log.e("FCM_TOKEN", "Error updating token", e)
            }
        }.addOnFailureListener {
            Log.e("FCM_TOKEN", "Error getting token", it)

        }
    }


}