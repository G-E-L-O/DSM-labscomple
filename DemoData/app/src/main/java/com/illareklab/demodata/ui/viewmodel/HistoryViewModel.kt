package com.illareklab.demodata.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.illareklab.demodata.data.local.FileStorageManager
import com.illareklab.demodata.data.repository.AudioRepository
import com.illareklab.demodata.data.repository.GpsRepository
import com.illareklab.demodata.data.repository.MediaRepository
import com.illareklab.demodata.model.ActivityItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class HistoryViewModel(
    private val gpsRepository: GpsRepository,
    private val mediaRepository: MediaRepository,
    private val audioRepository: AudioRepository,
    private val fileStorage: FileStorageManager
) : ViewModel() {

    val historyItems: StateFlow<List<ActivityItem>> = combine(
        gpsRepository.googlePoints,
        gpsRepository.sensorsPoints,
        mediaRepository.allMedia,
        audioRepository.allAudios
    ) { googleList, sensorsList, mediaList, audioList ->

        val unifiedList = mutableListOf<ActivityItem>()

        googleList.forEach { entity ->
            unifiedList.add(
                ActivityItem.GpsGoogle(
                    id = entity.id,
                    timestamp = entity.timestamp,
                    latitud = entity.latitud,
                    longitud = entity.longitud
                )
            )
        }

        sensorsList.forEach { entity ->
            unifiedList.add(
                ActivityItem.GpsSensors(
                    id = entity.id,
                    timestamp = entity.timestamp,
                    latitud = entity.latitud,
                    longitud = entity.longitud
                )
            )
        }

        mediaList.forEach { entity ->
            if (entity.tipo == "PHOTO") {
                unifiedList.add(
                    ActivityItem.Photo(
                        id = entity.id,
                        timestamp = entity.timestamp,
                        rutaArchivo = entity.rutaArchivo
                    )
                )
            } else {
                unifiedList.add(
                    ActivityItem.Video(
                        id = entity.id,
                        timestamp = entity.timestamp,
                        rutaArchivo = entity.rutaArchivo,
                        duracionMs = entity.duracionMs ?: 0L
                    )
                )
            }
        }

        audioList.forEach { entity ->
            unifiedList.add(
                ActivityItem.Audio(
                    id = entity.id,
                    timestamp = entity.timestamp,
                    rutaArchivo = entity.rutaArchivo,
                    duracionMs = entity.duracionMs,
                    formato = entity.formato
                )
            )
        }

        unifiedList.sortByDescending { it.timestamp }
        unifiedList
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun exportToCsv(onResult: (File) -> Unit) {
        viewModelScope.launch {
            val items = historyItems.value
            val csvBuilder = StringBuilder("Timestamp,Tipo,Ruta/Coordenadas\n")

            items.forEach { item ->
                val type = item.javaClass.simpleName
                val detail = when (item) {
                    is ActivityItem.GpsGoogle -> "${item.latitud};${item.longitud}"
                    is ActivityItem.GpsSensors -> "${item.latitud};${item.longitud}"
                    is ActivityItem.Photo -> item.rutaArchivo
                    is ActivityItem.Video -> item.rutaArchivo
                    is ActivityItem.Audio -> item.rutaArchivo
                }
                csvBuilder.append("${item.timestamp},$type,$detail\n")
            }

            val file = fileStorage.saveCsvExport(csvBuilder.toString())
            onResult(file)
        }
    }

    class Factory(
        private val gpsRepository: GpsRepository,
        private val mediaRepository: MediaRepository,
        private val audioRepository: AudioRepository,
        private val fileStorage: FileStorageManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                return HistoryViewModel(gpsRepository, mediaRepository, audioRepository, fileStorage) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
        }
    }
}
