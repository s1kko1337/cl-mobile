package com.example.ecommerceapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Типографика Material Design 3 для приложения.
 *
 * Определяет набор текстовых стилей, используемых в приложении.
 * Текущая конфигурация использует стандартный шрифт FontFamily.Default
 * с настройками для bodyLarge стиля.
 *
 * Дополнительные стили (titleLarge, labelSmall и др.) могут быть добавлены по необходимости.
 */
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)