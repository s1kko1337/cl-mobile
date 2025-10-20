package com.example.ecommerceapp.ui.admin.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.model.CategoryCreateDTO
import com.example.ecommerceapp.data.model.CategoryDTO
import com.example.ecommerceapp.data.model.CategoryUpdateDTO
import com.example.ecommerceapp.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.ecommerceapp.util.Resource


data class AdminCategoriesState(
    val categories: List<CategoryDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val editingCategory: CategoryDTO? = null
)

@HiltViewModel
class AdminCategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminCategoriesState())
    val state = _state.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = categoryRepository.getCategories()) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            categories = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun showAddDialog() {
        _state.update { it.copy(showAddDialog = true, editingCategory = null) }
    }

    fun showEditDialog(category: CategoryDTO) {
        _state.update { it.copy(editingCategory = category) }
    }

    fun hideDialog() {
        _state.update { it.copy(showAddDialog = false, editingCategory = null) }
    }

    fun createCategory(name: String, description: String?, imageUrl: String?) {
        viewModelScope.launch {
            val category = CategoryCreateDTO(name, description, imageUrl)
            when (val result = categoryRepository.createCategory(category)) {
                is Resource.Success -> {
                    loadCategories()
                    hideDialog()
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message ?: "Ошибка создания категории") }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun updateCategory(id: Int, name: String?, description: String?, imageUrl: String?) {
        viewModelScope.launch {
            val category = CategoryUpdateDTO(name, description, imageUrl)
            when (val result = categoryRepository.updateCategory(id, category)) {
                is Resource.Success -> {
                    loadCategories()
                    hideDialog()
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message ?: "Ошибка обновления категории") }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            when (val result = categoryRepository.deleteCategory(id)) {
                is Resource.Success -> {
                    loadCategories()
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message ?: "Ошибка удаления категории") }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun refresh() {
        loadCategories()
    }
}