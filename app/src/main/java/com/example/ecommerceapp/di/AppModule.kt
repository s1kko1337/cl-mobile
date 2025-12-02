package com.example.ecommerceapp.di

import android.content.Context
import androidx.room.Room
import com.example.ecommerceapp.BuildConfig
import com.example.ecommerceapp.data.local.AppDatabase
import com.example.ecommerceapp.data.local.CartDao
import com.example.ecommerceapp.data.local.UserPreferences
import com.example.ecommerceapp.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Модуль Dagger Hilt для настройки Dependency Injection всего приложения.
 *
 * Предоставляет singleton зависимости для:
 * - Сетевого слоя (Retrofit, OkHttp, ApiService)
 * - Локальной базы данных (Room, DAO)
 * - Preferences (DataStore)
 * - Аутентификационных перехватчиков
 *
 * Все зависимости имеют область видимости SingletonComponent.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Предоставляет singleton экземпляр UserPreferences для управления настройками пользователя.
     *
     * @param context Контекст приложения
     * @return Экземпляр UserPreferences
     */
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    /**
     * Предоставляет OkHttp перехватчик для автоматического добавления токена аутентификации в запросы.
     *
     * Получает токен из UserPreferences и добавляет его в заголовок Authorization
     * в формате "Bearer {token}" для всех HTTP запросов.
     *
     * @param userPrefs UserPreferences для получения токена
     * @return Interceptor для добавления токена аутентификации
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(userPrefs: UserPreferences): Interceptor {
        return Interceptor { chain ->
            val token = runBlocking { userPrefs.authToken.first() }
            val request = chain.request().newBuilder()
            if (!token.isNullOrEmpty()) {
                request.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(request.build())
        }
    }

    /**
     * Предоставляет настроенный OkHttpClient для выполнения HTTP запросов.
     *
     * Конфигурация включает:
     * - Перехватчик аутентификации (добавление Bearer токена)
     * - Логирование HTTP запросов/ответов (только в DEBUG режиме)
     * - Таймауты: 30 секунд для подключения, чтения и записи
     *
     * @param authInterceptor Перехватчик для добавления токена аутентификации
     * @return Настроенный OkHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Предоставляет настроенный Retrofit экземпляр для выполнения REST API запросов.
     *
     * Использует базовый URL из BuildConfig и Gson для сериализации/десериализации JSON.
     *
     * @param okHttpClient HTTP клиент с настроенными перехватчиками и таймаутами
     * @return Настроенный Retrofit экземпляр
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Предоставляет реализацию ApiService интерфейса для выполнения API запросов.
     *
     * @param retrofit Настроенный Retrofit экземпляр
     * @return ApiService для взаимодействия с REST API
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    /**
     * Предоставляет экземпляр Room базы данных приложения.
     *
     * База данных создаётся с именем "ecommerce_db" и включает миграцию с версии 1 на 2.
     *
     * @param context Контекст приложения
     * @return Настроенный AppDatabase экземпляр
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ecommerce_db"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    /**
     * Предоставляет DAO для работы с корзиной покупок.
     *
     * @param database Экземпляр базы данных приложения
     * @return CartDao для операций с корзиной
     */
    @Provides
    @Singleton
    fun provideCartDao(database: AppDatabase): CartDao {
        return database.cartDao()
    }
}