package com.example.ecommerceapp.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecommerceapp.data.model.ProductImageDTO
import com.example.ecommerceapp.util.Resource

@Composable
fun ProductImage(
    productId: Int,
    imageInfo: ProductImageDTO,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    viewModel: ProductImageViewModel = hiltViewModel()
) {
    val bitmap by viewModel.getImage(productId, imageInfo.id).collectAsState(initial = null)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (bitmap) {
            is Resource.Loading, null -> {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
            is Resource.Success -> {
                (bitmap as? Resource.Success<Bitmap>)?.data?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = imageInfo.altText,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale
                    )
                }
            }
            is Resource.Error -> {
                Text(
                    text = "Ошибка загрузки",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is Resource.Error<*> -> TODO()
            is Resource.Loading<*> -> TODO()
            is Resource.Success<*> -> TODO()
        }
    }
}

@Composable
fun ReviewImage(
    productId: Int,
    reviewId: Int,
    modifier: Modifier = Modifier,
    viewModel: ProductImageViewModel = hiltViewModel()
) {
    val bitmap by viewModel.getReviewImage(productId, reviewId).collectAsState(initial = null)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (bitmap) {
            is Resource.Loading, null -> {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
            is Resource.Success -> {
                (bitmap as? Resource.Success<Bitmap>)?.data?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Review image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            is Resource.Error -> {
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}