package com.example.shoppingapp.Presentation.Screens.Screens.Products.Utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.shoppingapp.Domain.Models.ProductDataModel
import com.example.shoppingapp.Presentation.ViewModel.MyViewModel
import com.example.shoppingapp.Presentation.ViewModel.WishListState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductCart(
    product: ProductDataModel,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
    viewModel: MyViewModel = koinViewModel()
) {

    val wishListStates by viewModel.wishListState.collectAsStateWithLifecycle()
    val currentWishListState = wishListStates[product.productID] ?: WishListState()

    LaunchedEffect(Unit) {
        if (currentWishListState.isInWishList == null) {
            viewModel.checkIfItemInWishList(product.productID)
        }
    }

    Card(
        modifier = modifier
            .padding(8.dp)
            .width(165.dp)
            .clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {

            // ⭐ CLEAN FULL-WIDTH IMAGE
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ⭐ PRODUCT NAME
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ⭐ PRICE ROW (Original + Final)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // ⭐ ORIGINAL PRICE (Strikethrough)
                Text(
                    text = "₹${product.price}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    maxLines = 1,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                )

                // ⭐ FINAL PRICE (Highlighted)
                Text(
                    text = "₹${product.finalprice}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    maxLines = 1
                )
            }

        }
    }
}
