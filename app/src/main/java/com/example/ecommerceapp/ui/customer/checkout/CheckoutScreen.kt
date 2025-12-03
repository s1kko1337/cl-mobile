package com.example.ecommerceapp.ui.customer.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.ecommerceapp.data.model.DeliveryAddress

/**
 * Экран оформления заказа.
 *
 * Предоставляет форму для ввода данных доставки (имя, телефон, адрес),
 * выбора способа оплаты (карта/наличные), сохранения адреса доставки.
 * Поддерживает выбор адреса с карты и использование сохранённых адресов.
 * Автоматически заполняет адрес по умолчанию при загрузке.
 *
 * @param onNavigateBack Callback для возврата на предыдущий экран
 * @param onOrderComplete Callback, вызываемый при успешном оформлении заказа
 * @param onNavigateToMap Callback для перехода к выбору адреса на карте
 * @param savedStateHandle SavedStateHandle для получения данных с карты
 * @param viewModel ViewModel для управления оформлением заказа
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onOrderComplete: () -> Unit,
    onNavigateToMap: () -> Unit,
    savedStateHandle: SavedStateHandle,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedLatitude by remember { mutableStateOf<Double?>(null) }
    var selectedLongitude by remember { mutableStateOf<Double?>(null) }
    var selectedPayment by remember { mutableStateOf("card") }
    var showAddressSelector by remember { mutableStateOf(false) }
    var saveAddress by remember { mutableStateOf(false) }
    var setAsDefault by remember { mutableStateOf(false) }

    val selectedAddressFromMap = savedStateHandle.getStateFlow<String?>("selected_address", null).collectAsState()
    val selectedLatitudeFromMap = savedStateHandle.getStateFlow<Double?>("selected_latitude", null).collectAsState()
    val selectedLongitudeFromMap = savedStateHandle.getStateFlow<Double?>("selected_longitude", null).collectAsState()

    LaunchedEffect(selectedAddressFromMap.value) {
        selectedAddressFromMap.value?.let { mapAddress ->
            address = mapAddress
            selectedLatitudeFromMap.value?.let { selectedLatitude = it }
            selectedLongitudeFromMap.value?.let { selectedLongitude = it }
            savedStateHandle.remove<String>("selected_address")
            savedStateHandle.remove<Double>("selected_latitude")
            savedStateHandle.remove<Double>("selected_longitude")
        }
    }

    LaunchedEffect(state.savedAddresses) {
        val defaultAddress = state.savedAddresses.firstOrNull { it.isDefault }
        if (defaultAddress != null && name.isBlank()) {
            name = defaultAddress.name
            phone = defaultAddress.phone
            address = defaultAddress.address
            selectedLatitude = defaultAddress.latitude
            selectedLongitude = defaultAddress.longitude
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            viewModel.clearError()
        }
    }

    LaunchedEffect(state.orderCompleted) {
        if (state.orderCompleted) {
            onOrderComplete()
        }
    }

    // Диалог выбора адреса
    if (showAddressSelector) {
        AddressSelectorDialog(
            addresses = state.savedAddresses,
            onDismiss = { showAddressSelector = false },
            onSelect = { selectedAddress ->
                name = selectedAddress.name
                phone = selectedAddress.phone
                address = selectedAddress.address
                showAddressSelector = false
            }
        )
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
                            // Сохраняем адрес если нужно
                            if (saveAddress) {
                                viewModel.saveDeliveryAddress(
                                    name = name,
                                    phone = phone,
                                    address = address,
                                    latitude = selectedLatitude,
                                    longitude = selectedLongitude,
                                    setAsDefault = setAsDefault
                                )
                            }
                            // Оформляем заказ
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Контактная информация",
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (state.savedAddresses.isNotEmpty()) {
                        TextButton(onClick = { showAddressSelector = true }) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Выбрать адрес")
                        }
                    }
                }
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
                    minLines = 3,
                    trailingIcon = {
                        IconButton(onClick = onNavigateToMap) {
                            Icon(
                                Icons.Default.Map,
                                contentDescription = "Выбрать на карте",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }

            // Показываем координаты если адрес выбран на карте
            if (selectedLatitude != null && selectedLongitude != null) {
                item {
                    Text(
                        text = "Координаты: ${String.format("%.6f", selectedLatitude)}, ${String.format("%.6f", selectedLongitude)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Опции сохранения адреса
            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = saveAddress,
                            onCheckedChange = {
                                saveAddress = it
                                if (!it) setAsDefault = false
                            }
                        )
                        Text("Сохранить данные для следующих заказов")
                    }

                    if (saveAddress) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp)
                        ) {
                            Checkbox(
                                checked = setAsDefault,
                                onCheckedChange = { setAsDefault = it }
                            )
                            Text("Использовать по умолчанию")
                        }
                    }
                }
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

@Composable
fun AddressSelectorDialog(
    addresses: List<DeliveryAddress>,
    onDismiss: () -> Unit,
    onSelect: (DeliveryAddress) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите адрес доставки") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(addresses) { address ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(address) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = address.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (address.isDefault) {
                                    Text(
                                        text = "По умолчанию",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = address.phone,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = address.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}