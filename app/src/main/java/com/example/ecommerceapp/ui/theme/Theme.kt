package com.example.ecommerceapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Цветовая схема для тёмной темы приложения.
 *
 * Использует светлые оттенки цветов (Purple80, PurpleGrey80, Pink80)
 * для обеспечения хорошей читаемости на тёмном фоне.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/**
 * Цветовая схема для светлой темы приложения.
 *
 * Использует тёмные оттенки цветов (Purple40, PurpleGrey40, Pink40)
 * для обеспечения хорошей читаемости на светлом фоне.
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/**
 * Главная composable функция темы приложения E-Commerce.
 *
 * Применяет Material Design 3 тему с поддержкой тёмного режима и динамических цветов (Android 12+).
 * Автоматически выбирает цветовую схему в зависимости от системных настроек и версии Android.
 *
 * Приоритеты выбора цветовой схемы:
 * 1. Динамические цвета системы (Android 12+), если включен dynamicColor
 * 2. Тёмная цветовая схема, если включена тёмная тема
 * 3. Светлая цветовая схема по умолчанию
 *
 * @param darkTheme Использовать ли тёмную тему (по умолчанию берётся из системных настроек)
 * @param dynamicColor Использовать ли динамические цвета Android 12+ (по умолчанию true)
 * @param content Composable содержимое приложения, к которому применяется тема
 */
@Composable
fun ECommerceAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}