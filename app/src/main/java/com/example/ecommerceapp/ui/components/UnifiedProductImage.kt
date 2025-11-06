package com.example.ecommerceapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.ecommerceapp.data.model.ProductImageDTO
import com.example.ecommerceapp.util.Resource

/**
 * Единые размеры изображений для всего приложения
 */
object ImageSizes {
    val ExtraSmall = 60.dp      // Admin products list
    val Small = 80.dp           // Home, Cart, Reviews
    val Medium = 100.dp         // Reserve для будущего
    val Large = 120.dp          // Edit screen thumbnails
    val DetailWidth = 300.dp    // Detail screen gallery
    val DetailHeight = 350.dp   // Detail screen gallery
    val ReviewHeight = 200.dp   // Review images full width
}

/**
 * Единые параметры скругления
 */
object ImageCorners {
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(16.dp)
}

/**
 * Унифицированный компонент для отображения изображений товаров
 *
 * @param productId ID товара
 * @param imageInfo информация об изображении
 * @param size размер изображения (квадрат)
 * @param cornerRadius скругление углов
 * @param contentScale масштабирование содержимого
 * @param onClick обработчик клика (опционально)
 * @param modifier дополнительные модификаторы
 */
@Composable
fun UnifiedProductImage(
    productId: Int,
    imageInfo: ProductImageDTO,
    size: Dp = ImageSizes.Small,
    cornerRadius: RoundedCornerShape = ImageCorners.Small,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: ProductImageViewModel = hiltViewModel()
) {
    val imageState by viewModel.getImage(productId, imageInfo.id).collectAsState()

    Box(
        modifier = modifier
            .size(size)
            .clip(cornerRadius)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (imageState) {
            is Resource.Loading, null -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(size / 2),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is Resource.Success -> {
                imageState?.data?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = imageInfo.altText,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale
                    )
                }
            }
            is Resource.Error -> {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Ошибка загрузки",
                    modifier = Modifier.size(size / 2),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * Вариант для прямой загрузки по URL (например, для корзины)
 *
 * @param imageUrl прямой URL изображения
 * @param contentDescription описание изображения
 * @param size размер изображения (квадрат)
 * @param cornerRadius скругление углов
 * @param contentScale масштабирование содержимого
 * @param onClick обработчик клика (опционально)
 * @param modifier дополнительные модификаторы
 */
@Composable
fun UnifiedProductImageUrl(
    imageUrl: String?,
    contentDescription: String?,
    size: Dp = ImageSizes.Small,
    cornerRadius: RoundedCornerShape = ImageCorners.Small,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(cornerRadius)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null && imageUrl.isNotBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            )
        } else {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Нет изображения",
                modifier = Modifier.size(size / 2),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * Унифицированный компонент для изображений отзывов
 *
 * @param productId ID товара
 * @param reviewId ID отзыва
 * @param size размер изображения (квадрат)
 * @param cornerRadius скругление углов
 * @param onClick обработчик клика (опционально)
 * @param modifier дополнительные модификаторы
 */
@Composable
fun UnifiedReviewImage(
    productId: Int,
    reviewId: Int,
    size: Dp = ImageSizes.Small,
    cornerRadius: RoundedCornerShape = ImageCorners.Small,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: ProductImageViewModel = hiltViewModel()
) {
    val imageState by viewModel.getReviewImage(productId, reviewId).collectAsState()

    Box(
        modifier = modifier
            .size(size)
            .clip(cornerRadius)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (imageState) {
            is Resource.Loading, null -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(size / 3),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is Resource.Success -> {
                imageState?.data?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Изображение отзыва",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            is Resource.Error -> {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Ошибка загрузки",
                    modifier = Modifier.size(size / 2),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * Extension функции для Modifier - упрощенные варианты
 */

// Для малых изображений (списки, карточки)
fun Modifier.smallProductImage() = this
    .size(ImageSizes.Small)
    .clip(ImageCorners.Small)

// Для extra малых изображений (админ-панель)
fun Modifier.extraSmallProductImage() = this
    .size(ImageSizes.ExtraSmall)
    .clip(ImageCorners.Small)

// Для больших изображений (редактирование)
fun Modifier.largeProductImage() = this
    .size(ImageSizes.Large)
    .clip(ImageCorners.Small)
