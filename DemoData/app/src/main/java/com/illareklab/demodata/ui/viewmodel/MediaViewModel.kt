package com.illareklab.demodata.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.illareklab.demodata.data.local.FileStorageManager
import com.illareklab.demodata.data.local.entity.MediaEntity
import com.illareklab.demodata.data.repository.MediaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class MediaViewModel(
    private val mediaRepository: MediaRepository,
    private val fileStorage: FileStorageManager
) : ViewModel() {

    val mediaItems = mediaRepository.allMedia.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val photoCount = mediaRepository.photoCount.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        0
    )

    val videoCount = mediaRepository.videoCount.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        0
    )

    fun preparePhotoFile(): File {
        return fileStorage.newPhotoFile()
    }

    fun prepareVideoFile(): File {
        return fileStorage.newVideoFile()
    }

    fun registerPhoto(file: File) {
        viewModelScope.launch {
            if (file.exists() && file.length() > 0) {
                mediaRepository.registerPhoto(
                    filePath = file.absolutePath,
                    widthPx = 0,
                    heightPx = 0
                )
            }
        }
    }

    fun registerVideo(file: File) {
        viewModelScope.launch {
            if (file.exists() && file.length() > 0) {

                mediaRepository.registerVideo(
                    filePath = file.absolutePath,
                    durationMs = 0L
                )
            }
        }
    }

    fun deleteMedia(item: MediaEntity) {
        viewModelScope.launch {
            mediaRepository.delete(item)
        }
    }
}

class MediaViewModelFactory(
    private val mediaRepository: MediaRepository,
    private val fileStorage: FileStorageManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            return MediaViewModel(mediaRepository, fileStorage) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}
