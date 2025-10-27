package com.example.ecommerceapp.ui.customer.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecommerceapp.ui.components.ReviewDialog
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Int,
    onNavigateBack: () -> Unit,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteReviewDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var selectedProductId by remember { mutableIntStateOf(0) }
    var selectedProductName by remember { mutableStateOf("") }
    var isEditingReview by remember { mutableStateOf(false) }
    var selectedReviewId by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    LaunchedEffect(state.orderDeleted) {
        if (state.orderDeleted) {
            onNavigateBack()
        }
    }

    LaunchedEffect(state.reviewSubmitted) {
        if (state.reviewSubmitted) {
            snackbarHostState.showSnackbar(
                if (isEditingReview) "Отзыв успешно обновлен" else "Отзыв успешно отправлен"
            )
            viewModel.resetReviewSubmitted()
            viewModel.loadOrder(orderId)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить заказ") },
            text = { Text("Вы уверены, что хотите удалить этот заказ? Товары вернутся на склад.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteOrder(orderId)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showDeleteReviewDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteReviewDialog = false },
            title = { Text("Удалить отзыв") },
            text = { Text("Вы уверены, что хотите удалить свой отзыв?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteReview(selectedProductId, selectedReviewId)
                        showDeleteReviewDialog = false
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteReviewDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showReviewDialog) {
        val existingReview = state.productReviews[selectedProductId]

        ReviewDialog(
            productName = selectedProductName,
            existingRating = existingReview?.rating,
            existingComment = existingReview?.comment,
            existingImageUrl = existingReview?.reviewImageUrl,
            isEdit = isEditingReview,
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment, imageFile, deleteImage ->
                if (isEditingReview) {
                    viewModel.updateReview(
                        productId = selectedProductId,
                        reviewId = selectedReviewId,
                        rating = rating,
                        comment = comment,
                        imageFile = imageFile,
                        deleteImage = deleteImage
                    )
                } else {
                    viewModel.submitReview(
                        productId = selectedProductId,
                        rating = rating,
                        comment = comment
                    )
                }
                showReviewDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Заказ #$orderId") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Удалить заказ",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Ошибка загрузки",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            state.order != null -> {
                val order = state.order!!
                val isCompleted = order.status == "Completed"

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Статус",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    StatusBadge(status = order.status)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Дата: ${formatDate(order.createdAt)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Информация о доставке",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Получатель: ${order.customerName}")
                                Text("Телефон: ${order.customerPhone}")
                                Text("Адрес: ${order.deliveryAddress}")
                                Text(
                                    "Оплата: ${if (order.paymentMethod == "Card") "Банковская карта" else "Наличные"}"
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            "Товары в заказе",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(order.orderItems) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.productName,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = "${item.priceAtPurchase} ₽ × ${item.quantity}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = String.format("%.2f ₽", item.subtotal),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Review button for completed orders
                                if (isCompleted) {
                                    val existingReview = state.productReviews[item.productId]

                                    Spacer(modifier = Modifier.height(12.dp))

                                    if (existingReview != null) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    selectedProductId = item.productId
                                                    selectedProductName = item.productName
                                                    selectedReviewId = existingReview.id
                                                    isEditingReview = true
                                                    showReviewDialog = true
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Редактировать")
                                            }
                                            OutlinedButton(
                                                onClick = {
                                                    selectedProductId = item.productId
                                                    selectedReviewId = existingReview.id
                                                    showDeleteReviewDialog = true
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.error
                                                )
                                            ) {
                                                Text("Удалить")
                                            }
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = {
                                                selectedProductId = item.productId
                                                selectedProductName = item.productName
                                                isEditingReview = false
                                                showReviewDialog = true
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Оценить товар")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Итого:",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = String.format("%.2f ₽", order.totalAmount),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
