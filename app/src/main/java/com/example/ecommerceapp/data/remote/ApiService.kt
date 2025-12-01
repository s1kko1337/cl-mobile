package com.example.ecommerceapp.data.remote

import com.example.ecommerceapp.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit интерфейс для взаимодействия с REST API сервера.
 *
 * Определяет все эндпоинты API для работы с аутентификацией, категориями,
 * продуктами, заказами, отзывами, а также административными отчётами и экспортом данных.
 * Все методы являются suspend функциями для работы с Kotlin Coroutines.
 */
interface ApiService {

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param request Данные для регистрации (email, пароль, имя и т.д.)
     * @return Response с Unit в случае успешной регистрации
     */
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param request Данные для входа (email, пароль)
     * @return Response с AuthResponse, содержащим токен и данные пользователя
     */
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    /**
     * Изменяет пароль текущего пользователя.
     *
     * @param request Данные с текущим и новым паролем
     * @return Response с Unit в случае успешного изменения пароля
     */
    @POST("change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Unit>

    /**
     * Получает список всех категорий продуктов.
     *
     * @return Response со списком CategoryDTO
     */
    @GET("api/categories")
    suspend fun getCategories(): Response<List<CategoryDTO>>

    /**
     * Получает информацию о конкретной категории по ID.
     *
     * @param id ID категории
     * @return Response с CategoryDTO
     */
    @GET("api/categories/{id}")
    suspend fun getCategory(@Path("id") id: Int): Response<CategoryDTO>

    /**
     * Создаёт новую категорию продуктов.
     *
     * @param category Данные для создания категории
     * @return Response с созданным CategoryDTO
     */
    @POST("api/categories")
    suspend fun createCategory(@Body category: CategoryCreateDTO): Response<CategoryDTO>

    /**
     * Обновляет существующую категорию.
     *
     * @param id ID категории для обновления
     * @param category Данные для обновления категории
     * @return Response с Unit в случае успешного обновления
     */
    @PUT("api/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body category: CategoryUpdateDTO
    ): Response<Unit>

    /**
     * Удаляет категорию по ID.
     *
     * @param id ID категории для удаления
     * @return Response с Unit в случае успешного удаления
     */
    @DELETE("api/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Response<Unit>

    /**
     * Получает список всех продуктов в указанной категории.
     *
     * @param id ID категории
     * @return Response со списком ProductDTO
     */
    @GET("api/categories/{id}/products")
    suspend fun getCategoryProducts(@Path("id") id: Int): Response<List<ProductDTO>>

    /**
     * Получает список всех продуктов в системе.
     *
     * @return Response со списком ProductDTO
     */
    @GET("api/products")
    suspend fun getProducts(): Response<List<ProductDTO>>

    /**
     * Получает информацию о конкретном продукте по ID.
     *
     * @param id ID продукта
     * @return Response с ProductDTO
     */
    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductDTO>

    /**
     * Создаёт новый продукт в системе.
     *
     * @param product Данные для создания продукта
     * @return Response с созданным ProductDTO
     */
    @POST("api/products")
    suspend fun createProduct(@Body product: ProductCreateDTO): Response<ProductDTO>

    /**
     * Обновляет информацию о существующем продукте.
     *
     * @param id ID продукта для обновления
     * @param product Данные для обновления продукта
     * @return Response с Unit в случае успешного обновления
     */
    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: ProductUpdateDTO
    ): Response<Unit>

    /**
     * Удаляет продукт из системы.
     *
     * @param id ID продукта для удаления
     * @return Response с Unit в случае успешного удаления
     */
    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>

    /**
     * Получает список всех изображений продукта.
     *
     * @param productId ID продукта
     * @return Response со списком ProductImageDTO
     */
    @GET("api/products/{productId}/images")
    suspend fun getProductImages(@Path("productId") productId: Int): Response<List<ProductImageDTO>>

    /**
     * Загружает новое изображение для продукта.
     *
     * @param productId ID продукта
     * @param file Multipart файл изображения
     * @param altText Альтернативный текст для изображения (опционально)
     * @return Response с созданным ProductImageDTO
     */
    @Multipart
    @POST("api/products/{productId}/images")
    suspend fun uploadProductImage(
        @Path("productId") productId: Int,
        @Part file: MultipartBody.Part,
        @Part("altText") altText: RequestBody?
    ): Response<ProductImageDTO>

    /**
     * Удаляет изображение продукта.
     *
     * @param productId ID продукта
     * @param id ID изображения для удаления
     * @return Response с Unit в случае успешного удаления
     */
    @DELETE("api/products/{productId}/images/{id}")
    suspend fun deleteProductImage(
        @Path("productId") productId: Int,
        @Path("id") id: Int
    ): Response<Unit>

    /**
     * Получает информацию о всех изображениях продукта (без загрузки самих изображений).
     *
     * @param productId ID продукта
     * @return Response со списком ProductImageDTO с метаданными
     */
    @GET("api/products/{productId}/images/info")
    suspend fun getProductImagesInfo(@Path("productId") productId: Int): Response<List<ProductImageDTO>>

    /**
     * Загружает изображение продукта в виде потока байтов.
     *
     * @param productId ID продукта
     * @param imageId ID изображения
     * @return Response с ResponseBody (поток данных изображения)
     */
    @GET("api/products/{productId}/images/{id}/download")
    @Streaming
    suspend fun downloadProductImage(
        @Path("productId") productId: Int,
        @Path("id") imageId: Int
    ): Response<ResponseBody>

    /**
     * Загружает изображение отзыва в виде потока байтов.
     *
     * @param productId ID продукта
     * @param reviewId ID отзыва
     * @return Response с ResponseBody (поток данных изображения)
     */
    @GET("api/products/{productId}/reviews/{id}/image/download")
    @Streaming
    suspend fun downloadReviewImage(
        @Path("productId") productId: Int,
        @Path("id") reviewId: Int
    ): Response<ResponseBody>

    /**
     * Получает все отзывы на продукт.
     *
     * @param productId ID продукта
     * @return Response со списком ProductReviewDTO
     */
    @GET("api/products/{productId}/reviews")
    suspend fun getProductReviews(@Path("productId") productId: Int): Response<List<ProductReviewDTO>>

    /**
     * Создаёт новый отзыв на продукт.
     *
     * @param productId ID продукта
     * @param review Данные для создания отзыва
     * @return Response с созданным ProductReviewDTO
     */
    @POST("api/products/{productId}/reviews")
    suspend fun createReview(
        @Path("productId") productId: Int,
        @Body review: ProductReviewCreateDTO
    ): Response<ProductReviewDTO>

    /**
     * Обновляет существующий отзыв на продукт.
     *
     * @param productId ID продукта
     * @param id ID отзыва для обновления
     * @param review Данные для обновления отзыва
     * @return Response с Unit в случае успешного обновления
     */
    @PUT("api/products/{productId}/reviews/{id}")
    suspend fun updateReview(
        @Path("productId") productId: Int,
        @Path("id") id: Int,
        @Body review: ProductReviewUpdateDTO
    ): Response<Unit>

    /**
     * Удаляет отзыв на продукт.
     *
     * @param productId ID продукта
     * @param id ID отзыва для удаления
     * @return Response с Unit в случае успешного удаления
     */
    @DELETE("api/products/{productId}/reviews/{id}")
    suspend fun deleteReview(
        @Path("productId") productId: Int,
        @Path("id") id: Int
    ): Response<Unit>

    /**
     * Получает список всех заказов в системе.
     *
     * @return Response со списком OrderDTO
     */
    @GET("api/orders")
    suspend fun getOrders(): Response<List<OrderDTO>>

    /**
     * Получает информацию о конкретном заказе по ID.
     *
     * @param id ID заказа
     * @return Response с OrderDTO
     */
    @GET("api/orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): Response<OrderDTO>

    /**
     * Получает все заказы конкретного пользователя.
     *
     * @param userId ID пользователя
     * @return Response со списком OrderDTO
     */
    @GET("api/orders/user/{userId}")
    suspend fun getOrdersByUser(@Path("userId") userId: Int): Response<List<OrderDTO>>

    /**
     * Создаёт новый заказ в системе.
     *
     * @param order Данные для создания заказа
     * @return Response с созданным OrderDTO
     */
    @POST("api/orders")
    suspend fun createOrder(@Body order: OrderCreateDTO): Response<OrderDTO>

    /**
     * Обновляет информацию о существующем заказе.
     *
     * @param id ID заказа для обновления
     * @param order Данные для обновления заказа
     * @return Response с Unit в случае успешного обновления
     */
    @PUT("api/orders/{id}")
    suspend fun updateOrder(
        @Path("id") id: Int,
        @Body order: OrderUpdateDTO
    ): Response<Unit>

    /**
     * Удаляет заказ из системы.
     *
     * @param id ID заказа для удаления
     * @return Response с Unit в случае успешного удаления
     */
    @DELETE("api/orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<Unit>

    /**
     * Получает конкретный отзыв на продукт по ID.
     *
     * @param productId ID продукта
     * @param reviewId ID отзыва
     * @return Response с ProductReviewDTO
     */
    @GET("/api/products/{productId}/reviews/{id}")
    suspend fun getProductReview(
        @Path("productId") productId: Int,
        @Path("id") reviewId: Int
    ): Response<ProductReviewDTO>

    /**
     * Загружает изображение для отзыва на продукт.
     *
     * @param productId ID продукта
     * @param reviewId ID отзыва
     * @param file Multipart файл изображения
     * @return Response с Unit в случае успешной загрузки
     */
    @Multipart
    @POST("/api/products/{productId}/reviews/{id}/image")
    suspend fun uploadReviewImage(
        @Path("productId") productId: Int,
        @Path("id") reviewId: Int,
        @Part file: MultipartBody.Part
    ): Response<Unit>

    /**
     * Получает изображение отзыва.
     *
     * @param productId ID продукта
     * @param reviewId ID отзыва
     * @return Response с ResponseBody (данные изображения)
     */
    @GET("/api/products/{productId}/reviews/{id}/image")
    suspend fun getReviewImage(
        @Path("productId") productId: Int,
        @Path("id") reviewId: Int
    ): Response<ResponseBody>

    /**
     * Удаляет изображение отзыва.
     *
     * @param productId ID продукта
     * @param reviewId ID отзыва
     * @return Response с Unit в случае успешного удаления
     */
    @DELETE("/api/products/{productId}/reviews/{id}/image")
    suspend fun deleteReviewImage(
        @Path("productId") productId: Int,
        @Path("id") reviewId: Int
    ): Response<Unit>

    /**
     * Получает дневную статистику продаж за указанный период.
     *
     * @param days Количество дней для выборки (по умолчанию 30)
     * @return Response со списком DailySalesReportDTO
     */
    @GET("api/admin/reports/sales/daily")
    suspend fun getDailySales(
        @Query("days") days: Int = 30
    ): Response<List<DailySalesReportDTO>>

    /**
     * Получает отчёт о продажах за определённый период.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Response с PeriodSalesReportDTO
     */
    @GET("api/admin/reports/sales/period")
    suspend fun getPeriodSales(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<PeriodSalesReportDTO>

    /**
     * Получает помесячную статистику выручки.
     *
     * @param months Количество месяцев для выборки (по умолчанию 12)
     * @return Response со списком MonthlyRevenueDTO
     */
    @GET("api/admin/reports/revenue/monthly")
    suspend fun getMonthlyRevenue(
        @Query("months") months: Int = 12
    ): Response<List<MonthlyRevenueDTO>>

    /**
     * Получает список топовых продуктов по продажам.
     *
     * @param limit Максимальное количество продуктов в списке (по умолчанию 10)
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Response со списком TopProductDTO
     */
    @GET("api/admin/reports/top-products")
    suspend fun getTopProducts(
        @Query("limit") limit: Int = 10,
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<List<TopProductDTO>>

    /**
     * Получает статистику продаж по категориям.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Response со списком CategorySalesDTO
     */
    @GET("api/admin/reports/sales-by-category")
    suspend fun getSalesByCategory(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<List<CategorySalesDTO>>

    /**
     * Получает статистику по способам оплаты.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Response со списком PaymentMethodStatsDTO
     */
    @GET("api/admin/reports/payment-methods")
    suspend fun getPaymentMethodStats(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<List<PaymentMethodStatsDTO>>

    /**
     * Получает сводную статистику для админ-панели Dashboard.
     *
     * @return Response с DashboardSummaryDTO, содержащим основные метрики
     */
    @GET("api/admin/reports/dashboard")
    suspend fun getDashboardSummary(): Response<DashboardSummaryDTO>

    /**
     * Получает список уведомлений и алертов для админ-панели.
     *
     * @return Response со списком DashboardAlertDTO
     */
    @GET("api/admin/reports/alerts")
    suspend fun getDashboardAlerts(): Response<List<DashboardAlertDTO>>

    /**
     * Экспортирует список товаров в формате CSV.
     *
     * @return Response с ResponseBody (файл CSV)
     */
    @GET("api/admin/export/products/csv")
    @Streaming
    suspend fun exportProductsCsv(): Response<ResponseBody>

    /**
     * Экспортирует список товаров в формате Excel.
     *
     * @return Response с ResponseBody (файл Excel)
     */
    @GET("api/admin/export/products/excel")
    @Streaming
    suspend fun exportProductsExcel(): Response<ResponseBody>

    /**
     * Экспортирует заказы в формате CSV за указанный период.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Response с ResponseBody (файл CSV)
     */
    @GET("api/admin/export/orders/csv")
    @Streaming
    suspend fun exportOrdersCsv(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    /**
     * Экспортирует заказы в формате Excel за указанный период.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Response с ResponseBody (файл Excel)
     */
    @GET("api/admin/export/orders/excel")
    @Streaming
    suspend fun exportOrdersExcel(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    /**
     * Экспортирует отчёт по продажам в формате CSV за указанный период.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Response с ResponseBody (файл CSV)
     */
    @GET("api/admin/export/sales-report/csv")
    @Streaming
    suspend fun exportSalesReportCsv(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    /**
     * Экспортирует отчёт по продажам в формате Excel за указанный период.
     *
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Response с ResponseBody (файл Excel)
     */
    @GET("api/admin/export/sales-report/excel")
    @Streaming
    suspend fun exportSalesReportExcel(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    /**
     * Экспортирует топовые продукты в формате CSV за указанный период.
     *
     * @param limit Максимальное количество продуктов (по умолчанию 10)
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Response с ResponseBody (файл CSV)
     */
    @GET("api/admin/export/top-products/csv")
    @Streaming
    suspend fun exportTopProductsCsv(
        @Query("limit") limit: Int = 10,
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    /**
     * Экспортирует топовые продукты в формате Excel за указанный период.
     *
     * @param limit Максимальное количество продуктов (по умолчанию 10)
     * @param from Начальная дата периода (опционально)
     * @param to Конечная дата периода (опционально)
     * @return Response с ResponseBody (файл Excel)
     */
    @GET("api/admin/export/top-products/excel")
    @Streaming
    suspend fun exportTopProductsExcel(
        @Query("limit") limit: Int = 10,
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    /**
     * Экспортирует инвентаризацию (остатки товаров) в формате CSV.
     *
     * @return Response с ResponseBody (файл CSV)
     */
    @GET("api/admin/export/inventory/csv")
    @Streaming
    suspend fun exportInventoryCsv(): Response<ResponseBody>

    /**
     * Экспортирует инвентаризацию (остатки товаров) в формате Excel.
     *
     * @return Response с ResponseBody (файл Excel)
     */
    @GET("api/admin/export/inventory/excel")
    @Streaming
    suspend fun exportInventoryExcel(): Response<ResponseBody>

}
