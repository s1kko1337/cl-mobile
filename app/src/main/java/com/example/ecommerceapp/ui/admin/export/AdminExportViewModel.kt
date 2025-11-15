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

data class ExportState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val exportedFile: File? = null
)

@HiltViewModel
class AdminExportViewModel @Inject constructor(
    private val reportsRepository: AdminReportsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ExportState())
    val state = _state.asStateFlow()

    fun exportProductsCsv() {
        exportFile("products", "csv") {
            reportsRepository.exportProductsCsv()
        }
    }

    fun exportProductsExcel() {
        exportFile("products", "xlsx") {
            reportsRepository.exportProductsExcel()
        }
    }

    fun exportOrdersCsv() {
        exportFile("orders", "csv") {
            reportsRepository.exportOrdersCsv(null, null)
        }
    }

    fun exportOrdersExcel() {
        exportFile("orders", "xlsx") {
            reportsRepository.exportOrdersExcel(null, null)
        }
    }

    fun exportInventoryCsv() {
        exportFile("inventory", "csv") {
            reportsRepository.exportInventoryCsv()
        }
    }

    fun exportInventoryExcel() {
        exportFile("inventory", "xlsx") {
            reportsRepository.exportInventoryExcel()
        }
    }

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
                is Resource.Loading -> {
                    // Уже в loading
                }
            }
        }
    }

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

    fun clearMessage() {
        _state.update {
            it.copy(
                successMessage = null,
                error = null
            )
        }
    }
}
