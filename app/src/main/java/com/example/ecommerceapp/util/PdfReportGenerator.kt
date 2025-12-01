package com.example.ecommerceapp.util

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.ecommerceapp.data.model.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Генератор PDF отчётов аналитики для админ-панели.
 *
 * Создаёт многостраничные PDF документы с визуализацией статистики продаж,
 * топовых товаров, продаж по категориям и способам оплаты.
 * Включает графики, таблицы и диаграммы.
 *
 * @property context Контекст приложения для доступа к ресурсам
 */
class PdfReportGenerator(private val context: Context) {

    companion object {
        private const val PAGE_WIDTH = 595
        private const val PAGE_HEIGHT = 842

        private const val MARGIN_LEFT = 40f
        private const val MARGIN_TOP = 40f
        private const val MARGIN_RIGHT = 40f
        private const val MARGIN_BOTTOM = 40f

        private const val TITLE_SIZE = 24f
        private const val HEADER_SIZE = 16f
        private const val SUBHEADER_SIZE = 14f
        private const val BODY_SIZE = 10f
        private const val SMALL_SIZE = 8f

        private val PRIMARY_COLOR = Color.parseColor("#6750A4")
        private val SECONDARY_COLOR = Color.parseColor("#625B71")
        private val TERTIARY_COLOR = Color.parseColor("#7D5260")
        private val SUCCESS_COLOR = Color.parseColor("#4CAF50")
        private val TEXT_COLOR = Color.parseColor("#1C1B1F")
        private val LIGHT_GRAY = Color.parseColor("#E7E0EC")
    }

    private lateinit var document: PdfDocument
    private var pageNumber = 0
    private var currentY = MARGIN_TOP
    private var currentPage: PdfDocument.Page? = null
    private var currentCanvas: Canvas? = null

    // Шрифты
    private val titlePaint = Paint().apply {
        color = TEXT_COLOR
        textSize = TITLE_SIZE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }

    private val headerPaint = Paint().apply {
        color = TEXT_COLOR
        textSize = HEADER_SIZE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }

    private val subHeaderPaint = Paint().apply {
        color = SECONDARY_COLOR
        textSize = SUBHEADER_SIZE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }

    private val bodyPaint = Paint().apply {
        color = TEXT_COLOR
        textSize = BODY_SIZE
        isAntiAlias = true
    }

    private val smallPaint = Paint().apply {
        color = SECONDARY_COLOR
        textSize = SMALL_SIZE
        isAntiAlias = true
    }

    private val valuePaint = Paint().apply {
        color = PRIMARY_COLOR
        textSize = BODY_SIZE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }

    private val chartPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val linePaint = Paint().apply {
        color = PRIMARY_COLOR
        strokeWidth = 2f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val gridPaint = Paint().apply {
        color = LIGHT_GRAY
        strokeWidth = 1f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    /**
     * Генерирует полный PDF отчёт со всей аналитикой.
     *
     * Создаёт многостраничный документ с разделами: продажи, топ товары,
     * категории и способы оплаты. Включает графики и таблицы.
     *
     * @param dailySales Дневная статистика продаж
     * @param monthlyRevenue Помесячная выручка
     * @param topProducts Список топовых продуктов
     * @param categorySales Продажи по категориям
     * @param paymentMethodStats Статистика по способам оплаты
     * @param selectedDays Период для анализа (количество дней)
     * @param topProductsLimit Максимальное количество топ товаров
     * @return Файл PDF отчёта или null в случае ошибки
     */
    fun generateAnalyticsReport(
        dailySales: List<DailySalesReportDTO>,
        monthlyRevenue: List<MonthlyRevenueDTO>,
        topProducts: List<TopProductDTO>,
        categorySales: List<CategorySalesDTO>,
        paymentMethodStats: List<PaymentMethodStatsDTO>,
        selectedDays: Int,
        topProductsLimit: Int
    ): File? {
        return try {
            document = PdfDocument()
            pageNumber = 0

            // Первая страница - Заголовок и Продажи
            startNewPage()
            drawTitle()
            drawSalesSection(dailySales, monthlyRevenue, selectedDays)

            // Вторая страница - Топ товары
            startNewPage()
            drawTopProductsSection(topProducts, topProductsLimit)

            // Третья страница - Категории и Способы оплаты
            startNewPage()
            drawCategorySalesSection(categorySales)
            drawPaymentMethodsSection(paymentMethodStats)

            // Завершаем последнюю страницу
            finishCurrentPage()

            // Сохранение файла
            val fileName = "analytics_report_${System.currentTimeMillis()}.pdf"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }

            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun startNewPage(): Canvas {
        // Завершаем предыдущую страницу, если она есть
        currentPage?.let { page ->
            document.finishPage(page)
        }

        pageNumber++
        currentY = MARGIN_TOP

        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        currentPage = document.startPage(pageInfo)
        currentCanvas = currentPage!!.canvas

        return currentCanvas!!
    }

    private fun getCurrentPage(): Canvas {
        return currentCanvas ?: startNewPage()
    }

    private fun finishCurrentPage() {
        currentPage?.let { page ->
            document.finishPage(page)
            currentPage = null
            currentCanvas = null
        }
    }

    private fun ensureSpace(needed: Float): Canvas {
        if (currentY + needed > PAGE_HEIGHT - MARGIN_BOTTOM) {
            finishCurrentPage()
            return startNewPage()
        }
        return getCurrentPage()
    }

    private fun drawTitle() {
        val canvas = getCurrentPage()

        // Заголовок отчета
        canvas.drawText("Аналитический отчет", MARGIN_LEFT, currentY + TITLE_SIZE, titlePaint)
        currentY += TITLE_SIZE + 10

        // Дата генерации
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val dateText = "Дата создания: ${dateFormat.format(Date())}"
        canvas.drawText(dateText, MARGIN_LEFT, currentY + SMALL_SIZE, smallPaint)
        currentY += SMALL_SIZE + 20

        // Разделитель
        canvas.drawLine(MARGIN_LEFT, currentY, PAGE_WIDTH - MARGIN_RIGHT, currentY, gridPaint)
        currentY += 20
    }

    private fun drawSalesSection(
        dailySales: List<DailySalesReportDTO>,
        monthlyRevenue: List<MonthlyRevenueDTO>,
        selectedDays: Int
    ) {
        val canvas = getCurrentPage()

        // Заголовок секции
        canvas.drawText("Продажи за $selectedDays дней", MARGIN_LEFT, currentY + HEADER_SIZE, headerPaint)
        currentY += HEADER_SIZE + 20

        // График дневных продаж
        if (dailySales.isNotEmpty()) {
            drawLineChart(canvas, dailySales.takeLast(14))
            currentY += 15

            // Сводная статистика
            val totalRevenue = dailySales.sumOf { it.totalRevenue }
            val totalOrders = dailySales.sumOf { it.ordersCount }
            val totalItems = dailySales.sumOf { it.itemsSold }
            val avgOrder = if (totalOrders > 0) totalRevenue / totalOrders else 0.0

            drawStatCard(canvas, "Общая выручка", String.format("%.0f ₽", totalRevenue), PRIMARY_COLOR)
            drawStatCard(canvas, "Заказов", "$totalOrders", SECONDARY_COLOR)
            drawStatCard(canvas, "Товаров продано", "$totalItems", TERTIARY_COLOR)
            drawStatCard(canvas, "Средний чек", String.format("%.0f ₽", avgOrder), SUCCESS_COLOR)

            currentY += 15
        }

        // Помесячная выручка
        if (monthlyRevenue.isNotEmpty()) {
            canvas.drawText("Помесячная выручка", MARGIN_LEFT, currentY + SUBHEADER_SIZE, subHeaderPaint)
            currentY += SUBHEADER_SIZE + 15

            drawBarChart(canvas, monthlyRevenue.take(6))
            currentY += 15

            // Таблица помесячной выручки
            monthlyRevenue.take(6).forEach { monthly ->
                val text = "${monthly.monthName} ${monthly.year}"
                val value = String.format("%.0f ₽ (%d заказов)", monthly.totalRevenue, monthly.ordersCount)

                canvas.drawText(text, MARGIN_LEFT, currentY + BODY_SIZE, bodyPaint)
                val valueWidth = valuePaint.measureText(value)
                canvas.drawText(value, PAGE_WIDTH - MARGIN_RIGHT - valueWidth, currentY + BODY_SIZE, valuePaint)
                currentY += BODY_SIZE + 8
            }
        }
    }

    private fun drawTopProductsSection(topProducts: List<TopProductDTO>, limit: Int) {
        val canvas = getCurrentPage()

        // Заголовок секции
        canvas.drawText("Топ-$limit товаров", MARGIN_LEFT, currentY + HEADER_SIZE, headerPaint)
        currentY += HEADER_SIZE + 20

        if (topProducts.isEmpty()) {
            canvas.drawText("Нет данных", MARGIN_LEFT, currentY + BODY_SIZE, bodyPaint)
            currentY += BODY_SIZE + 20
            return
        }

        // Горизонтальная столбчатая диаграмма
        drawHorizontalBarChart(canvas, topProducts.take(10))
        currentY += 20

        // Детальная таблица
        canvas.drawText("Детализация:", MARGIN_LEFT, currentY + SUBHEADER_SIZE, subHeaderPaint)
        currentY += SUBHEADER_SIZE + 15

        // Заголовок таблицы
        val colWidths = listOf(30f, 180f, 80f, 80f, 80f)
        val headers = listOf("№", "Товар", "Продано", "Выручка", "Заказов")

        var x = MARGIN_LEFT
        headers.forEachIndexed { index, header ->
            canvas.drawText(header, x, currentY + SMALL_SIZE, smallPaint)
            x += colWidths[index]
        }
        currentY += SMALL_SIZE + 8

        // Разделитель
        canvas.drawLine(MARGIN_LEFT, currentY, PAGE_WIDTH - MARGIN_RIGHT, currentY, gridPaint)
        currentY += 5

        // Строки таблицы
        topProducts.take(10).forEachIndexed { index, product ->
            x = MARGIN_LEFT

            // Номер
            canvas.drawText("${index + 1}", x, currentY + BODY_SIZE, bodyPaint)
            x += colWidths[0]

            // Название (обрезаем если длинное)
            val name = if (product.productName.length > 25) {
                product.productName.take(22) + "..."
            } else {
                product.productName
            }
            canvas.drawText(name, x, currentY + BODY_SIZE, bodyPaint)
            x += colWidths[1]

            // Количество продано
            canvas.drawText("${product.totalQuantitySold} шт", x, currentY + BODY_SIZE, bodyPaint)
            x += colWidths[2]

            // Выручка
            canvas.drawText(String.format("%.0f₽", product.totalRevenue), x, currentY + BODY_SIZE, valuePaint)
            x += colWidths[3]

            // Заказов
            canvas.drawText("${product.ordersCount}", x, currentY + BODY_SIZE, bodyPaint)

            currentY += BODY_SIZE + 10
        }
    }

    private fun drawCategorySalesSection(categorySales: List<CategorySalesDTO>) {
        val canvas = getCurrentPage()

        // Заголовок секции
        canvas.drawText("Продажи по категориям", MARGIN_LEFT, currentY + HEADER_SIZE, headerPaint)
        currentY += HEADER_SIZE + 20

        if (categorySales.isEmpty()) {
            canvas.drawText("Нет данных", MARGIN_LEFT, currentY + BODY_SIZE, bodyPaint)
            currentY += BODY_SIZE + 20
            return
        }

        // Круговая диаграмма
        drawPieChart(canvas, categorySales.map { it.categoryName to it.totalRevenue })
        currentY += 15

        // Детальная информация
        val totalRevenue = categorySales.sumOf { it.totalRevenue }

        categorySales.forEach { category ->
            val percentage = if (totalRevenue > 0) category.totalRevenue / totalRevenue * 100 else 0.0

            // Название категории
            canvas.drawText(category.categoryName, MARGIN_LEFT, currentY + BODY_SIZE, bodyPaint)

            // Выручка и процент
            val valueText = String.format("%.0f₽ (%.1f%%)", category.totalRevenue, percentage)
            val valueWidth = valuePaint.measureText(valueText)
            canvas.drawText(valueText, PAGE_WIDTH - MARGIN_RIGHT - valueWidth, currentY + BODY_SIZE, valuePaint)
            currentY += BODY_SIZE + 5

            // Детали
            val details = "Товаров: ${category.productsCount} | Продано: ${category.totalQuantitySold} шт | Заказов: ${category.ordersCount}"
            canvas.drawText(details, MARGIN_LEFT + 10, currentY + SMALL_SIZE, smallPaint)
            currentY += SMALL_SIZE + 10
        }

        currentY += 10
    }

    private fun drawPaymentMethodsSection(paymentMethodStats: List<PaymentMethodStatsDTO>) {
        val canvas = ensureSpace(150f)

        // Заголовок секции
        canvas.drawText("Способы оплаты", MARGIN_LEFT, currentY + HEADER_SIZE, headerPaint)
        currentY += HEADER_SIZE + 20

        if (paymentMethodStats.isEmpty()) {
            canvas.drawText("Нет данных", MARGIN_LEFT, currentY + BODY_SIZE, bodyPaint)
            currentY += BODY_SIZE + 20
            return
        }

        val totalRevenue = paymentMethodStats.sumOf { it.totalRevenue }
        val totalOrders = paymentMethodStats.sumOf { it.ordersCount }

        // Общая статистика
        canvas.drawText("Всего заказов: $totalOrders", MARGIN_LEFT, currentY + BODY_SIZE, bodyPaint)
        val revenueText = String.format("Общая выручка: %.0f₽", totalRevenue)
        val revenueWidth = valuePaint.measureText(revenueText)
        canvas.drawText(revenueText, PAGE_WIDTH - MARGIN_RIGHT - revenueWidth, currentY + BODY_SIZE, valuePaint)
        currentY += BODY_SIZE + 15

        // Разделитель
        canvas.drawLine(MARGIN_LEFT, currentY, PAGE_WIDTH - MARGIN_RIGHT, currentY, gridPaint)
        currentY += 15

        // Детализация по способам оплаты
        val barColors = listOf(PRIMARY_COLOR, SECONDARY_COLOR, TERTIARY_COLOR)

        paymentMethodStats.forEachIndexed { index, payment ->
            val methodName = when (payment.paymentMethod) {
                "Card" -> "Картой"
                "Cash" -> "Наличные"
                else -> payment.paymentMethod
            }

            // Название способа оплаты
            canvas.drawText(methodName, MARGIN_LEFT, currentY + BODY_SIZE, bodyPaint)

            // Выручка
            val valueText = String.format("%.0f₽ (%.1f%%)", payment.totalRevenue, payment.percentage)
            val valueWidth = valuePaint.measureText(valueText)
            canvas.drawText(valueText, PAGE_WIDTH - MARGIN_RIGHT - valueWidth, currentY + BODY_SIZE, valuePaint)
            currentY += BODY_SIZE + 5

            // Прогресс-бар
            val barWidth = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT
            val filledWidth = barWidth * (payment.percentage / 100f).toFloat()

            // Фон бара
            chartPaint.color = LIGHT_GRAY
            canvas.drawRect(MARGIN_LEFT, currentY, MARGIN_LEFT + barWidth, currentY + 8, chartPaint)

            // Заполненная часть
            chartPaint.color = barColors[index % barColors.size]
            canvas.drawRect(MARGIN_LEFT, currentY, MARGIN_LEFT + filledWidth, currentY + 8, chartPaint)

            currentY += 8 + 5

            // Количество заказов
            canvas.drawText("Заказов: ${payment.ordersCount}", MARGIN_LEFT + 10, currentY + SMALL_SIZE, smallPaint)
            currentY += SMALL_SIZE + 15
        }
    }

    // Вспомогательные функции для рисования графиков

    private fun drawLineChart(canvas: Canvas, data: List<DailySalesReportDTO>) {
        if (data.isEmpty()) return

        val chartLeft = MARGIN_LEFT + 30
        val chartTop = currentY
        val chartWidth = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT - 30
        val chartHeight = 100f

        // Находим максимальное значение
        val maxValue = data.maxOfOrNull { it.totalRevenue } ?: 1.0

        // Рисуем оси
        canvas.drawLine(chartLeft, chartTop + chartHeight, chartLeft + chartWidth, chartTop + chartHeight, gridPaint)
        canvas.drawLine(chartLeft, chartTop, chartLeft, chartTop + chartHeight, gridPaint)

        // Рисуем линию графика
        val path = Path()
        data.forEachIndexed { index, sale ->
            val x = chartLeft + (index.toFloat() / (data.size - 1).coerceAtLeast(1)) * chartWidth
            val y = chartTop + chartHeight - (sale.totalRevenue / maxValue * chartHeight).toFloat()

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }

            // Точка
            chartPaint.color = PRIMARY_COLOR
            canvas.drawCircle(x, y, 3f, chartPaint)
        }

        linePaint.color = PRIMARY_COLOR
        canvas.drawPath(path, linePaint)

        currentY += chartHeight + 20
    }

    private fun drawBarChart(canvas: Canvas, data: List<MonthlyRevenueDTO>) {
        if (data.isEmpty()) return

        val chartLeft = MARGIN_LEFT
        val chartTop = currentY
        val chartWidth = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT
        val chartHeight = 80f

        val maxValue = data.maxOfOrNull { it.totalRevenue } ?: 1.0
        val barWidth = chartWidth / data.size * 0.7f
        val gap = chartWidth / data.size * 0.3f

        data.forEachIndexed { index, monthly ->
            val barHeight = (monthly.totalRevenue / maxValue * chartHeight).toFloat()
            val x = chartLeft + index * (barWidth + gap) + gap / 2

            // Столбец
            chartPaint.color = PRIMARY_COLOR
            canvas.drawRect(x, chartTop + chartHeight - barHeight, x + barWidth, chartTop + chartHeight, chartPaint)

            // Подпись месяца
            val monthLabel = monthly.monthName.take(3)
            val labelWidth = smallPaint.measureText(monthLabel)
            canvas.drawText(monthLabel, x + barWidth / 2 - labelWidth / 2, chartTop + chartHeight + SMALL_SIZE + 5, smallPaint)
        }

        currentY += chartHeight + SMALL_SIZE + 20
    }

    private fun drawHorizontalBarChart(canvas: Canvas, data: List<TopProductDTO>) {
        if (data.isEmpty()) return

        val maxRevenue = data.maxOfOrNull { it.totalRevenue } ?: 1.0
        val barHeight = 12f
        val barMaxWidth = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT - 150

        val colors = listOf(PRIMARY_COLOR, SECONDARY_COLOR, TERTIARY_COLOR, SUCCESS_COLOR, Color.GRAY)

        data.take(5).forEachIndexed { index, product ->
            val barWidth = (product.totalRevenue / maxRevenue * barMaxWidth).toFloat()

            // Название товара (обрезаем)
            val name = if (product.productName.length > 20) {
                product.productName.take(17) + "..."
            } else {
                product.productName
            }
            canvas.drawText("${index + 1}. $name", MARGIN_LEFT, currentY + BODY_SIZE, bodyPaint)
            currentY += BODY_SIZE + 3

            // Столбец
            chartPaint.color = colors[index % colors.size]
            canvas.drawRect(MARGIN_LEFT, currentY, MARGIN_LEFT + barWidth, currentY + barHeight, chartPaint)

            // Значение
            val valueText = String.format("%.0f₽", product.totalRevenue)
            canvas.drawText(valueText, MARGIN_LEFT + barWidth + 5, currentY + barHeight - 2, smallPaint)

            currentY += barHeight + 8
        }
    }

    private fun drawPieChart(canvas: Canvas, data: List<Pair<String, Double>>) {
        if (data.isEmpty()) return

        val centerX = MARGIN_LEFT + 60
        val centerY = currentY + 50
        val radius = 40f

        val total = data.sumOf { it.second }
        var startAngle = -90f

        val colors = listOf(PRIMARY_COLOR, SECONDARY_COLOR, TERTIARY_COLOR, SUCCESS_COLOR, Color.GRAY, Color.CYAN)

        data.forEachIndexed { index, (_, value) ->
            val sweepAngle = ((value / total) * 360).toFloat()

            chartPaint.color = colors[index % colors.size]
            canvas.drawArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle,
                sweepAngle,
                true,
                chartPaint
            )

            startAngle += sweepAngle
        }

        // Легенда справа от диаграммы
        val legendX = centerX + radius + 30
        var legendY = currentY + 10

        data.take(6).forEachIndexed { index, (name, value) ->
            val percentage = if (total > 0) value / total * 100 else 0.0

            // Цветной квадрат
            chartPaint.color = colors[index % colors.size]
            canvas.drawRect(legendX, legendY, legendX + 8, legendY + 8, chartPaint)

            // Текст
            val legendText = "${name.take(15)} (${String.format("%.0f%%", percentage)})"
            canvas.drawText(legendText, legendX + 12, legendY + 7, smallPaint)

            legendY += 15
        }

        currentY += 120
    }

    private fun drawStatCard(canvas: Canvas, label: String, value: String, color: Int) {
        val cardWidth = (PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT - 30) / 2
        val cardHeight = 35f

        val isEven = ((currentY.toInt() / 50) % 2 == 0)
        val x = if (isEven) MARGIN_LEFT else MARGIN_LEFT + cardWidth + 15

        // Фон карточки
        chartPaint.color = Color.argb(30, Color.red(color), Color.green(color), Color.blue(color))
        canvas.drawRoundRect(x, currentY, x + cardWidth, currentY + cardHeight, 5f, 5f, chartPaint)

        // Метка
        canvas.drawText(label, x + 8, currentY + 12, smallPaint)

        // Значение
        valuePaint.color = color
        canvas.drawText(value, x + 8, currentY + 28, valuePaint)
        valuePaint.color = PRIMARY_COLOR

        if (!isEven) {
            currentY += cardHeight + 8
        }
    }
}
