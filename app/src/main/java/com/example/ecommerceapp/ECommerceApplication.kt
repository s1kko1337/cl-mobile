package com.example.ecommerceapp

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

/**
 * Главный класс Application для приложения электронной коммерции.
 *
 * Инициализирует Hilt для dependency injection и настраивает
 * Yandex MapKit для работы с картами при выборе адреса доставки.
 */
@HiltAndroidApp
class ECommerceApplication : Application() {

    /**
     * Вызывается при создании приложения.
     *
     * Инициализирует Yandex Maps SDK для использования в приложении.
     */
    override fun onCreate() {
        super.onCreate()
        initializeYandexMaps()
    }

    /**
     * Инициализирует Yandex MapKit SDK.
     *
     * Извлекает API ключ из AndroidManifest.xml и настраивает MapKit.
     * В случае ошибки логирует сообщение, но не прерывает работу приложения.
     */
    private fun initializeYandexMaps() {
        try {
            val appInfo = packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            )
            val apiKey = appInfo.metaData?.getString("com.yandex.maps.MAPKIT_API_KEY")

            if (apiKey.isNullOrEmpty()) {
                Log.e("ECommerceApp", "Yandex Maps API key is missing in AndroidManifest.xml!")
                return
            }

            Log.d("ECommerceApp", "Initializing Yandex Maps with API key: ${apiKey.take(10)}...")

            MapKitFactory.setApiKey(apiKey)
            MapKitFactory.initialize(this)

            Log.d("ECommerceApp", "Yandex Maps initialized successfully")
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("ECommerceApp", "Failed to get application info", e)
        } catch (e: Exception) {
            Log.e("ECommerceApp", "Failed to initialize Yandex Maps", e)
        }
    }
}