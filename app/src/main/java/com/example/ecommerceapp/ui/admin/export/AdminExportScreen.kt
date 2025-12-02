package com.example.ecommerceapp.ui.admin.export

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Экран экспорта данных администратором.
 *
 * Позволяет экспортировать товары, заказы и складские запасы
 * в форматах CSV и Excel. Экспортированные файлы сохраняются
 * во внешнее хранилище приложения и могут быть открыты или расшарены.
 *
 * @param onNavigateBack Callback для возврата на предыдущий экран
 * @param viewModel ViewModel для управления экспортом данных
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminExportScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminExportViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.successMessage, state.error) {
        if (state.successMessage != null || state.error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Экспорт данных") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = {
            if (state.successMessage != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        if (state.exportedFile != null) {
                            TextButton(
                                onClick = {
                                    val file = state.exportedFile!!
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "*/*")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Открыть файл"))
                                }
                            ) {
                                Text("Открыть")
                            }
                        }
                    }
                ) {
                    Text(state.successMessage!!)
                }
            } else if (state.error != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Text(state.error!!)
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
                    "Выберите тип экспорта",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Text(
                    "Товары",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                ExportCard(
                    title = "Экспорт товаров (CSV)",
                    description = "Список всех товаров в формате CSV",
                    icon = Icons.Default.TableChart,
                    isLoading = state.isLoading,
                    onClick = { viewModel.exportProductsCsv() }
                )
            }

            item {
                ExportCard(
                    title = "Экспорт товаров (Excel)",
                    description = "Список всех товаров в формате Excel",
                    icon = Icons.Default.GridOn,
                    isLoading = state.isLoading,
                    onClick = { viewModel.exportProductsExcel() }
                )
            }

            item {
                Divider()
            }

            item {
                Text(
                    "Заказы",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                ExportCard(
                    title = "Экспорт заказов (CSV)",
                    description = "История заказов в формате CSV",
                    icon = Icons.Default.Receipt,
                    isLoading = state.isLoading,
                    onClick = { viewModel.exportOrdersCsv() }
                )
            }

            item {
                ExportCard(
                    title = "Экспорт заказов (Excel)",
                    description = "История заказов в формате Excel",
                    icon = Icons.Default.ShoppingCart,
                    isLoading = state.isLoading,
                    onClick = { viewModel.exportOrdersExcel() }
                )
            }

            item {
                Divider()
            }

            item {
                Text(
                    "Инвентаризация",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                ExportCard(
                    title = "Экспорт инвентаризации (CSV)",
                    description = "Остатки товаров на складе в формате CSV",
                    icon = Icons.Default.Inventory,
                    isLoading = state.isLoading,
                    onClick = { viewModel.exportInventoryCsv() }
                )
            }

            item {
                ExportCard(
                    title = "Экспорт инвентаризации (Excel)",
                    description = "Остатки товаров на складе в формате Excel",
                    icon = Icons.Default.Warehouse,
                    isLoading = state.isLoading,
                    onClick = { viewModel.exportInventoryExcel() }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Экспортированные файлы сохраняются в папку Downloads приложения",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Карточка экспорта данных.
 *
 * Отображает тип экспорта с иконкой, заголовком, описанием
 * и кнопкой для запуска экспорта. Показывает индикатор загрузки
 * во время процесса экспорта.
 *
 * @param title Заголовок типа экспорта
 * @param description Описание типа экспорта
 * @param icon Иконка типа экспорта
 * @param isLoading Флаг процесса загрузки
 * @param onClick Callback для запуска экспорта
 */
@Composable
fun ExportCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onClick,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Download, contentDescription = null)
                }
            }
        }
    }
}
