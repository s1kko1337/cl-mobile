package com.example.ecommerceapp.ui.customer.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Экран "О приложении".
 *
 * Отображает информацию о приложении: название, версию, описание,
 * список основных возможностей, используемые технологии и ссылку на GitHub.
 *
 * @param onNavigateBack Callback для возврата на предыдущий экран
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О приложении") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "ECommerce App",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Версия 1.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            Text(
                text = "Современное мобильное приложение для онлайн-покупок, разработанное на Kotlin с использованием Jetpack Compose и следующее принципам Clean Architecture.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Основные возможности:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    FeatureItem(
                        icon = Icons.Default.ShoppingBag,
                        text = "Удобный каталог товаров с категориями"
                    )
                    FeatureItem(
                        icon = Icons.Default.ShoppingCart,
                        text = "Корзина с сохранением данных"
                    )
                    FeatureItem(
                        icon = Icons.Default.Star,
                        text = "Отзывы и рейтинги товаров"
                    )
                    FeatureItem(
                        icon = Icons.Default.LocationOn,
                        text = "Сохранение адресов доставки"
                    )
                    FeatureItem(
                        icon = Icons.Default.AccountCircle,
                        text = "Личный кабинет и история заказов"
                    )
                    FeatureItem(
                        icon = Icons.Default.Build,
                        text = "Панель администратора"
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Технологии:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text("• Kotlin 2.2.20", style = MaterialTheme.typography.bodyMedium)
                    Text("• Jetpack Compose", style = MaterialTheme.typography.bodyMedium)
                    Text("• Material Design 3", style = MaterialTheme.typography.bodyMedium)
                    Text("• MVVM + Clean Architecture", style = MaterialTheme.typography.bodyMedium)
                    Text("• Hilt (Dependency Injection)", style = MaterialTheme.typography.bodyMedium)
                    Text("• Retrofit + OkHttp", style = MaterialTheme.typography.bodyMedium)
                    Text("• Room Database", style = MaterialTheme.typography.bodyMedium)
                    Text("• Coil для загрузки изображений", style = MaterialTheme.typography.bodyMedium)
                    Text("• DataStore для настроек", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://github.com/yourusername/ecommerce-app")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Code, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Исходный код на GitHub")
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Учебный проект",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "© 2025",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Элемент списка возможностей приложения.
 *
 * Отображает иконку и текстовое описание одной функциональности приложения.
 *
 * @param icon Иконка возможности
 * @param text Текстовое описание возможности
 */
@Composable
fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
