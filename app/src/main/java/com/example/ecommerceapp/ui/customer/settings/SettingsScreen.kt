package com.example.ecommerceapp.ui.customer.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.ecommerceapp.data.model.DeliveryAddress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit,
    savedStateHandle: SavedStateHandle,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<Pair<Int, DeliveryAddress>?>(null) }

    // Данные с карты
    var pendingMapAddress by remember { mutableStateOf<String?>(null) }
    var pendingMapLatitude by remember { mutableStateOf<Double?>(null) }
    var pendingMapLongitude by remember { mutableStateOf<Double?>(null) }

    // Получаем данные с карты
    val selectedAddressFromMap = savedStateHandle.getStateFlow<String?>("selected_address", null).collectAsState()
    val selectedLatitudeFromMap = savedStateHandle.getStateFlow<Double?>("selected_latitude", null).collectAsState()
    val selectedLongitudeFromMap = savedStateHandle.getStateFlow<Double?>("selected_longitude", null).collectAsState()

    // Обновляем временные переменные когда получаем данные с карты
    LaunchedEffect(selectedAddressFromMap.value) {
        selectedAddressFromMap.value?.let { mapAddress ->
            pendingMapAddress = mapAddress
            pendingMapLatitude = selectedLatitudeFromMap.value
            pendingMapLongitude = selectedLongitudeFromMap.value
            // Показываем диалог
            showAddDialog = true
            // Очищаем данные
            savedStateHandle.remove<String>("selected_address")
            savedStateHandle.remove<Double>("selected_latitude")
            savedStateHandle.remove<Double>("selected_longitude")
        }
    }

    if (showAddDialog) {
        AddressDialog(
            title = "Добавить адрес",
            initialAddress = if (pendingMapAddress != null) {
                DeliveryAddress(
                    name = "",
                    phone = "",
                    address = pendingMapAddress ?: "",
                    latitude = pendingMapLatitude,
                    longitude = pendingMapLongitude,
                    isDefault = false
                )
            } else null,
            onDismiss = {
                showAddDialog = false
                pendingMapAddress = null
                pendingMapLatitude = null
                pendingMapLongitude = null
            },
            onConfirm = { address ->
                viewModel.addAddress(address)
                showAddDialog = false
                pendingMapAddress = null
                pendingMapLatitude = null
                pendingMapLongitude = null
            },
            onNavigateToMap = onNavigateToMap
        )
    }

    editingAddress?.let { (index, address) ->
        AddressDialog(
            title = "Редактировать адрес",
            initialAddress = address,
            onDismiss = { editingAddress = null },
            onConfirm = { updatedAddress ->
                viewModel.updateAddress(index, updatedAddress)
                editingAddress = null
            },
            onNavigateToMap = onNavigateToMap
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить адрес")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Адреса доставки",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (state.addresses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Нет сохраненных адресов",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(state.addresses) { index, address ->
                    AddressCard(
                        address = address,
                        onEdit = { editingAddress = index to address },
                        onDelete = { viewModel.deleteAddress(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun AddressCard(
    address: DeliveryAddress,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить адрес?") },
            text = { Text("Вы уверены, что хотите удалить этот адрес доставки?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
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

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = address.name,
                    style = MaterialTheme.typography.titleMedium
                )
                if (address.isDefault) {
                    AssistChip(
                        onClick = {},
                        label = { Text("По умолчанию") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = address.phone,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = address.address,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Изменить")
                }
                TextButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AddressDialog(
    title: String,
    initialAddress: DeliveryAddress? = null,
    onDismiss: () -> Unit,
    onConfirm: (DeliveryAddress) -> Unit,
    onNavigateToMap: () -> Unit
) {
    var name by remember { mutableStateOf(initialAddress?.name ?: "") }
    var phone by remember { mutableStateOf(initialAddress?.phone ?: "") }
    var address by remember { mutableStateOf(initialAddress?.address ?: "") }
    var latitude by remember { mutableStateOf(initialAddress?.latitude) }
    var longitude by remember { mutableStateOf(initialAddress?.longitude) }
    var isDefault by remember { mutableStateOf(initialAddress?.isDefault ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя получателя") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

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

                // Показываем координаты если есть
                if (latitude != null && longitude != null) {
                    Text(
                        text = "Координаты: ${String.format("%.6f", latitude)}, ${String.format("%.6f", longitude)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it }
                    )
                    Text("Использовать по умолчанию")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()) {
                        onConfirm(
                            DeliveryAddress(
                                name = name,
                                phone = phone,
                                address = address,
                                latitude = latitude,
                                longitude = longitude,
                                isDefault = isDefault
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
