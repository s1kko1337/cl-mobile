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

data class MapAddressPickerState(
    val currentLocation: Point? = null,
    val selectedPoint: Point? = null,
    val selectedAddress: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MapAddressPickerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(MapAddressPickerState())
    val state = _state.asStateFlow()

    private val geocoder = Geocoder(context, Locale("ru"))
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                Log.d("MapViewModel", "Requesting current location...")

                // Сначала пробуем получить последнее известное местоположение
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
                            // Автоматически выбираем текущее местоположение
                            onMapClick(point)
                        } else {
                            // Если lastLocation null, запрашиваем активное обновление
                            Log.d("MapViewModel", "lastLocation is null, requesting current location...")
                            requestCurrentLocation()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MapViewModel", "Failed to get lastLocation", exception)
                        // Если не удалось получить lastLocation, пробуем активный запрос
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

    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation() {
        try {
            Log.d("MapViewModel", "Creating location request...")

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10000 // 10 секунд
            ).apply {
                setMaxUpdates(1) // Получить только одно обновление
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
                        // Автоматически выбираем текущее местоположение
                        onMapClick(point)

                        // Удаляем callback после получения местоположения
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

    override fun onCleared() {
        super.onCleared()
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

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

    private suspend fun getAddressFromCoordinates(point: Point): String {
        return withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    // Формируем читаемый адрес
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

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
