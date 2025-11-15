package com.example.ecommerceapp.ui.admin.dashboard

// ИЗМЕНЕНО: Добавлены импорты для verticalScroll и rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val username by viewModel.username.collectAsState(initial = "")
    var showLogoutDialog by remember { mutableStateOf(false) }
    val dashboard = state.dashboardSummary

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Выход") },
            text = { Text("Вы уверены, что хотите выйти?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        onLogout()
                    }
                ) {
                    Text("Выйти")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Админ панель") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Выйти")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading && dashboard == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null && dashboard == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ошибка: ${state.error}")
                }
            }
            dashboard != null -> {
                // ИЗМЕНЕНО: Добавили verticalScroll
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState()) // <--- ВОТ РЕШЕНИЕ
                        .padding(16.dp)
                ) {
                    // Приветствие
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
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Добро пожаловать, $username",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "Администратор",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Алерты
                    state.alerts.forEach { alert ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = when (alert.type) {
                                    "error" -> MaterialTheme.colorScheme.errorContainer
                                    "warning" -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.secondaryContainer
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    when (alert.type) {
                                        "error" -> Icons.Default.Error
                                        "warning" -> Icons.Default.Warning
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = when (alert.type) {
                                        "error" -> MaterialTheme.colorScheme.error
                                        "warning" -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.secondary
                                    }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        alert.message,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "Количество: ${alert.count}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (state.alerts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Общая статистика
                    Text(
                        text = "Общая статистика",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Товары",
                            value = dashboard.totalProducts.toString(),
                            icon = Icons.Default.Inventory,
                            onClick = onNavigateToProducts // ИЗМЕНЕНО: Добавлена навигация
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Заказы",
                            value = dashboard.totalOrders.toString(),
                            icon = Icons.Default.ShoppingCart,
                            onClick = onNavigateToOrders // ИЗМЕНЕНО: Добавлена навигация
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Пользователи",
                            value = dashboard.totalUsers.toString(),
                            icon = Icons.Default.Person,
                            onClick = null // Не кликабельно
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Выручка",
                            value = String.format("%.0f₽", dashboard.totalRevenue),
                            icon = Icons.Default.AttachMoney,
                            onClick = onNavigateToAnalytics // ИЗМЕНЕНО: Добавлена навигация
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Статистика за периоды
                    Text(
                        text = "За период",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MiniStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Сегодня",
                            orders = dashboard.todayOrders,
                            revenue = dashboard.todayRevenue
                        )
                        MiniStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Неделя",
                            orders = dashboard.weekOrders,
                            revenue = dashboard.weekRevenue
                        )
                        MiniStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Месяц",
                            orders = dashboard.monthOrders,
                            revenue = dashboard.monthRevenue
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Управление
                    Text(
                        text = "Управление",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ИЗМЕНЕНО: Заменили LazyVerticalGrid на обычные Row
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MenuCard(
                                modifier = Modifier.weight(1f),
                                title = "Товары",
                                icon = Icons.Default.Inventory,
                                onClick = onNavigateToProducts
                            )
                            MenuCard(
                                modifier = Modifier.weight(1f),
                                title = "Категории",
                                icon = Icons.Default.Category,
                                onClick = onNavigateToCategories
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MenuCard(
                                modifier = Modifier.weight(1f),
                                title = "Заказы",
                                icon = Icons.Default.ShoppingCart,
                                onClick = onNavigateToOrders
                            )
                            MenuCard(
                                modifier = Modifier.weight(1f),
                                title = "Отзывы",
                                icon = Icons.Default.Star,
                                onClick = onNavigateToReviews
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MenuCard(
                                modifier = Modifier.weight(1f),
                                title = "Аналитика",
                                icon = Icons.Default.Analytics,
                                onClick = onNavigateToAnalytics
                            )
                            // Пустой Spacer, чтобы последняя карточка
                            // не растягивалась, если их нечетное число
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// ИЗМЕНЕНО: Добавлен необязательный параметр onClick
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        onClick = { onClick?.invoke() }, // <--- Используем onClick
        enabled = onClick != null // <--- Карточка активна только если есть onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ИЗМЕНЕНО: Используем onClick самой карточки
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCard(
    modifier: Modifier = Modifier, // <--- Добавлен Modifier
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick, // <--- Используем onClick
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// Компонент MiniStatCard без изменений
@Composable
fun MiniStatCard(
    modifier: Modifier = Modifier,
    title: String,
    orders: Int,
    revenue: Double
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = orders.toString(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = String.format("%.0f₽", revenue),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}