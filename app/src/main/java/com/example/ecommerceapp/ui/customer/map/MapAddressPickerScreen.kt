package com.example.ecommerceapp.ui.customer.map

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView

/**
 * Экран выбора адреса доставки на карте.
 *
 * Интегрирует Yandex MapKit для выбора точки доставки на карте.
 * Запрашивает разрешения на геолокацию, определяет текущее местоположение,
 * преобразует координаты в текстовый адрес с помощью Geocoder.
 *
 * @param onNavigateBack Callback для возврата на предыдущий экран
 * @param onAddressSelected Callback с выбранным адресом и координатами
 * @param viewModel ViewModel для управления картой и геолокацией
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapAddressPickerScreen(
    onNavigateBack: () -> Unit,
    onAddressSelected: (String, Double, Double) -> Unit,
    viewModel: MapAddressPickerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState()

    var mapView: MapView? by remember { mutableStateOf(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.values.any { it }
        if (hasLocationPermission) {
            viewModel.getCurrentLocation()
        }
    }

    // Запрашиваем разрешение при первом запуске
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.getCurrentLocation()
        }
    }

    // Обновляем камеру когда получаем местоположение
    LaunchedEffect(state.currentLocation) {
        state.currentLocation?.let { point ->
            mapView?.mapWindow?.map?.move(
                CameraPosition(point, 16.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выберите адрес на карте") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    // Кнопка моего местоположения
                    IconButton(
                        onClick = {
                            if (hasLocationPermission) {
                                viewModel.getCurrentLocation()
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Моё местоположение")
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.selectedAddress != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        state.selectedAddress?.let { address ->
                            state.selectedPoint?.let { point ->
                                onAddressSelected(address, point.latitude, point.longitude)
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Check, contentDescription = null) },
                    text = { Text("Выбрать адрес") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Карта
            AndroidView(
                factory = { ctx ->
                    Log.d("MapScreen", "Creating MapView")
                    MapView(ctx).apply {
                        Log.d("MapScreen", "MapView created, setting initial position")

                        // Включаем hardware acceleration для лучшей производительности
                        setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                        Log.d("MapScreen", "Hardware layer set")

                        // Начальная позиция - Саратов (ваше текущее местоположение)
                        val initialPoint = Point(51.5298083, 46.0560933)
                        try {
                            mapWindow.map.move(
                                CameraPosition(initialPoint, 14.0f, 0.0f, 0.0f)
                            )
                            Log.d("MapScreen", "Initial camera position set to Saratov")

                            // Логируем информацию о карте
                            Log.d("MapScreen", "Map logo: ${mapWindow.map.logo}")
                            Log.d("MapScreen", "Map is valid: ${mapWindow.map.isValid}")
                        } catch (e: Exception) {
                            Log.e("MapScreen", "Failed to set initial position", e)
                        }

                        // Обработчик кликов на карте
                        mapWindow.map.addInputListener(object : InputListener {
                            override fun onMapTap(map: Map, point: Point) {
                                Log.d("MapScreen", "Map tapped at: ${point.latitude}, ${point.longitude}")
                                viewModel.onMapClick(point)
                            }

                            override fun onMapLongTap(map: Map, point: Point) {
                                Log.d("MapScreen", "Map long tapped")
                            }
                        })

                        // Сохраняем ссылку на MapView
                        mapView = this

                        // Запускаем MapView сразу после создания
                        try {
                            onStart()
                            Log.d("MapScreen", "MapView.onStart() called, map should start loading tiles")
                        } catch (e: Exception) {
                            Log.e("MapScreen", "Failed to call onStart()", e)
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Lifecycle управление MapView - привязано к lifecycle композабла
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    Log.d("MapScreen", "Lifecycle event: $event")
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> {
                            mapView?.let { view ->
                                try {
                                    view.onStart()
                                    Log.d("MapScreen", "MapView.onStart() called on ON_RESUME")
                                } catch (e: Exception) {
                                    Log.e("MapScreen", "Failed to call onStart() on ON_RESUME", e)
                                }
                            }
                        }
                        Lifecycle.Event.ON_PAUSE -> {
                            mapView?.let { view ->
                                try {
                                    view.onStop()
                                    Log.d("MapScreen", "MapView.onStop() called on ON_PAUSE")
                                } catch (e: Exception) {
                                    Log.e("MapScreen", "Failed to call onStop() on ON_PAUSE", e)
                                }
                            }
                        }
                        else -> {}
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    Log.d("MapScreen", "DisposableEffect onDispose called")
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    mapView?.let { view ->
                        try {
                            view.onStop()
                            Log.d("MapScreen", "MapView.onStop() called on dispose")
                        } catch (e: Exception) {
                            Log.e("MapScreen", "Failed to call onStop() on dispose", e)
                        }
                    }
                    mapView = null
                }
            }

            // Индикатор загрузки
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }

            // Карточка с адресом
            state.selectedAddress?.let { address ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Выбранный адрес:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = address,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Ошибка
            state.error?.let { error ->
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}
