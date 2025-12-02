package com.example.ecommerceapp.ui.admin.products

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.CategoryDTO
import com.example.ecommerceapp.data.model.ProductDTO
import com.example.ecommerceapp.data.model.ProductImageDTO
import com.example.ecommerceapp.data.model.ProductUpdateDTO
import com.example.ecommerceapp.data.repository.CategoryRepository
import com.example.ecommerceapp.data.repository.ImageRepository
import com.example.ecommerceapp.data.repository.ProductRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * Состояние экрана редактирования товара администратором.
 *
 * @property isLoading Индикатор загрузки или сохранения данных
 * @property product Загруженный товар для редактирования
 * @property name Редактируемое название товара
 * @property description Редактируемое описание товара
 * @property price Редактируемая цена в виде строки
 * @property stockQuantity Редактируемое количество на складе в виде строки
 * @property sku Редактируемый артикул товара
 * @property categoryId ID выбранной категории
 * @property categories Список доступных категорий
 * @property images Список изображений товара
 * @property isUploadingImage Индикатор загрузки изображения
 * @property error Сообщение об ошибке (null если ошибок нет)
 * @property success Флаг успешного сохранения или удаления
 */
data class AdminProductEditState(
    val isLoading: Boolean = false,
    val product: ProductDTO? = null,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val stockQuantity: String = "",
    val sku: String = "",
    val categoryId: Int? = null,
    val categories: List<CategoryDTO> = emptyList(),
    val images: List<ProductImageDTO> = emptyList(),
    val isUploadingImage: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

/**
 * ViewModel для экрана редактирования товара администратором.
 *
 * Управляет загрузкой товара, редактированием его полей,
 * загрузкой и удалением изображений, обновлением и удалением товара.
 *
 * @property productRepository Репозиторий для работы с товарами
 * @property categoryRepository Репозиторий для работы с категориями
 * @property imageRepository Репозиторий для работы с изображениями
 */
@HiltViewModel
class AdminProductEditViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminProductEditState())

    /**
     * Реактивный поток состояния экрана редактирования товара.
     */
    val state: StateFlow<AdminProductEditState> = _state.asStateFlow()

    /**
     * Загружает товар для редактирования по его ID.
     *
     * Последовательно загружает данные товара, список категорий и изображения товара,
     * заполняет поля формы редактирования.
     *
     * @param id ID товара для редактирования
     */
    fun loadProduct(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val res = productRepository.getProduct(id)) {
                is Resource.Success -> {
                    val product = res.data!!
                    val categories = when (val catsRes = categoryRepository.getCategories()) {
                        is Resource.Success -> catsRes.data ?: emptyList()
                        else -> emptyList()
                    }
                    val images = when (val imgsRes = productRepository.getProductImages(id)) {
                        is Resource.Success -> imgsRes.data ?: emptyList()
                        else -> emptyList()
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            product = product,
                            name = product.name,
                            description = product.description ?: "",
                            price = product.price.toString(),
                            stockQuantity = product.stockQuantity.toString(),
                            sku = product.sku,
                            categoryId = product.categoryId,
                            categories = categories,
                            images = images,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = res.message ?: "Ошибка загрузки") }
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * Обновляет название товара в состоянии.
     *
     * @param value Новое название
     */
    fun onNameChange(value: String) {
        _state.update { it.copy(name = value) }
    }

    /**
     * Обновляет описание товара в состоянии.
     *
     * @param value Новое описание
     */
    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value) }
    }

    /**
     * Обновляет цену товара в состоянии.
     *
     * @param value Новая цена в виде строки
     */
    fun onPriceChange(value: String) {
        _state.update { it.copy(price = value) }
    }

    /**
     * Обновляет количество на складе в состоянии.
     *
     * @param value Новое количество в виде строки
     */
    fun onStockQuantityChange(value: String) {
        _state.update { it.copy(stockQuantity = value) }
    }

    /**
     * Обновляет выбранную категорию товара.
     *
     * @param categoryId ID новой категории
     */
    fun onCategorySelected(categoryId: Int) {
        _state.update { it.copy(categoryId = categoryId) }
    }

    /**
     * Обновляет артикул товара в состоянии.
     *
     * @param value Новый артикул
     */
    fun onSkuChange(value: String) {
        _state.update { it.copy(sku = value) }
    }

    /**
     * Загружает изображение для товара.
     *
     * После успешной загрузки обновляет список изображений
     * и очищает кэш изображений.
     *
     * @param productId ID товара
     * @param imageFile Файл изображения для загрузки
     * @param altText Альтернативный текст для изображения (необязательно)
     */
    fun uploadImage(productId: Int, imageFile: File, altText: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingImage = true, error = null) }

            when (val res = productRepository.uploadProductImage(productId, imageFile, altText)) {
                is Resource.Success -> {
                    val updatedImages = _state.value.images + res.data!!
                    _state.update {
                        it.copy(
                            isUploadingImage = false,
                            images = updatedImages,
                            error = null
                        )
                    }
                    imageRepository.clearCache()
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isUploadingImage = false,
                            error = res.message ?: "Ошибка загрузки изображения"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * Удаляет изображение товара.
     *
     * После успешного удаления обновляет список изображений
     * и удаляет изображение из кэша.
     *
     * @param productId ID товара
     * @param imageId ID изображения для удаления
     */
    fun deleteImage(productId: Int, imageId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(error = null) }

            when (val res = productRepository.deleteProductImage(productId, imageId)) {
                is Resource.Success -> {
                    val updatedImages = _state.value.images.filter { it.id != imageId }
                    _state.update { it.copy(images = updatedImages) }
                    imageRepository.removeCachedImage("product_${productId}_$imageId")
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(error = res.message ?: "Ошибка удаления изображения")
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    /**
     * Обновляет данные товара на сервере.
     *
     * Валидирует заполнение всех обязательных полей,
     * преобразует строковые значения в числовые типы,
     * отправляет обновлённые данные на сервер.
     *
     * @param id ID товара для обновления
     */
    fun updateProduct(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val st = _state.value
            val price = st.price.toDoubleOrNull()
            val stockQuantity = st.stockQuantity.toIntOrNull()
            val catId = st.categoryId

            if (st.name.isBlank() || price == null || stockQuantity == null || catId == null) {
                _state.update { it.copy(isLoading = false, error = "Проверьте заполнение всех полей") }
                return@launch
            }

            val updateDto = ProductUpdateDTO(
                name = st.name,
                description = st.description,
                price = price,
                stockQuantity = stockQuantity,
                categoryId = catId,
                sku = st.sku
            )

            when (val res = productRepository.updateProduct(id, updateDto)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, success = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = res.message ?: "Ошибка обновления") }
                }
                else -> {}
            }
        }
    }

    /**
     * Удаляет товар из системы.
     *
     * @param id ID товара для удаления
     */
    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val res = productRepository.deleteProduct(id)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, success = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = res.message ?: "Ошибка удаления") }
                }
                else -> {}
            }
        }
    }
}