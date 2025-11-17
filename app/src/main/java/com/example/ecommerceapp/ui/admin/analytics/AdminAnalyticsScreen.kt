package com.example.ecommerceapp.ui.admin.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecommerceapp.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnalyticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminAnalyticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Продажи", "Товары", "Категории", "Оплата")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Аналитика") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Вкладки
            ScrollableTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ошибка: ${state.error}")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.refresh() }) {
                                Text("Повторить")
                            }
                        }
                    }
                }
                else -> {
                    when (selectedTab) {
                        0 -> SalesTab(state, viewModel)
                        1 -> TopProductsTab(state, viewModel)
                        2 -> CategorySalesTab(state)
                        3 -> PaymentMethodsTab(state)
                    }
                }
            }
        }
    }
}

@Composable
fun SalesTab(
    state: AdminAnalyticsState,
    viewModel: AdminAnalyticsViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Фильтр по дням
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Период: ${state.selectedDays} дней",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.selectedDays == 7,
                            onClick = { viewModel.setDaysFilter(7) },
                            label = { Text("7 дней") }
                        )
                        FilterChip(
                            selected = state.selectedDays == 30,
                            onClick = { viewModel.setDaysFilter(30) },
                            label = { Text("30 дней") }
                        )
                        FilterChip(
                            selected = state.selectedDays == 90,
                            onClick = { viewModel.setDaysFilter(90) },
                            label = { Text("90 дней") }
                        )
                    }
                }
            }
        }

        // График дневных продаж
        item {
            val chartData = state.dailySales.map {
                it.date.substring(5, 10) to it.totalRevenue
            }
            DailySalesLineChart(
                data = chartData,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Дневная статистика
        item {
            Text(
                "Дневная статистика продаж",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(state.dailySales.take(10)) { dailySale ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            dailySale.date.take(10),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            String.format("%.0f₽", dailySale.totalRevenue),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Заказов: ${dailySale.ordersCount}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Товаров: ${dailySale.itemsSold}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Средний чек:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                String.format("%.0f₽", dailySale.averageOrderValue),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }

        // Помесячная статистика
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Помесячная выручка",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // График помесячной выручки
        item {
            val monthlyChartData = state.monthlyRevenue.map {
                Triple(it.monthName, it.year, it.totalRevenue)
            }
            MonthlyRevenueBarChart(
                data = monthlyChartData,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(state.monthlyRevenue.take(6)) { monthly ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "${monthly.monthName} ${monthly.year}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Заказов: ${monthly.ordersCount} | Товаров: ${monthly.itemsSold}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        String.format("%.0f₽", monthly.totalRevenue),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun TopProductsTab(
    state: AdminAnalyticsState,
    viewModel: AdminAnalyticsViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Фильтр по количеству
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Показать: топ ${state.topProductsLimit}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.topProductsLimit == 5,
                            onClick = { viewModel.setTopProductsLimit(5) },
                            label = { Text("Топ 5") }
                        )
                        FilterChip(
                            selected = state.topProductsLimit == 10,
                            onClick = { viewModel.setTopProductsLimit(10) },
                            label = { Text("Топ 10") }
                        )
                        FilterChip(
                            selected = state.topProductsLimit == 20,
                            onClick = { viewModel.setTopProductsLimit(20) },
                            label = { Text("Топ 20") }
                        )
                    }
                }
            }
        }

        item {
            Text(
                "Самые продаваемые товары",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // График топ товаров
        item {
            val topProductsChartData = state.topProducts.map {
                it.productName to it.totalRevenue
            }
            TopProductsHorizontalBarChart(
                data = topProductsChartData,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(state.topProducts) { product ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Номер в топе
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${state.topProducts.indexOf(product) + 1}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            product.productName,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "Артикул: ${product.sku}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column {
                                Text(
                                    "Продано",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    "${product.totalQuantitySold} шт",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Column {
                                Text(
                                    "Выручка",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    String.format("%.0f₽", product.totalRevenue),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column {
                                Text(
                                    "Заказов",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    "${product.ordersCount}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySalesTab(state: AdminAnalyticsState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Продажи по категориям",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // График продаж по категориям
        item {
            val categoryChartData = state.categorySales.map {
                it.categoryName to it.totalRevenue
            }
            CategorySalesPieChart(
                data = categoryChartData,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(state.categorySales) { category ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            category.categoryName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            String.format("%.0f₽", category.totalRevenue),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Товаров: ${category.productsCount}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Продано: ${category.totalQuantitySold} шт",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Заказов: ${category.ordersCount}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Средняя цена:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                String.format("%.0f₽", category.averagePrice),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodsTab(state: AdminAnalyticsState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Способы оплаты",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // График способов оплаты
        item {
            val paymentChartData = state.paymentMethodStats.map {
                Triple(it.paymentMethod, it.ordersCount, it.totalRevenue)
            }
            PaymentMethodsPieChart(
                data = paymentChartData,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(state.paymentMethodStats) { payment ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                when (payment.paymentMethod) {
                                    "Card" -> Icons.Default.CreditCard
                                    else -> Icons.Default.AttachMoney
                                },
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    when (payment.paymentMethod) {
                                        "Card" -> "Картой"
                                        "Cash" -> "Наличные"
                                        else -> payment.paymentMethod
                                    },
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    String.format("%.1f%%", payment.percentage),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        Text(
                            String.format("%.0f₽", payment.totalRevenue),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = (payment.percentage / 100).toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Заказов: ${payment.ordersCount}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
