package com.example.ecommerceapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

/**
 * Линейный график для дневных продаж.
 *
 * Отображает динамику продаж по дням с использованием Vico Charts.
 * Показывает заглушку, если данные отсутствуют.
 *
 * @param data Список пар (дата, сумма продаж)
 * @param modifier Модификатор компонента
 */
@Composable
fun DailySalesLineChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder(modifier)
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries {
                series(data.map { it.second })
            }
        }
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "График продаж",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis()
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            // Легенда с датами
            if (data.size <= 7) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    data.take(7).forEach { (date, _) ->
                        Text(
                            date.take(5), // MM-DD
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Столбчатая диаграмма помесячной выручки.
 *
 * Отображает выручку по месяцам в виде столбцов с использованием Vico Charts.
 * Показывает легенду с названиями месяцев и суммами (в тысячах).
 *
 * @param data Список троек (название месяца, год, сумма)
 * @param modifier Модификатор компонента
 */
@Composable
fun MonthlyRevenueBarChart(
    data: List<Triple<String, Int, Double>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder(modifier)
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries {
                series(data.map { it.third })
            }
        }
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Помесячная выручка",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis()
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            // Легенда с месяцами
            if (data.size <= 6) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    data.take(6).forEach { (month, year, revenue) ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                month.take(3),
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                String.format("%.0fк", revenue / 1000),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Горизонтальная столбчатая диаграмма топ товаров.
 *
 * Отображает топ-5 товаров по выручке с прогресс-барами разных цветов.
 * Показывает название (обрезанное до 20 символов) и выручку для каждого товара.
 *
 * @param data Список пар (название товара, выручка)
 * @param modifier Модификатор компонента
 */
@Composable
fun TopProductsHorizontalBarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder(modifier)
        return
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Топ товары по выручке",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val maxValue = data.maxOfOrNull { it.second } ?: 1.0

            data.take(5).forEachIndexed { index, (name, revenue) ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${index + 1}. ${name.take(20)}${if (name.length > 20) "..." else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            String.format("%.0f₽", revenue),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (revenue / maxValue).toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .height(8.dp),
                        color = when (index) {
                            0 -> MaterialTheme.colorScheme.primary
                            1 -> MaterialTheme.colorScheme.secondary
                            2 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primaryContainer
                        },
                    )
                }
            }
        }
    }
}

/**
 * Диаграмма продаж по категориям.
 *
 * Отображает распределение выручки по категориям товаров
 * с цветными индикаторами, процентами и суммами выручки.
 *
 * @param data Список пар (название категории, выручка)
 * @param modifier Модификатор компонента
 */
@Composable
fun CategorySalesPieChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder(modifier)
        return
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Продажи по категориям",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val totalRevenue = data.sumOf { it.second }
            val colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.tertiaryContainer
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                data.forEachIndexed { index, (category, revenue) ->
                    val percentage = (revenue / totalRevenue * 100)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = colors[index % colors.size],
                                modifier = Modifier.size(12.dp)
                            ) {}

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    category,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    String.format("%.1f%%", percentage),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            String.format("%.0f₽", revenue),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (index < data.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

/**
 * Диаграмма распределения по способам оплаты.
 *
 * Отображает статистику по способам оплаты (Card, Cash):
 * общее количество заказов, выручку, процентное соотношение
 * и прогресс-бары для визуализации долей.
 *
 * @param data Список троек (способ оплаты, количество заказов, выручка)
 * @param modifier Модификатор компонента
 */
@Composable
fun PaymentMethodsPieChart(
    data: List<Triple<String, Int, Double>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder(modifier)
        return
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Распределение по способам оплаты",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val totalRevenue = data.sumOf { it.third }
            val totalOrders = data.sumOf { it.second }
            val colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.tertiary
            )

            // Общая статистика
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Всего заказов",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "$totalOrders",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Выручка",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        String.format("%.0f₽", totalRevenue),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Детализация по способам оплаты
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                data.forEachIndexed { index, (method, orders, revenue) ->
                    val revenuePercentage = (revenue / totalRevenue * 100)
                    val ordersPercentage = (orders.toDouble() / totalOrders * 100)

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = colors[index % colors.size],
                                    modifier = Modifier.size(16.dp)
                                ) {}

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        when (method) {
                                            "Card" -> "Картой"
                                            "Cash" -> "Наличные"
                                            else -> method
                                        },
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        "$orders заказов (${String.format("%.1f%%", ordersPercentage)})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    String.format("%.0f₽", revenue),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    String.format("%.1f%%", revenuePercentage),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { (revenuePercentage / 100).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .height(8.dp),
                            color = colors[index % colors.size],
                        )
                    }

                    if (index < data.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
                    }
                }
            }
        }
    }
}

/**
 * Заглушка для пустого графика.
 *
 * Отображается когда данные для графика отсутствуют.
 *
 * @param modifier Модификатор компонента
 */
@Composable
private fun EmptyChartPlaceholder(modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Нет данных для отображения",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
