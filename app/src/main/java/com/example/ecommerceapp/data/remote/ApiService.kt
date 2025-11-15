package com.example.ecommerceapp.data.remote

import com.example.ecommerceapp.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // Categories
    @GET("api/categories")
    suspend fun getCategories(): Response<List<CategoryDTO>>

    @GET("api/categories/{id}")
    suspend fun getCategory(@Path("id") id: Int): Response<CategoryDTO>

    @POST("api/categories")
    suspend fun createCategory(@Body category: CategoryCreateDTO): Response<CategoryDTO>

    @PUT("api/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body category: CategoryUpdateDTO
    ): Response<Unit>

    @DELETE("api/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Response<Unit>

    @GET("api/categories/{id}/products")
    suspend fun getCategoryProducts(@Path("id") id: Int): Response<List<ProductDTO>>

    // Products
    @GET("api/products")
    suspend fun getProducts(): Response<List<ProductDTO>>

    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductDTO>

    @POST("api/products")
    suspend fun createProduct(@Body product: ProductCreateDTO): Response<ProductDTO>

    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: ProductUpdateDTO
    ): Response<Unit>

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>

    // Product Images
    @GET("api/products/{productId}/images")
    suspend fun getProductImages(@Path("productId") productId: Int): Response<List<ProductImageDTO>>

    @Multipart
    @POST("api/products/{productId}/images")
    suspend fun uploadProductImage(
        @Path("productId") productId: Int,
        @Part file: MultipartBody.Part,
        @Part("altText") altText: RequestBody?
    ): Response<ProductImageDTO>

    @DELETE("api/products/{productId}/images/{id}")
    suspend fun deleteProductImage(
        @Path("productId") productId: Int,
        @Path("id") id: Int
    ): Response<Unit>


    @GET("api/products/{productId}/images/info")
    suspend fun getProductImagesInfo(@Path("productId") productId: Int): Response<List<ProductImageDTO>>

    @GET("api/products/{productId}/images/{id}/download")
    @Streaming
    suspend fun downloadProductImage(
        @Path("productId") productId: Int,
        @Path("id") imageId: Int
    ): Response<ResponseBody>

    @GET("api/products/{productId}/reviews/{id}/image/download")
    @Streaming
    suspend fun downloadReviewImage(
        @Path("productId") productId: Int,
        @Path("id") reviewId: Int
    ): Response<ResponseBody>

    // Product Reviews
    @GET("api/products/{productId}/reviews")
    suspend fun getProductReviews(@Path("productId") productId: Int): Response<List<ProductReviewDTO>>

    @POST("api/products/{productId}/reviews")
    suspend fun createReview(
        @Path("productId") productId: Int,
        @Body review: ProductReviewCreateDTO
    ): Response<ProductReviewDTO>

    @PUT("api/products/{productId}/reviews/{id}")
    suspend fun updateReview(
        @Path("productId") productId: Int,
        @Path("id") id: Int,
        @Body review: ProductReviewUpdateDTO
    ): Response<Unit>

    @DELETE("api/products/{productId}/reviews/{id}")
    suspend fun deleteReview(
        @Path("productId") productId: Int,
        @Path("id") id: Int
    ): Response<Unit>


    // Orders
    @GET("api/orders")
    suspend fun getOrders(): Response<List<OrderDTO>>

    @GET("api/orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): Response<OrderDTO>

    @GET("api/orders/user/{userId}")
    suspend fun getOrdersByUser(@Path("userId") userId: Int): Response<List<OrderDTO>>

    @POST("api/orders")
    suspend fun createOrder(@Body order: OrderCreateDTO): Response<OrderDTO>

    @PUT("api/orders/{id}")
    suspend fun updateOrder(
        @Path("id") id: Int,
        @Body order: OrderUpdateDTO
    ): Response<Unit>

    @DELETE("api/orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<Unit>

    // Product Reviews

    @GET("/api/products/{productId}/reviews/{id}")
    suspend fun getProductReview(
        @Path("productId") productId: Int,
        @Path("id") reviewId: Int
    ): Response<ProductReviewDTO>

    // Review Images
    @Multipart
    @POST("/api/products/{productId}/reviews/{id}/image")
    suspend fun uploadReviewImage(
        @Path("productId") productId: Int,
        @Path("id") reviewId: Int,
        @Part file: MultipartBody.Part
    ): Response<Unit>

    @GET("/api/products/{productId}/reviews/{id}/image")
    suspend fun getReviewImage(
        @Path("productId") productId: Int,
        @Path("id") reviewId: Int
    ): Response<ResponseBody>


    @DELETE("/api/products/{productId}/reviews/{id}/image")
    suspend fun deleteReviewImage(
        @Path("productId") productId: Int,
        @Path("id") reviewId: Int
    ): Response<Unit>

    // Admin Reports - Аналитика
    @GET("api/admin/reports/sales/daily")
    suspend fun getDailySales(
        @Query("days") days: Int = 30
    ): Response<List<DailySalesReportDTO>>

    @GET("api/admin/reports/sales/period")
    suspend fun getPeriodSales(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<PeriodSalesReportDTO>

    @GET("api/admin/reports/revenue/monthly")
    suspend fun getMonthlyRevenue(
        @Query("months") months: Int = 12
    ): Response<List<MonthlyRevenueDTO>>

    @GET("api/admin/reports/top-products")
    suspend fun getTopProducts(
        @Query("limit") limit: Int = 10,
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<List<TopProductDTO>>

    @GET("api/admin/reports/sales-by-category")
    suspend fun getSalesByCategory(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<List<CategorySalesDTO>>

    @GET("api/admin/reports/payment-methods")
    suspend fun getPaymentMethodStats(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<List<PaymentMethodStatsDTO>>

    // Admin Reports - Dashboard
    @GET("api/admin/reports/dashboard")
    suspend fun getDashboardSummary(): Response<DashboardSummaryDTO>

    @GET("api/admin/reports/alerts")
    suspend fun getDashboardAlerts(): Response<List<DashboardAlertDTO>>

    // Admin Export - Экспорт данных
    @GET("api/admin/export/products/csv")
    @Streaming
    suspend fun exportProductsCsv(): Response<ResponseBody>

    @GET("api/admin/export/products/excel")
    @Streaming
    suspend fun exportProductsExcel(): Response<ResponseBody>

    @GET("api/admin/export/orders/csv")
    @Streaming
    suspend fun exportOrdersCsv(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    @GET("api/admin/export/orders/excel")
    @Streaming
    suspend fun exportOrdersExcel(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    @GET("api/admin/export/sales-report/csv")
    @Streaming
    suspend fun exportSalesReportCsv(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    @GET("api/admin/export/sales-report/excel")
    @Streaming
    suspend fun exportSalesReportExcel(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    @GET("api/admin/export/top-products/csv")
    @Streaming
    suspend fun exportTopProductsCsv(
        @Query("limit") limit: Int = 10,
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    @GET("api/admin/export/top-products/excel")
    @Streaming
    suspend fun exportTopProductsExcel(
        @Query("limit") limit: Int = 10,
        @Query("from") from: String?,
        @Query("to") to: String?
    ): Response<ResponseBody>

    @GET("api/admin/export/inventory/csv")
    @Streaming
    suspend fun exportInventoryCsv(): Response<ResponseBody>

    @GET("api/admin/export/inventory/excel")
    @Streaming
    suspend fun exportInventoryExcel(): Response<ResponseBody>

}
