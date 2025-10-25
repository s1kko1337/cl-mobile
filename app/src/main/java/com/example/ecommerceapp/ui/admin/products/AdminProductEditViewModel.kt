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

@HiltViewModel
class AdminProductEditViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminProductEditState())
    val state: StateFlow<AdminProductEditState> = _state.asStateFlow()

    fun loadProduct(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // load product
            when (val res = productRepository.getProduct(id)) {
                is Resource.Success -> {
                    val product = res.data!!
                    // load categories
                    val categories = when (val catsRes = categoryRepository.getCategories()) {
                        is Resource.Success -> catsRes.data ?: emptyList()
                        else -> emptyList()
                    }
                    // load images
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

    fun onNameChange(value: String) {
        _state.update { it.copy(name = value) }
    }

    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value) }
    }

    fun onPriceChange(value: String) {
        _state.update { it.copy(price = value) }
    }

    fun onStockQuantityChange(value: String) {
        _state.update { it.copy(stockQuantity = value) }
    }

    fun onCategorySelected(categoryId: Int) {
        _state.update { it.copy(categoryId = categoryId) }
    }

    fun onSkuChange(value: String) {
        _state.update { it.copy(sku = value) }
    }

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