package com.example.shoppingapp.Presentation.Screens.Screens.HomeScreenSection.BannerUtilsSection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.shoppingapp.Domain.Models.BannerDataModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BannerCart(
    BannerPhotos: List<BannerDataModel>,
    modifier: Modifier = Modifier,
    autoScrollInterval : Long = 2000L, // 3 seconds
    initialPage: Int = 0
) {
    val CoroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f
    ){
        BannerPhotos.size // This is the pageCount

    }
    // Auto-scroll effect
    LaunchedEffect(pagerState) {
        while (true) {
            delay(autoScrollInterval)
            val nextPage = (pagerState.currentPage + 1) % BannerPhotos.size
            pagerState.animateScrollToPage(nextPage)
        }
    }
    Column(modifier = Modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(180.dp) // Fixed height for consistency
        ) { page ->
            BannerItemContent(item = BannerPhotos[page])
        }

        // Indicators
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(BannerPhotos.size) { index ->
                val color = if (pagerState.currentPage == index) Color.Blue else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable {
                            CoroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                )
            }
        }

    }

}
@Composable
fun BannerItemContent(item: BannerDataModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = rememberAsyncImagePainter(model = item.imageUrl),
            contentDescription = item.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

    }
}