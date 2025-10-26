package com.example.ecommerceapp.ui.customer.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecommerceapp.data.model.ProductReviewDTO
import com.example.ecommerceapp.ui.components.ProductImage
import com.example.ecommerceapp.ui.components.ImageZoomDialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var quantity by remember { mutableStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.addedToCart) {
        if (state.addedToCart) {
            snackbarHostState.showSnackbar("Товар добавлен в корзину")
            viewModel.resetAddedToCart()
        }
    }

    // Image zoom dialog
    if (showImageDialog && state.product?.images?.isNotEmpty() == true) {
        ImageZoomDialog(
            productId = state.product!!.id,
            images = state.product!!.images!!,
            initialPage = selectedImageIndex,
            onDismiss = { showImageDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали товара") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            state.product?.let { product ->
                if (product.stockQuantity > 0) {
                    Surface(
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Количество")
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (quantity > 1) quantity-- },
                                        enabled = quantity > 1
                                    ) {
                                        Text("-", style = MaterialTheme.typography.headlineSmall)
                                    }
                                    Text(
                                        text = quantity.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    IconButton(
                                        onClick = { if (quantity < product.stockQuantity) quantity++ },
                                        enabled = quantity < product.stockQuantity
                                    ) {
                                        Text("+", style = MaterialTheme.typography.headlineSmall)
                                    }
                                }
                            }

                            Button(
                                onClick = { viewModel.addToCart(quantity) }
                            ) {
                                Text("В корзину ${product.price * quantity} ₽")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(state.error ?: "Ошибка загрузки")
            }
        } else {
            state.product?.let { product ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    if (!product.images.isNullOrEmpty()) {
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(product.images) { index, image ->
                                    Box(
                                        modifier = Modifier
                                            .size(width = 300.dp, height = 350.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                selectedImageIndex = index
                                                showImageDialog = true
                                            }
                                    ) {
                                        ProductImage(
                                            productId = product.id,
                                            imageInfo = image,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Product info
                    item {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${product.price} ₽",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (product.averageRating != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = String.format("%.1f", product.averageRating),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = " (${product.reviewsCount} отзывов)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (product.stockQuantity > 0) {
                                Text(
                                    text = "В наличии: ${product.stockQuantity} шт.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            } else {
                                Text(
                                    text = "Нет в наличии",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            product.description?.let {
                                Text(
                                    text = "Описание",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    // Reviews
                    if (state.reviews.isNotEmpty()) {
                        item {
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                            Text(
                                text = "Отзывы",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        items(state.reviews) { review ->
                            ReviewItem(review)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: ProductReviewDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = review.authorName,
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < review.rating)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            review.comment?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = review.createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}