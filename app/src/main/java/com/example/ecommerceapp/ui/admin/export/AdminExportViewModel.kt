package com.example.ecommerceapp.ui.admin.export

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.AdminReportsRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Состояние экспорта данных администратором.
 *
 * @property isLoading Индикатор процесса экспорта
 * @property error Сообщение об ошибке экспорта
 * @property successMessage Сообщение об успешном экспорте
 * @property exportedFile Файл с экспортированными данными
 */
data class ExportState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val exportedFile: File? = null
)

/**
 * ViewModel для экспорта данных администратором.
 *
 * Управляет экспортом товаров, заказов и складских запасов
 * в форматах CSV и Excel с сохранением во внешнее хранилище.
 *
 * @property reportsRepository Репозиторий для получения данных экспорта
 * @property context Контекст приложения для доступа к файловой системе
 */
@HiltViewModel
class AdminExportViewModel @Inject constructor(
    private val reportsRepository: AdminReportsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ExportState())

    /** Реактивный поток состояния экспорта */
    val state = _state.asStateFlow()

    /** Экспортирует список товаров в формате CSV */
    fun exportProductsCsv() {
        exportFile("products", "csv") {
            reportsRepository.exportProductsCsv()
        }
    }

    /** Экспортирует список товаров в формате Excel */
    fun exportProductsExcel() {
        exportFile("products", "xlsx") {
            reportsRepository.exportProductsExcel()
        }
    }

    /** Экспортирует список заказов в формате CSV */
    fun exportOrdersCsv() {
        exportFile("orders", "csv") {
            reportsRepository.exportOrdersCsv(null, null)
        }
    }

    /** Экспортирует список заказов в формате Excel */
    fun exportOrdersExcel() {
        exportFile("orders", "xlsx") {
            reportsRepository.exportOrdersExcel(null, null)
        }
    }

    /** Экспортирует складские запасы в формате CSV */
    fun exportInventoryCsv() {
        exportFile("inventory", "csv") {
            reportsRepository.exportInventoryCsv()
        }
    }

    /** Экспортирует складские запасы в формате Excel */
    fun exportInventoryExcel() {
        exportFile("inventory", "xlsx") {
            reportsRepository.exportInventoryExcel()
        }
    }

    /**
     * Универсальный метод экспорта данных в файл.
     *
     * Выполняет запрос к серверу для получения данных,
     * сохраняет их в файл и обновляет состояние.
     *
     * @param name Базовое имя файла (например, "products", "orders")
     * @param extension Расширение файла ("csv" или "xlsx")
     * @param exportFunction Suspend-функция, выполняющая запрос к репозиторию
     */
    private fun exportFile(
        name: String,
        extension: String,
        exportFunction: suspend () -> Resource<ResponseBody>
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    successMessage = null,
                    exportedFile = null
                )
            }

            when (val result = exportFunction()) {
                is Resource.Success -> {
                    val responseBody = result.data
                    if (responseBody != null) {
                        val file = saveFile(responseBody, name, extension)
                        if (file != null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    successMessage = "Файл успешно экспортирован: ${file.name}",
                                    exportedFile = file
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Ошибка сохранения файла"
                                )
                            }
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Пустой ответ от сервера"
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Ошибка экспорта"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * Сохраняет ResponseBody в файл.
     *
     * Создаёт файл с уникальным именем на основе timestamp
     * во внешнем хранилище приложения и записывает в него данные.
     *
     * @param body Тело ответа от сервера с данными для сохранения
     * @param name Базовое имя файла
     * @param extension Расширение файла
     * @return Сохранённый файл или null в случае ошибки
     */
    private fun saveFile(
        body: ResponseBody,
        name: String,
        extension: String
    ): File? {
        return try {
            val timestamp = System.currentTimeMillis()
            val filename = "${name}_${timestamp}.${extension}"
            val downloadsDir = context.getExternalFilesDir(null)
            val file = File(downloadsDir, filename)

            FileOutputStream(file).use { outputStream ->
                body.byteStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Очищает сообщения об успехе или ошибке экспорта */
    fun clearMessage() {
        _state.update {
            it.copy(
                successMessage = null,
                error = null
            )
        }
    }
}
