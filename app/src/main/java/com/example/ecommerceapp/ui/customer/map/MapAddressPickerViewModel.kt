package com.example.ecommerceapp.ui.customer.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

/**
 * Состояние экрана выбора адреса на карте.
 *
 * @property currentLocation Текущее местоположение пользователя
 * @property selectedPoint Выбранная точка на карте
 * @property selectedAddress Текстовый адрес выбранной точки
 * @property isLoading Индикатор загрузки данных о местоположении
 * @property error Сообщение об ошибке (null если ошибок нет)
 */
data class MapAddressPickerState(
    val currentLocation: Point? = null,
    val selectedPoint: Point? = null,
    val selectedAddress: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel для экрана выбора адреса на карте.
 *
 * Управляет получением текущего местоположения пользователя,
 * выбором точки на карте и преобразованием координат в читаемый адрес
 * с использованием Geocoder и Google Location Services.
 *
 * @property context Контекст приложения для доступа к сервисам геолокации
 */
@HiltViewModel
class MapAddressPickerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(MapAddressPickerState())

    /**
     * Реактивный поток состояния экрана выбора адреса.
     */
    val state = _state.asStateFlow()

    private val geocoder = Geocoder(context, Locale("ru"))
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    /**
     * Получает текущее местоположение пользователя.
     *
     * Сначала пытается получить последнее известное местоположение,
     * если оно недоступно - запрашивает активное обновление.
     * После получения координат автоматически вызывает onMapClick
     * для определения адреса.
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                Log.d("MapViewModel", "Requesting current location...")

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            Log.d("MapViewModel", "Got lastLocation: ${location.latitude}, ${location.longitude}")
                            val point = Point(location.latitude, location.longitude)
                            _state.update { state ->
                                state.copy(
                                    currentLocation = point,
                                    isLoading = false
                                )
                            }
                            onMapClick(point)
                        } else {
                            Log.d("MapViewModel", "lastLocation is null, requesting current location...")
                            requestCurrentLocation()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MapViewModel", "Failed to get lastLocation", exception)
                        requestCurrentLocation()
                    }
            } catch (e: SecurityException) {
                Log.e("MapViewModel", "Security exception", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Нет разрешения на доступ к геолокации. Предоставьте разрешение в настройках."
                    )
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Unexpected exception", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Неожиданная ошибка: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Запрашивает активное обновление текущего местоположения.
     *
     * Создаёт запрос на одно обновление с высокой точностью.
     * После получения местоположения автоматически вызывает onMapClick
     * и удаляет callback обновлений.
     */
    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation() {
        try {
            Log.d("MapViewModel", "Creating location request...")

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10000
            ).apply {
                setMaxUpdates(1)
                setWaitForAccurateLocation(false)
            }.build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    Log.d("MapViewModel", "Got location result: ${result.locations.size} locations")

                    result.locations.firstOrNull()?.let { location ->
                        Log.d("MapViewModel", "Location: ${location.latitude}, ${location.longitude}")
                        val point = Point(location.latitude, location.longitude)
                        _state.update { state ->
                            state.copy(
                                currentLocation = point,
                                isLoading = false
                            )
                        }
                        onMapClick(point)

                        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
                    } ?: run {
                        Log.w("MapViewModel", "Location result is empty")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Не удалось определить местоположение. Проверьте настройки GPS."
                            )
                        }
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            ).addOnFailureListener { exception ->
                Log.e("MapViewModel", "Failed to request location updates", exception)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка запроса местоположения: ${exception.message}"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("MapViewModel", "Exception in requestCurrentLocation", e)
            _state.update {
                it.copy(
                    isLoading = false,
                    error = "Ошибка: ${e.message}"
                )
            }
        }
    }

    /**
     * Освобождает ресурсы при уничтожении ViewModel.
     *
     * Удаляет активные обновления местоположения.
     */
    override fun onCleared() {
        super.onCleared()
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    /**
     * Обрабатывает клик пользователя на карте.
     *
     * Определяет текстовый адрес для выбранной точки
     * и обновляет состояние с новыми координатами и адресом.
     *
     * @param point Точка на карте, выбранная пользователем
     */
    fun onMapClick(point: Point) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val address = getAddressFromCoordinates(point)
                _state.update {
                    it.copy(
                        selectedPoint = point,
                        selectedAddress = address,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось определить адрес: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Преобразует географические координаты в текстовый адрес.
     *
     * Использует Geocoder для получения адреса по координатам.
     * Формирует читаемое представление адреса из компонентов
     * (улица, дом, город, регион, страна).
     * При невозможности определить адрес возвращает координаты в текстовом виде.
     *
     * @param point Точка с координатами для преобразования
     * @return Текстовое представление адреса или координаты
     */
    private suspend fun getAddressFromCoordinates(point: Point): String {
        return withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    buildString {
                        address.thoroughfare?.let { append(it) }
                        address.subThoroughfare?.let {
                            if (isNotEmpty()) append(", ")
                            append(it)
                        }
                        address.locality?.let {
                            if (isNotEmpty()) append(", ")
                            append(it)
                        }
                        address.adminArea?.let {
                            if (isNotEmpty()) append(", ")
                            append(it)
                        }
                        address.countryName?.let {
                            if (isNotEmpty()) append(", ")
                            append(it)
                        }
                    }.takeIf { it.isNotEmpty() }
                        ?: "${point.latitude}, ${point.longitude}"
                } else {
                    "${point.latitude}, ${point.longitude}"
                }
            } catch (e: Exception) {
                "${point.latitude}, ${point.longitude}"
            }
        }
    }

    /**
     * Очищает сообщение об ошибке в состоянии.
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
