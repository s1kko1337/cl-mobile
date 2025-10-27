package com.example.ecommerceapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import java.io.File
import java.io.FileOutputStream

@Composable
fun ReviewDialog(
    productName: String,
    existingRating: Int? = null,
    existingComment: String? = null,
    existingImageUrl: String? = null,
    isEdit: Boolean = false,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String, imageFile: File?, deleteImage: Boolean) -> Unit
) {
    var rating by remember { mutableIntStateOf(existingRating ?: 5) }
    var comment by remember { mutableStateOf(existingComment ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var deleteExistingImage by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        deleteExistingImage = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isEdit) "Редактировать отзыв" else "Оставить отзыв")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Rating
                Column {
                    Text("Оценка", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 1..5) {
                            IconButton(
                                onClick = { rating = i },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = "Звезда $i",
                                    tint = if (i <= rating)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                // Comment
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Комментарий") },
                    placeholder = { Text("Расскажите о товаре...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                // Image section - only show in edit mode
                if (isEdit) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Фото (необязательно)", style = MaterialTheme.typography.labelMedium)

                        // Show existing image or new selected image
                        when {
                            selectedImageUri != null -> {
                                Box {
                                    Image(
                                        painter = rememberAsyncImagePainter(selectedImageUri),
                                        contentDescription = "Выбранное изображение",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline,
                                                RoundedCornerShape(8.dp)
                                            ),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { selectedImageUri = null },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Удалить изображение",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                            existingImageUrl != null && !deleteExistingImage -> {
                                Box {
                                    Image(
                                        painter = rememberAsyncImagePainter(existingImageUrl),
                                        contentDescription = "Текущее изображение",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline,
                                                RoundedCornerShape(8.dp)
                                            ),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { deleteExistingImage = true },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Удалить изображение",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                            else -> {
                                OutlinedButton(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Outlined.AddPhotoAlternate,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Добавить фото")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Only allow image file in edit mode
                    val imageFile = if (isEdit) {
                        selectedImageUri?.let { uri ->
                            try {
                                val inputStream = context.contentResolver.openInputStream(uri)
                                val file = File(context.cacheDir, "review_image_${System.currentTimeMillis()}.jpg")
                                val outputStream = FileOutputStream(file)
                                inputStream?.copyTo(outputStream)
                                inputStream?.close()
                                outputStream.close()
                                file
                            } catch (e: Exception) {
                                null
                            }
                        }
                    } else {
                        null
                    }
                    onSubmit(rating, comment, imageFile, deleteExistingImage)
                }
            ) {
                Text(if (isEdit) "Сохранить" else "Отправить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
