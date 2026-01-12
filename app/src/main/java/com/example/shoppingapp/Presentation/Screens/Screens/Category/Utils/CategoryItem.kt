package com.example.shoppingapp.Presentation.Screens.Screens.Category.Utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun CategoryItem(
    ImageUrl : String,
    CategoryName: String,
    onItemClick : () -> Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp).
        width(100.dp).clickable{ onItemClick() }
    ) {
        //
        Card(
            modifier = Modifier.size(80.dp)
            , shape = CircleShape
        ){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (ImageUrl.isEmpty().not()){
                    AsyncImage(
                        model = ImageUrl,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                }else{
                    // Show placeholder when image is empty
                    Text(
                        text = CategoryName.take(1).uppercase(), // Show first 2 letters
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }

        }
        // Spacer between image and text
        Spacer(modifier = Modifier.height(8.dp))

        // Category Name Text
        Text(
            text = CategoryName,
            modifier = Modifier.padding(bottom = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}