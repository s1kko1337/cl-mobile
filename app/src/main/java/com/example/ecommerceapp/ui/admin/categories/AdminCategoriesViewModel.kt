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

/**
 * Состояние экрана управления категориями для администратора.
 *
 * @property categories Список всех категорий
 * @property isLoading Индикатор загрузки данных
 * @property error Сообщение об ошибке (null если ошибок нет)
 * @property showAddDialog Флаг отображения диалога создания категории
 * @property editingCategory Категория для редактирования (null если не редактируем)
 */
data class AdminCategoriesState(
    val categories: List<CategoryDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val editingCategory: CategoryDTO? = null
)

/**
 * ViewModel для экрана управления категориями администратором.
 *
 * Управляет списком категорий, их созданием, обновлением и удалением.
 *
 * @property categoryRepository Репозиторий для работы с категориями
 */
@HiltViewModel
class AdminCategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminCategoriesState())

    /**
     * Реактивный поток состояния экрана управления категориями.
     */
    val state = _state.asStateFlow()

    init {
        loadCategories()
    }

    /**
     * Загружает список всех категорий с сервера.
     */
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

    /**
     * Открывает диалог создания новой категории.
     */
    fun showAddDialog() {
        _state.update { it.copy(showAddDialog = true, editingCategory = null) }
    }

    /**
     * Открывает диалог редактирования категории.
     *
     * @param category Категория для редактирования
     */
    fun showEditDialog(category: CategoryDTO) {
        _state.update { it.copy(editingCategory = category) }
    }

    /**
     * Закрывает диалог создания/редактирования категории.
     */
    fun hideDialog() {
        _state.update { it.copy(showAddDialog = false, editingCategory = null) }
    }

    /**
     * Создаёт новую категорию.
     *
     * После успешного создания обновляет список категорий и закрывает диалог.
     *
     * @param name Название категории
     * @param description Описание категории (необязательно)
     */
    fun createCategory(name: String, description: String?) {
        viewModelScope.launch {
            val category = CategoryCreateDTO(name, description)
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

    /**
     * Обновляет существующую категорию.
     *
     * После успешного обновления обновляет список категорий и закрывает диалог.
     *
     * @param id ID категории для обновления
     * @param name Новое название (необязательно)
     * @param description Новое описание (необязательно)
     */
    fun updateCategory(id: Int, name: String?, description: String?) {
        viewModelScope.launch {
            val category = CategoryUpdateDTO(name, description)
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

    /**
     * Удаляет категорию по её ID.
     *
     * После успешного удаления обновляет список категорий.
     *
     * @param id ID категории для удаления
     */
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

    /**
     * Обновляет список категорий.
     */
    fun refresh() {
        loadCategories()
    }
}