package com.example.ecommerceapp.ui.admin.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecommerceapp.data.model.CategoryDTO
import com.example.ecommerceapp.data.model.ProductDTO

data class AdminProductEditState(
    val isLoading: Boolean = false,
    val product: ProductDTO? = null,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val stockQuantity: String = "",
    val sku: String = "",
    val categoryId: Int? = null,
    val categories: List<CategoryDTO> = emptyList(),
    val error: String? = null,
    val success: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductEditScreen(
    productId: Int,
    onNavigateBack: () -> Unit,
    viewModel: AdminProductEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // load product once when screen opens (productId could be same between recompositions)
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    // when update/delete success -> navigate back
    LaunchedEffect(state.success) {
        if (state.success) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование товара") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            Row(modifier = Modifier.padding(end = 16.dp)) {
                ExtendedFloatingActionButton(
                    text = { Text("Сохранить") },
                    icon = { Icon(Icons.Default.Save, contentDescription = null) },
                    onClick = { viewModel.updateProduct(productId) }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
        ) {
            when {
                state.isLoading && state.product == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.product == null -> {
                    // no product loaded, show message
                    Column {
                        Text("Товар не найден")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadProduct(productId) }) {
                            Text("Повторить")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { viewModel.onNameChange(it) },
                            label = { Text("Название") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.sku,
                            onValueChange = { viewModel.onSkuChange(it) },
                            label = { Text("SKU") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.description,
                            onValueChange = { viewModel.onDescriptionChange(it) },
                            label = { Text("Описание") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.price,
                            onValueChange = { viewModel.onPriceChange(it) },
                            label = { Text("Цена") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.stockQuantity,
                            onValueChange = { viewModel.onStockQuantityChange(it) },
                            label = { Text("Остаток") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))

                        // Category dropdown/simple selector
                        Text("Категория", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(4.dp))
                        val categories = state.categories
                        var expanded by remember { mutableStateOf(false) }
                        val selectedCategoryName =
                            categories.firstOrNull { it.id == state.categoryId }?.name ?: "Не выбрано"
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCategoryName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Категория") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.name) },
                                        onClick = {
                                            viewModel.onCategorySelected(cat.id)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Delete button
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Удалить товар")
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Error text
                        state.error?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            // Delete confirmation dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Удалить товар?") },
                    text = { Text("Вы уверены, что хотите удалить этот товар?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            viewModel.deleteProduct(productId)
                        }) {
                            Text("Удалить")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Отмена")
                        }
                    }
                )
            }
        }
    }
}
