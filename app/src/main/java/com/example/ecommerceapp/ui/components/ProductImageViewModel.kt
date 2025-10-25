package com.example.ecommerceapp.ui.components

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.ImageRepository
import com.example.ecommerceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val imageStates = mutableMapOf<String, MutableStateFlow<Resource<Bitmap>?>>()

    fun getImage(productId: Int, imageId: Int): StateFlow<Resource<Bitmap>?> {
        val key = "product_${productId}_$imageId"

        return imageStates.getOrPut(key) {
            MutableStateFlow<Resource<Bitmap>?>(Resource.Loading()).also { flow ->
                viewModelScope.launch {
                    flow.value = imageRepository.getProductImage(productId, imageId)
                }
            }
        }
    }

    fun getReviewImage(productId: Int, reviewId: Int): StateFlow<Resource<Bitmap>?> {
        val key = "review_${productId}_$reviewId"

        return imageStates.getOrPut(key) {
            MutableStateFlow<Resource<Bitmap>?>(Resource.Loading()).also { flow ->
                viewModelScope.launch {
                    flow.value = imageRepository.getReviewImage(productId, reviewId)
                }
            }
        }
    }
}