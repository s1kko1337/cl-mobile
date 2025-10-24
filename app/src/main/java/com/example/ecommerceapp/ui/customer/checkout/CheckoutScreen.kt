package com.example.ecommerceapp.ui.customer.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onOrderComplete: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("card") }

    // Показываем ошибку если она есть
    LaunchedEffect(state.error) {
        state.error?.let {
            // Можно показать Snackbar или Toast
            // Пока просто очищаем ошибку
            viewModel.clearError()
        }
    }

    LaunchedEffect(state.orderCompleted) {
        if (state.orderCompleted) {
            onOrderComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Оформление заказа") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Показываем ошибку если есть
                    state.error?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Итого:", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = String.format("%.2f ₽", state.total),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.placeOrder(name, address, phone, selectedPayment)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isProcessing && name.isNotBlank() &&
                                address.isNotBlank() && phone.isNotBlank()
                    ) {
                        if (state.isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Оформить заказ")
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Контактная информация",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Адрес доставки") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            item {
                Text(
                    "Способ оплаты",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPayment == "card",
                            onClick = { selectedPayment = "card" }
                        )
                        Text("Банковская карта")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPayment == "cash",
                            onClick = { selectedPayment = "cash" }
                        )
                        Text("Наличные при получении")
                    }
                }
            }

            item {
                HorizontalDivider()
                Text(
                    "Ваш заказ",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(state.items) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.name} x${item.quantity}")
                    Text(String.format("%.2f ₽", item.price * item.quantity))
                }
            }
        }
    }
}