package com.example.ecommerceapp

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ECommerceApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeYandexMaps()
    }

    private fun initializeYandexMaps() {
        try {
            // Получаем API ключ из AndroidManifest.xml
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

            // Устанавливаем API ключ перед инициализацией
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